package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 任务队列
 * <p>Date: 2022-04-08 10:21.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskQueue {

    default void start() {
    }

    void setSchedule(@NotNull TaskSchedule schedule);

    @NotNull TaskSchedule getSchedule();

    @NotNull
    default String scheduleName() {
        return getSchedule().getName();
    }

    @NotNull TaskElement add(@NotNull TaskElement task);

    @Nullable TaskElement remove(@NotNull RemoveRequest request);

    default void stop() {
    }
}