package com.qianmi.b2b.scheduling.configuration.timer;

import com.qianmi.b2b.commons.utils.timer.delay.DelayedOperationPurgatory;
import com.qianmi.b2b.commons.utils.timer.timingwheel.Timer;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TemplateManager;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerWheelConfig;
import com.qianmi.b2b.scheduling.core.schedule.DelayedOperationPurgatoryBuilder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2022-03-23 15:06.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class ConfigurablePurgatoryBuilder implements DelayedOperationPurgatoryBuilder {

    private final TemplateManager templateManager;
    private final Map<TimerWheelConfig, DelayedOperationPurgatory> cache = new ConcurrentHashMap<>();

    @Override
    public @NotNull DelayedOperationPurgatory build(@NotNull String template) {
        final TimerWheelConfig wheelConfig = templateManager.findTimerWheelByTemplate(template);
        return cache.computeIfAbsent(wheelConfig, wheel -> {
            final Timer timer = Timer.of(wheel.getKey(), wheel.getTickMs(), wheel.getWheelSize());
            return new DelayedOperationPurgatory(template, timer);
        });
    }
}
