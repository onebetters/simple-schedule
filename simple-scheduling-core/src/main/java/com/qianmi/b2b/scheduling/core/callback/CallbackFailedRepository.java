package com.qianmi.b2b.scheduling.core.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-04-08 18:49.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface CallbackFailedRepository {

    int alreadyFailedCount(@NotNull final TaskElement task);

    /**
     * 记录每次的操作失败日志
     *
     * @param task  回调失败的任务
     * @param cause 失败原因
     */
    void saveFailedRecord(@NotNull final TaskElement task, @NotNull final Throwable cause);

    void saveFinalFailed(@NotNull final TaskElement task);
}
