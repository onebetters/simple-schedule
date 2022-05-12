package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Date: 2022-03-22 17:54.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
// @RefreshScope
@ConfigurationProperties(prefix = "timer")
public class TimerProperties implements Serializable {
    private static final long serialVersionUID = 5469174924553504952L;

    /**
     * 数据持久化方法
     */
    private PersistentType persistent;

    /**
     * 所有的时间轮模板配置
     */
    private Map<String, TimerWheelConfig> timerWheels = new HashMap<>();

    /**
     * 业务定时任务模板配置
     */
    private Map<String, TimerTemplate> templates = new HashMap<>();

    public enum PersistentType {
        MONGO, JDBC;
    }
}
