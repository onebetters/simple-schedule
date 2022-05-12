package com.qianmi.b2b.scheduling.core;

import com.qianmi.b2b.scheduling.core.taskqueue.RemoveRequest;
import com.qianmi.b2b.scheduling.core.taskqueue.TaskQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 定时任务调度
 *
 * <p>Date: 2022-03-22 14:16.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class TaskSchedule {

    private final String name;
    private final TaskQueue taskQueue;

    public TaskSchedule(@NotNull String name, @NotNull TaskQueue taskQueue) {
        this.name = name;
        taskQueue.setSchedule(this);
        this.taskQueue = taskQueue;
    }

    public String getName() {
        return name;
    }

    /**
     * 提交任务
     *
     * @param task 待提交任务
     */
    @NotNull
    public TaskElement submit(@NotNull TaskElement task) {
        return taskQueue.add(task);
    }

    /**
     * 取消任务
     *
     * @param task 待取消任务
     */
    @NotNull
    public Optional<TaskElement> cancel(@NotNull String taskId, @NotNull TaskRemoveType removeType) {
        return Optional.ofNullable(taskQueue.remove(new RemoveRequest(taskId, removeType)));
    }
}
