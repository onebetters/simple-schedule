package com.qianmi.b2b.scheduling.core.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import org.jetbrains.annotations.NotNull;

/**
 * 回调失败重试动作
 *
 * <p>Date: 2022-04-08 16:17.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface CallbackFailover {

    /**
     * 任务回调失败动作
     *
     * @param schedule 任务调度器
     * @param task     任务
     * @param cause    失败原因
     */
    void onFailed(@NotNull TaskSchedule schedule, @NotNull TaskElement task, @NotNull final Throwable cause);
}
