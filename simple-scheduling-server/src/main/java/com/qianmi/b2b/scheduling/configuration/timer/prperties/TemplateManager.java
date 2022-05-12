package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Date: 2022-03-23 15:12.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Value
public class TemplateManager {
    /**
     * 所有的时间轮模板配置
     */
    Map<String, TimerWheelConfig> timerWheels;
    /**
     * 业务定时任务模板配置
     */
    Map<String, TimerTemplate> templates;

    public TemplateManager(TimerProperties properties) {
        properties.getTimerWheels().forEach((k, v) -> v.setKey(k));
        properties.getTemplates().forEach((k, v) -> v.setKey(k));

        this.timerWheels = Collections.unmodifiableMap(properties.getTimerWheels());
        this.templates = Collections.unmodifiableMap(properties.getTemplates());
    }

    public Optional<TimerTemplate> findTemplate(@Nullable final String template) {
        return Optional.ofNullable(template).map(templates::get);
    }

    /**
     * 根据任务模板ID，找到关联的时间轮配置信息
     *
     * @param template 任务模板ID
     * @return 时间轮配置信息
     */
    @NotNull
    public TimerWheelConfig findTimerWheelByTemplate(@Nullable final String template) {
        return this.findTemplate(template)
                .flatMap(tem -> this.findTimerWheelByExecutor(tem.getTimeWheel()))
                .orElse(TimerWheelConfig.DEFAULT_TIMER_WHEEL);
    }

    /**
     * 根据任务模板ID，找到关联的时间轮配置信息
     *
     * @param timerWheelName 时间轮编码
     * @return 时间轮配置信息
     */
    @NotNull
    public TimerWheelConfig findTimerWheel(@Nullable final String timerWheelName) {
        return this.findTimerWheelByExecutor(timerWheelName).orElse(TimerWheelConfig.DEFAULT_TIMER_WHEEL);
    }

    private Optional<TimerWheelConfig> findTimerWheelByExecutor(@Nullable final String executor) {
        return Optional.ofNullable(executor).filter(StringUtils::isNotBlank).map(timerWheels::get);
    }
}
