package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskRebuildRequest;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsConstants;
import com.qianmi.b2b.scheduling.acceptor.jms.JmsTaskExtractor;
import com.qianmi.b2b.scheduling.core.TaskRemoveType;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.stream.Stream;

/**
 * <p>Date: 2022-03-25 13:41.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ActiveMqRebuildAcceptor implements ActiveMqTaskRequestHandler<TaskRebuildRequest> {

    private final TaskRepository taskRepository;
    private final TaskScheduleSelector taskScheduleSelector;

    /**
     * 销毁任务重建.<br>
     * 相当于CANCEL和SUBMIT的组合
     *
     * @param message 任务消息，最终格式内容为{@link TaskRebuildRequest}.<br>
     *                支持多种消息格式，如json文本，map、byte、stream、object
     * @throws JMSException jms异常
     */
    @JmsListener(destination = JmsConstants.TASK_REBUILD_DESTINATION,
            subscription = JmsConstants.TASK_REBUILD_DESTINATION)
    public void listen(final Message message) throws JMSException {
        final TaskRebuildRequest request = JmsTaskExtractor.extract(message, TaskRebuildRequest.class);
        this.handle(request);
    }

    @Override
    public @NotNull TaskAcceptType acceptType() {
        return TaskAcceptType.REBUILD;
    }

    @Override
    public void handle(TaskRebuildRequest request) {
        final TaskSchedule schedule = taskScheduleSelector.getInstance(request.getTemplate());
        final Stream<String> taskIds = taskRepository.findByIndex(schedule.getName(),
                request.getTemplate(),
                request.getUniqueIndex()
        ).map(TaskElement::getId);
        taskIds.forEach(taskId -> schedule.cancel(taskId, TaskRemoveType.CANCELED_BY_REBUILD));

        final TaskElement task = request.toTask();
        schedule.submit(task);
    }
}
