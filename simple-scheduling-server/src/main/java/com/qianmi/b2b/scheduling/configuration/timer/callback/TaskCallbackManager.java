package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.commons.utils.stream.StreamUtils;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackProperties;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackType;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerTemplate;
import com.qianmi.b2b.scheduling.core.callback.TaskCallback;
import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2022-03-24 15:59.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class TaskCallbackManager {

    private final Map<CallbackType, TaskCallbackFactory> factoriesMap;
    private final Map<TimerTemplate, TaskCallback> cache = new ConcurrentHashMap<>();

    public TaskCallbackManager(List<TaskCallbackFactory> factories) {
        this.factoriesMap = StreamUtils.map(factories, TaskCallbackFactory::type);
    }

    @Nullable
    public TaskCallback get(@NotNull TimerTemplate template, @NotNull TaskElement task) {
        return cache.computeIfAbsent(
                template,
                temp -> Optional.ofNullable(temp.getCallback())
                        .filter(c -> Objects.nonNull(c.getType()))
                        .map(p -> this.find(p, task))
                        .orElse(null)
        );
    }

    private TaskCallback find(@NotNull final CallbackProperties properties, @NotNull TaskElement task) {
        return Optional.ofNullable(factoriesMap.get(properties.getType()))
                .map(f -> f.get(properties, task))
                .orElse(null);
    }
}
