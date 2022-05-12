package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.commons.utils.function.Consumers;
import com.qianmi.b2b.commons.utils.timer.delay.DelayedOperationPurgatory;
import com.qianmi.b2b.commons.utils.timer.timingwheel.TimerTask;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailover;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackLocker;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackSelector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.qianmi.b2b.commons.utils.function.Consumers.nonnull;

/**
 * <p>Date: 2022-04-08 10:23.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
public class InMemoryTimerWheelTaskQueue implements TaskQueue {

    /**
     * 调度器
     */
    @NotNull
    private final DelayedOperationPurgatory purgatory;
    /**
     * 任务回调逻辑
     */
    private final TimerWheelCallbackAdapter callbackAdapter;
    /**
     * 任务索引，适用于后续快速定位检索，便于如删除任务等场景
     */
    private final Map<String, TaskRunner> taskIdIndex = new ConcurrentHashMap<>(1024);
    private TaskSchedule schedule;

    /**
     * 内存队列定时器
     *
     * @param purgatory          定时任务器
     * @param callbackSelector   回调逻辑选择器
     * @param taskCallbackLocker 回调互斥锁
     * @param callbackNotifier   回调执行后，更多定制化操作
     */
    public InMemoryTimerWheelTaskQueue(
            @NotNull DelayedOperationPurgatory purgatory,
            @NotNull TaskCallbackSelector callbackSelector,
            @NotNull TaskCallbackLocker taskCallbackLocker,
            @Nullable Consumer<TaskElement> callbackNotifier,
            @NotNull final CallbackFailover callbackFailover) {
        this.purgatory = purgatory;
        this.callbackAdapter = new TimerWheelCallbackAdapter(
                this,
                callbackSelector,
                taskCallbackLocker,
                Consumers.of(nonnull(callbackNotifier), task -> taskIdIndex.remove(task.getId())),
                callbackFailover
        );
    }

    @Override
    public void setSchedule(@NotNull TaskSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public @NotNull TaskSchedule getSchedule() {
        return schedule;
    }

    @Override
    public @NotNull TaskElement add(@NotNull TaskElement task) {
        return taskIdIndex.computeIfAbsent(task.getId(), id -> {
            final TaskRunner runner = new TaskRunner(task);
            purgatory.submit(runner);
            return runner;
        }).source;
    }

    @Override
    public @Nullable TaskElement remove(@NotNull RemoveRequest request) {
        String taskId = request.getTaskId();
        final TaskRunner runner = taskIdIndex.remove(taskId);
        if (Objects.nonNull(runner)) {
            runner.cancel();
            return runner.source;
        }
        return null;
    }

    private class TaskRunner extends TimerTask {
        private final TaskElement source;

        public TaskRunner(TaskElement source) {
            super(Math.max(0L, source.getExpectedFireMs() - System.currentTimeMillis()));
            this.source = source;
        }

        @Override
        public void run() {
            callbackAdapter.call(this.source);
        }
    }
}
