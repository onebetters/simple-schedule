package com.qianmi.b2b.scheduling.core.schedule;

import com.qianmi.b2b.commons.utils.timer.delay.DelayedOperationPurgatory;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <p>Date: 2022-03-22 19:44.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class SimpleTaskScheduleSelector implements TaskScheduleSelector {

    @NotNull
    private final DelayedOperationPurgatoryBuilder purgatoryBuilder;
    @NotNull
    private final TaskScheduleBuilder taskScheduleBuilder;
    @NotNull
    private final Function<String, String> taskTemplateToScheduleName;

    private final Map<String, TaskSchedule> scheduleNameAndScheduleCache = new ConcurrentHashMap<>();

    @Override
    public @NotNull TaskSchedule getInstance(@NotNull String template) {
        final String scheduleName = taskTemplateToScheduleName.apply(template);
        return scheduleNameAndScheduleCache.computeIfAbsent(scheduleName, t -> {
            final DelayedOperationPurgatory purgatory = purgatoryBuilder.build(t);
            return taskScheduleBuilder.build(purgatory);
        });
    }
}
