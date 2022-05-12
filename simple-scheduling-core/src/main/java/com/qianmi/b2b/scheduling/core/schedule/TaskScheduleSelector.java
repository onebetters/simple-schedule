package com.qianmi.b2b.scheduling.core.schedule;

import com.qianmi.b2b.scheduling.core.TaskSchedule;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 19:20.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskScheduleSelector {

    @NotNull TaskSchedule getInstance(@NotNull final String template);
}
