package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskSubmitRequest;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsConstants;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsTaskExtractor;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleSelector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * <p>Date: 2022-03-25 13:41.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
public class ActiveMqSubmitAcceptor implements ActiveMqTaskRequestHandler<TaskSubmitRequest> {

    private final TaskScheduleSelector taskScheduleSelector;

    public ActiveMqSubmitAcceptor(TaskScheduleSelector taskScheduleSelector) {
        this.taskScheduleSelector = taskScheduleSelector;
    }

    /**
     * 提交任务
     *
     * @param message 任务消息，最终格式内容为{@link TaskSubmitRequest}.<br>
     *                支持多种消息格式，如json文本，map、byte、stream、object
     * @throws JMSException jms异常
     */
    @JmsListener(destination = JmsConstants.TASK_SUBMIT_DESTINATION,
            subscription = JmsConstants.TASK_SUBMIT_DESTINATION)
    public void listen(final Message message) throws JMSException {
        final TaskSubmitRequest request = JmsTaskExtractor.extract(message, TaskSubmitRequest.class);
        this.handle(request);
    }

    @Override
    public @NotNull TaskAcceptType acceptType() {
        return TaskAcceptType.SUBMIT;
    }

    @Override
    public void handle(TaskSubmitRequest request) {
        final TaskElement task = request.toTask();
        final TaskSchedule schedule = taskScheduleSelector.getInstance(task.getTemplate());
        schedule.submit(task);
    }
}
