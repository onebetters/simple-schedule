package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackProperties;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackType;
import com.qianmi.b2b.scheduling.core.callback.TaskCallback;
import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Date: 2022-03-24 16:52.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskCallbackFactory {

    @NotNull CallbackType type();

    @Nullable TaskCallback get(@NotNull final CallbackProperties properties, @NotNull TaskElement task);
}
