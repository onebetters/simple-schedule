package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.commons.utils.range.Range;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * <p>Date: 2022-04-08 10:45.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class DefaultTaskQueue implements TaskQueue {

    protected final InDatabaseTaskQueue databaseTaskQueue;
    protected final InMemoryTimerWheelTaskQueue memoryTimeWheelTaskQueue;
    /**
     * 最近一次加载数据库任务到内存的时间戳
     */
    protected final AtomicLong lastLoadTimeMs = new AtomicLong();
    private TaskSchedule schedule;

    public DefaultTaskQueue(
            @NotNull InDatabaseTaskQueue databaseTaskQueue,
            @NotNull InMemoryTimerWheelTaskQueue memoryTimeWheelTaskQueue) {
        this.databaseTaskQueue = databaseTaskQueue;
        this.memoryTimeWheelTaskQueue = memoryTimeWheelTaskQueue;
    }

    @Override
    public void setSchedule(@NotNull TaskSchedule schedule) {
        this.schedule = schedule;
        databaseTaskQueue.setSchedule(schedule);
        memoryTimeWheelTaskQueue.setSchedule(schedule);
    }

    @Override
    public @NotNull TaskSchedule getSchedule() {
        return schedule;
    }

    @Override
    public void start() {
        databaseTaskQueue.start();
        memoryTimeWheelTaskQueue.start();
        this.loadDatabaseTasksToMemory();
    }

    private void loadDatabaseTasksToMemory() {
        final long timestamp = System.currentTimeMillis();
        final Range<Long> range = Range.between(null, timestamp, false);
        this.loadDatabaseTasksToMemory(range);
    }

    protected void loadDatabaseTasksToMemory(@NotNull final Range<Long> range) {
        final Stream<TaskElement> taskStream = databaseTaskQueue.getTaskRepository()
                .findByExpectedFireTimeBetween(scheduleName(), range);
        taskStream.forEach(this::addToMemoryQueue);
        Optional.ofNullable(range.getTo()).ifPresent(lastLoadTimeMs::set);
    }

    @Override
    public @NotNull TaskElement add(@NotNull TaskElement task) {
        final TaskElement saved = databaseTaskQueue.add(task);
        this.addToMemoryQueue(saved);
        return saved;
    }

    protected void addToMemoryQueue(@NotNull TaskElement task) {
        memoryTimeWheelTaskQueue.add(task);
    }

    @Override
    public @Nullable TaskElement remove(@NotNull RemoveRequest request) {
        final TaskElement removed = databaseTaskQueue.remove(request);
        memoryTimeWheelTaskQueue.remove(request);
        return removed;
    }
}
