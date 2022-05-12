package com.qianmi.b2b.scheduling.callback.activemq;

import com.qianmi.b2b.scheduling.core.SchedulingException;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.callback.TaskCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Destination;
import java.util.Objects;

/**
 * <p>Date: 2022-03-24 15:16.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ActiveMqCallback implements TaskCallback {
    private final JmsMessagingTemplateSelector jmsMessagingTemplateSelector;
    private final DestinationSelector destinationSelector;

    @Override
    public void call(@NotNull TaskElement task) {
        final JmsMessagingTemplate jmsMessagingTemplate = jmsMessagingTemplateSelector.select(task);
        final Destination destination = destinationSelector.select(task);
        if (Objects.nonNull(destination)) {
            try {
                jmsMessagingTemplate.convertAndSend(destination, StringUtils.defaultString(task.getPayload()));
                log.info("定时任务回调通知成功, task={}", task);
            } catch (Exception e) {
                log.error("定时任务回调通知失败, task={}", task, e);
                throw new SchedulingException(e);
            }
        } else {
            log.error("未匹配到对应的Destination，task={}", task);
            throw new SchedulingException("未匹配到对应的Destination");
        }
    }
}
