package com.qianmi.b2b.scheduling.acceptor.jms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qianmi.b2b.commons.utils.stream.StreamUtils;
import com.qianmi.b2b.scheduling.acceptor.common.req.*;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskDestination;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskExtra;
import com.qianmi.b2b.scheduling.core.supports.json.JsonUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import javax.jms.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2022-03-25 14:19.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@UtilityClass
public class JmsTaskExtractor {

    private static final Map<Class<? extends TaskRequest>, Booster> CUSTOM_BOOST = new ConcurrentHashMap<>();

    private interface Booster {

        void boost(@NotNull Message message, @NotNull TaskRequest request) throws JMSException;
    }

    static {
        CUSTOM_BOOST.put(
                TaskSubmitRequest.class,
                (message, request) -> boostTaskSubmitRequest(message, (TaskSubmitRequest) request)
        );

        CUSTOM_BOOST.put(TaskAcceptRequest.class, (message, request) -> {
            @SuppressWarnings("unchecked")
            final TaskAcceptRequest<TaskRequest> acceptRequest = (TaskAcceptRequest<TaskRequest>) request;
            final TaskRequest taskRequest = acceptRequest.getRequestPayload();
            boost(message, taskRequest);
        });
    }

    @NotNull
    public <T extends TaskRequest> T extract(@NotNull final Message message, @NotNull final Class<T> clazz)
            throws JMSException {
        if (message instanceof ObjectMessage) {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final Serializable serializable = objectMessage.getObject();
            if (clazz.isAssignableFrom(serializable.getClass())) {
                @SuppressWarnings("unchecked") final T req = (T) serializable;
                return req;
            }
        }

        final TypeReference<? extends TaskRequest> typeReference = deduceTypeReference(message, clazz);
        final T request = extractByType(message, clazz, typeReference);

        boost(message, request);

        return request;
    }

    private TypeReference<? extends TaskRequest> deduceTypeReference(
            @NotNull final Message message, @NotNull final Class<? extends TaskRequest> clazz) throws JMSException {
        if (clazz.isAssignableFrom(TaskAcceptRequest.class)) {
            final String json = asJson(message, clazz).orElse(null);
            if (StringUtils.isBlank(json)) {
                throw new JmsTaskException("消息内容不能为空");
            }
            final Map<?, ?> jsonMap = Optional.ofNullable(JsonUtils.parseObject(json, Map.class))
                    .orElse(Collections.emptyMap());
            final Object requestType = jsonMap.get(TaskAcceptRequest.Fields.requestType);
            if (requestType instanceof String) {
                final TaskAcceptType acceptType = TaskAcceptType.of((String) requestType).orElse(null);
                if (Objects.nonNull(acceptType)) {
                    switch (acceptType) {
                        case SUBMIT:
                            return new TypeReference<TaskAcceptRequest<TaskSubmitRequest>>() {
                            };
                        case CANCEL:
                            return new TypeReference<TaskAcceptRequest<TaskCancelRequest>>() {
                            };
                        case RESET:
                            return new TypeReference<TaskAcceptRequest<TaskResetRequest>>() {
                            };
                    }
                }
            }
            throw new JmsTaskException("任务提交类型值不合法");
        } else {
            return new TypeReference<>() {
                @Override
                public Type getType() {
                    return clazz;
                }
            };
        }
    }

    private void boost(@NotNull final Message message, @NotNull final TaskRequest request) throws JMSException {
        for (Map.Entry<Class<? extends TaskRequest>, Booster> classBoosterEntry : CUSTOM_BOOST.entrySet()) {
            final Class<? extends TaskRequest> clazz = classBoosterEntry.getKey();
            if (clazz.isInstance(request)) {
                final Booster booster = classBoosterEntry.getValue();
                booster.boost(message, request);
            }
        }
    }

    private void boostTaskSubmitRequest(final Message message, final TaskSubmitRequest request) throws JMSException {
        // 自动应答地址
        if (Optional.ofNullable(request.getExtra()).map(JmsTaskExtra::getDestination).isEmpty()) {
            final Destination replyTo = message.getJMSReplyTo();
            if (Objects.nonNull(replyTo)) {
                if (Objects.isNull(request.getExtra())) {
                    request.setExtra(new JmsTaskExtra());
                }
                if (Objects.isNull(request.getExtra().getDestination())) {
                    request.getExtra().setDestination(new JmsTaskDestination());
                }
                request.getExtra().getDestination().setDestination(replyTo.toString());
            }
        }

        // indexes
        if (!CollectionUtils.isEmpty(request.getIndexes())) {
            final String correlationId = message.getJMSCorrelationID();
            if (StringUtils.isNotBlank(correlationId)) {
                request.setIndexes(Collections.singletonList(correlationId));
            }
        }

        // 延时时间
        if (request.getDelayMs() <= 0) {
            final long sendTimeMs = message.getJMSTimestamp();
            final long expirationTimeMs = message.getJMSExpiration();
            final long delayMs = expirationTimeMs - sendTimeMs;
            if (delayMs > 0) {
                request.setDelayMs(delayMs);
            }
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private <T extends TaskRequest> T extractByType(
            final Message message,
            final Class<? extends TaskRequest> clazz,
            final TypeReference<? extends TaskRequest> type) throws JMSException {
        final Optional<String> jsonOp = asJson(message, clazz);
        final Optional<TaskRequest> taskOp = jsonOp.map(j -> fromJson(j, message, type));
        final TaskRequest request = taskOp.orElseThrow(() -> new JmsTaskException("接收到的消息不合法: " + message));
        return (T) request;
    }

    private <T extends TaskRequest> Optional<String> asJson(final Message message, final Class<T> expectedContextClass)
            throws JMSException {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            final String text = textMessage.getText();
            return Optional.ofNullable(text);
        } else if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;
            final Enumeration<?> names = mapMessage.getMapNames();
            final Map<String, Object> map = new HashMap<>((int) StreamUtils.of(names).count());
            while (names.hasMoreElements()) {
                Object k = names.nextElement();
                String key = k instanceof String ? (String) k : k.toString();
                Object value = mapMessage.getObject(key);
                map.put(key, value);
            }
            final String text = JsonUtils.toJson(map);
            return Optional.ofNullable(text);
        } else if (message instanceof BytesMessage) {
            final BytesMessage bytesMessage = (BytesMessage) message;
            final String text = bytesMessage.readUTF();
            return Optional.ofNullable(text);
        } else if (message instanceof StreamMessage) {
            final StreamMessage streamMessage = (StreamMessage) message;
            final String text = streamMessage.readString();
            return Optional.ofNullable(text);
        } else if (message instanceof ObjectMessage) {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final Serializable serializable = objectMessage.getObject();
            if (expectedContextClass.isAssignableFrom(serializable.getClass())) {
                @SuppressWarnings("unchecked") final T context = (T) serializable;
                return Optional.ofNullable(JsonUtils.toJson(context));
            } else if (serializable instanceof String) {
                return Optional.of((String) serializable);
            } else {
                final String text = JsonUtils.toJson(serializable);
                return Optional.ofNullable(text);
            }
        }
        return Optional.empty();
    }

    private TaskRequest fromJson(
            final String json,
            @NotNull final Message source,
            @NotNull final TypeReference<? extends TaskRequest> type) {
        if (StringUtils.isBlank(json)) {
            log.error("消息转json内容为空, message={}", source);
            throw new JmsTaskException("消息转json内容为空");
        }
        final TaskRequest task = JsonUtils.parseObject(json, type);
        if (Objects.isNull(task)) {
            log.error("消息内容不合法, text格式需约定为json格式, message={}", source);
            throw new JmsTaskException("消息内容不合法, text格式需约定为json格式");
        }
        return task;
    }
}
