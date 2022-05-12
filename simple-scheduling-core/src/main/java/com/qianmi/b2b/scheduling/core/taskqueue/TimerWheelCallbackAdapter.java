package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.callback.*;
import com.qianmi.b2b.scheduling.core.supports.CloseableLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * <p>Date: 2022-04-08 16:26.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@RequiredArgsConstructor
class TimerWheelCallbackAdapter {

    private final TaskQueue memoryQueue;
    /**
     * 任务回调执行逻辑
     */
    @NotNull
    private final TaskCallbackSelector selector;
    /**
     * 任务回调互斥锁
     */
    @NotNull
    private final TaskCallbackLocker locker;
    /**
     * 回调通知动作
     */
    @NotNull
    private final Consumer<TaskElement> notifier;
    /**
     * 失败重试策略
     */
    @NotNull
    private final CallbackFailover failover;

    void call(@NotNull TaskElement task) {
        try (final CloseableLock lock = new CloseableLock(() -> locker.lock(new TaskLockContext(memoryQueue.scheduleName(),
                task,
                3_000
                // FIXME
        )), locker)) {
            if (lock.tryLock()) {
                final TaskCallback callback = selector.select(task);
                try {
                    callback.call(task);
                    notifier.accept(task);
                } catch (Exception e) {
                    log.error("任务回调执行失败, task={}", task, e);
                    failover.onFailed(memoryQueue.getSchedule(), task, e);
                }
            }
        }
    }
}
