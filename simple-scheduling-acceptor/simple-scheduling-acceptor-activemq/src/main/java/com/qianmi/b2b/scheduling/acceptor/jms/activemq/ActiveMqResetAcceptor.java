package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskResetRequest;
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
 * <p>Date: 2022-04-06 16:16.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
public class ActiveMqResetAcceptor implements ActiveMqTaskRequestHandler<TaskResetRequest> {

    private final TaskRepository taskRepository;
    private final TaskScheduleSelector taskScheduleSelector;

    public ActiveMqResetAcceptor(TaskRepository taskRepository, TaskScheduleSelector taskScheduleSelector) {
        this.taskRepository = taskRepository;
        this.taskScheduleSelector = taskScheduleSelector;
    }

    /**
     * 重置定时任务
     *
     * @param message 重置任务通知
     * @throws JMSException jms异常
     */
    @JmsListener(destination = JmsConstants.TASK_RESET_DESTINATION, subscription = JmsConstants.TASK_RESET_DESTINATION)
    public void listen(final Message message) throws JMSException {
        final TaskResetRequest request = JmsTaskExtractor.extract(message, TaskResetRequest.class);
        if (!request.shouldReset()) {
            log.debug("入参信息不需要重置任务，比如时间变更为0。taskReq={}", request);
            return;
        }

        this.handle(request);
    }

    @Override
    public @NotNull TaskAcceptType acceptType() {
        return TaskAcceptType.RESET;
    }

    @Override
    public void handle(TaskResetRequest request) {
        final TaskSchedule schedule = taskScheduleSelector.getInstance(request.getTemplate());
        final Stream<String> taskIds = taskRepository.findByIndex(schedule.getName(),
                request.getTemplate(),
                request.getIndex()
        ).map(TaskElement::getId);
        taskIds.forEach(taskId -> schedule.cancel(taskId, TaskRemoveType.CANCELED_BY_RESET).ifPresent(task -> {
            final TaskElement newTask = task.resetDelayMs(request.getDelayMsDiff());
            schedule.submit(newTask);
        }));
    }
}
