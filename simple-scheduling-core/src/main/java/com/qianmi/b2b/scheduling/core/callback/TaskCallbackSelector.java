package com.qianmi.b2b.scheduling.core.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-23 15:48.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskCallbackSelector {

    @NotNull TaskCallback select(@NotNull final TaskElement task);
}
