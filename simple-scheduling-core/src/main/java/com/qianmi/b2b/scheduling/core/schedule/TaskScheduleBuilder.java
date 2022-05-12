package com.qianmi.b2b.scheduling.core.schedule;

import com.qianmi.b2b.commons.utils.timer.delay.DelayedOperationPurgatory;
import com.qianmi.b2b.scheduling.core.TaskRemoveType;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailover;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackLocker;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackSelector;
import com.qianmi.b2b.scheduling.core.taskqueue.*;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * <p>Date: 2022-03-22 19:36.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Value
public class TaskScheduleBuilder {

    @NotNull TaskRepository taskRepository;
    @NotNull TaskCallbackSelector callbackSelector;
    @NotNull TaskCallbackLocker taskCallbackLocker;
    @NotNull CallbackFailover callbackFailover;
    /**
     * 单位分钟.<br>
     * 表示内存队列只保留该时间段内即将执行的任务.<br>
     * 超出该时间范围的，存储在磁盘，后续定时同步到内存队列.<br>
     */
    Function<String, Long> keepInMemoryDelayMinutesSupplier;

    public TaskSchedule build(@NotNull final DelayedOperationPurgatory purgatory) {
        final String name = purgatory.getName();
        final Long keepInMemoryDelayMinutes = keepInMemoryDelayMinutesSupplier.apply(name);

        final InDatabaseTaskQueue databaseTaskQueue = new InDatabaseTaskQueue(taskRepository);
        final InMemoryTimerWheelTaskQueue memoryTimeWheelTaskQueue = new InMemoryTimerWheelTaskQueue(purgatory,
                callbackSelector,
                taskCallbackLocker,
                task -> databaseTaskQueue.remove(new RemoveRequest(task.getId(), TaskRemoveType.FINISHED)),
                callbackFailover
        );
        final TaskQueue taskQueue;
        if (Objects.isNull(keepInMemoryDelayMinutes) || keepInMemoryDelayMinutes <= 0) {
            taskQueue = new DefaultTaskQueue(databaseTaskQueue, memoryTimeWheelTaskQueue);
        } else {
            taskQueue = new DelayedBrokenTaskQueue(databaseTaskQueue,
                    memoryTimeWheelTaskQueue,
                    keepInMemoryDelayMinutes,
                    TimeUnit.MINUTES
            );
        }
        final TaskSchedule schedule = new TaskSchedule(name, taskQueue);
        taskQueue.start();
        return schedule;
    }
}
