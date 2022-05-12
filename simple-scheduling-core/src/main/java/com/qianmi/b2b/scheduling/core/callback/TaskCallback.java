package com.qianmi.b2b.scheduling.core.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

/**
 * 任务到点执行的逻辑
 *
 * <p>Date: 2022-03-22 14:21.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskCallback {

    /**
     * 任务回调逻辑
     *
     * @param task 调度任务
     * @return 回调结果
     */
    void call(@NotNull TaskElement task);
}
