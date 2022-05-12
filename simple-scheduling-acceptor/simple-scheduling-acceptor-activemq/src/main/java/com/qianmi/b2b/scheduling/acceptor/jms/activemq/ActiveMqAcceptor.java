package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptRequest;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskRequest;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsConstants;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsTaskExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Date: 2022-04-21 10:05.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
public class ActiveMqAcceptor {
    private final Map<TaskAcceptType, ActiveMqTaskRequestHandler<? extends TaskRequest>> handlerMap;

    public ActiveMqAcceptor(List<ActiveMqTaskRequestHandler<? extends TaskRequest>> handlers) {
        this.handlerMap = handlers.stream().collect(Collectors.toMap(ActiveMqTaskRequestHandler::acceptType, h -> h));
    }

    /**
     * 统一入口接收业务线任务，包括提交、取消、重置...
     *
     * @param message 任务消息，具体格式内容可参考{@link TaskAcceptRequest}.<br>
     *                支持多种消息格式，如json文本，map、byte、stream、object
     * @throws JMSException jms异常
     */
    @JmsListener(destination = JmsConstants.TASK_ACCEPT_DESTINATION,
            subscription = JmsConstants.TASK_ACCEPT_DESTINATION)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void listen(final Message message) throws JMSException {
        @SuppressWarnings("unchecked") final TaskAcceptRequest<TaskRequest> request = JmsTaskExtractor.extract(message,
                TaskAcceptRequest.class
        );

        final TaskAcceptType acceptType = TaskAcceptType.of(request.getRequestType()).orElse(null);
        if (Objects.isNull(acceptType)) {
            log.error("任务提交失败，操作类型不存在, 原始jms消息内容message={}", message);
            return;
        }

        final ActiveMqTaskRequestHandler handler = handlerMap.get(acceptType);
        if (Objects.nonNull(handler)) {
            final TaskRequest taskRequest = request.getRequestPayload();
            handler.handle(taskRequest);
        }
    }
}
