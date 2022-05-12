package com.qianmi.b2b.scheduling.configuration.timer;

import com.qianmi.b2b.scheduling.configuration.timer.callback.TaskCallbackManager;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TemplateManager;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerTemplate;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.callback.TaskCallback;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <p>Date: 2022-03-23 15:52.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ConfigurableTaskCallbackSelector implements TaskCallbackSelector {

    private final TaskCallbackManager taskCallbackManager;
    private final TemplateManager templateManager;
    private static final TaskCallback UNKNOWN_CALLBACK = task -> log.error("匹配不到对应任务回调逻辑，task={}", task);

    @Override
    public @NotNull TaskCallback select(@NotNull TaskElement task) {
        final Optional<TimerTemplate> timerTemplateOp = templateManager.findTemplate(task.getTemplate());
        return timerTemplateOp.map(template -> taskCallbackManager.get(template, task)).orElse(UNKNOWN_CALLBACK);
    }
}
