package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.commons.utils.range.Range;
import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.qianmi.b2b.commons.utils.thread.ThreadFactoryFactory.prefix;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * 实现目标：<br>
 * 配合persistent实现层（比如mongodb、jdbc等），实现对内存任务队列的短路、桥接，主要是为了内存队列大小的均衡考量。<br>
 * 如：<br>
 * 内存队列只存储最近N时间段内（比如最近1小时内）的任务，超出时间范围的，只存储在比如(数据库等)磁盘里。<br>
 * 配合定时任务器，将超过N时间范围的后续任务，定时提交到内存队列里。
 *
 * <p>Date: 2022-04-08 11:08.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class DelayedBrokenTaskQueue extends DefaultTaskQueue {

    /**
     * 内存队列只保留最近N时间段内即将触发的任务 <br>
     */
    private final long keepInMemoryDelayMs;

    public DelayedBrokenTaskQueue(
            @NotNull InDatabaseTaskQueue databaseTaskQueue,
            @NotNull InMemoryTimerWheelTaskQueue memoryTimeWheelTaskQueue,
            final long keepInMemoryDelayTime,
            @NotNull final TimeUnit keepInMemoryTimeUnit) {
        super(databaseTaskQueue, memoryTimeWheelTaskQueue);
        this.keepInMemoryDelayMs = keepInMemoryTimeUnit.toMillis(keepInMemoryDelayTime);
    }

    @Override
    public void start() {
        super.start();
        this.initTimer();
    }

    private void initTimer() {
        final ThreadFactory threadFactory = prefix(DelayedBrokenTaskQueue.class.getSimpleName(), true);
        final ScheduledExecutorService executor = newSingleThreadScheduledExecutor(threadFactory);
        executor.scheduleAtFixedRate(() -> {
            final long from = 0 == lastLoadTimeMs.get() ? System.currentTimeMillis() : lastLoadTimeMs.get();
            final long to = from + keepInMemoryDelayMs;
            final Range<Long> range = Range.between(from, true, to, false);
            this.loadDatabaseTasksToMemory(range);
        }, 1000, keepInMemoryDelayMs, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    @Override
    protected void addToMemoryQueue(@NotNull TaskElement task) {
        if (task.getExpectedFireMs() <= System.currentTimeMillis() + keepInMemoryDelayMs) {
            super.addToMemoryQueue(task);
        }
    }
}
