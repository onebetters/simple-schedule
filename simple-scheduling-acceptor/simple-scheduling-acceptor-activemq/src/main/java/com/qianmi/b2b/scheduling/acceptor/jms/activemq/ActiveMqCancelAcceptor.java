package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskCancelRequest;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsConstants;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsTaskExtractor;
import com.qianmi.b2b.scheduling.core.TaskRemoveType;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleSelector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.stream.Stream;

/**
 * <p>Date: 2022-04-06 14:11.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
public class ActiveMqCancelAcceptor implements ActiveMqTaskRequestHandler<TaskCancelRequest> {

    private final TaskRepository taskRepository;
    private final TaskScheduleSelector taskScheduleSelector;

    public ActiveMqCancelAcceptor(TaskRepository taskRepository, TaskScheduleSelector taskScheduleSelector) {
        this.taskRepository = taskRepository;
        this.taskScheduleSelector = taskScheduleSelector;
    }

    /**
     * 取消定时任务
     *
     * @param message 取消通知
     * @throws JMSException jms异常
     */
    @JmsListener(destination = JmsConstants.TASK_CANCEL_DESTINATION,
            subscription = JmsConstants.TASK_CANCEL_DESTINATION)
    public void listen(final Message message) throws JMSException {
        final TaskCancelRequest request = JmsTaskExtractor.extract(message, TaskCancelRequest.class);
        this.handle(request);
    }

    @Override
    public @NotNull TaskAcceptType acceptType() {
        return TaskAcceptType.CANCEL;
    }

    @Override
    public void handle(TaskCancelRequest request) {
        final TaskSchedule schedule = taskScheduleSelector.getInstance(request.getTemplate());
        final Stream<String> taskIds = taskRepository.findByIndex(schedule.getName(),
                request.getTemplate(),
                request.getIndex()
        ).map(TaskElement::getId);
        taskIds.forEach(taskId -> schedule.cancel(taskId, TaskRemoveType.CANCELED));
    }
}
