package com.qianmi.b2b.scheduling.core.schedule;

import com.qianmi.b2b.commons.utils.timer.delay.DelayedOperationPurgatory;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 19:41.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface DelayedOperationPurgatoryBuilder {

    @NotNull DelayedOperationPurgatory build(@NotNull String template);
}
