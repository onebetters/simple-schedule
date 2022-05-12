package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Date: 2022-04-08 10:27.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class InDatabaseTaskQueue implements TaskQueue {

    /**
     * 任务主存储操作
     */
    private final TaskRepository taskRepository;
    private TaskSchedule schedule;

    public InDatabaseTaskQueue(@NotNull TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void setSchedule(@NotNull TaskSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public @NotNull TaskSchedule getSchedule() {
        return schedule;
    }

    @NotNull TaskRepository getTaskRepository() {
        return taskRepository;
    }

    @Override
    public @NotNull TaskElement add(@NotNull TaskElement task) {
        return taskRepository.save(scheduleName(), task);
    }

    @Override
    public @Nullable TaskElement remove(@NotNull RemoveRequest request) {
        return taskRepository.remove(scheduleName(), request);
    }
}
