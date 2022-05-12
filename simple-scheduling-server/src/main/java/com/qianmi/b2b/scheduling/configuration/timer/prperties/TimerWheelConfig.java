package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-23 14:47.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimerWheelConfig implements Serializable {
    private static final long serialVersionUID = -8602273660480260637L;
    private static final long DEFAULT_TICK_MS = 1;
    private static final int DEFAULT_WHEEL_SIZE = 20;
    static final TimerWheelConfig DEFAULT_TIMER_WHEEL = new TimerWheelConfig("common",
            DEFAULT_TICK_MS,
            DEFAULT_WHEEL_SIZE,
            30
    );

    /**
     * 时间轮线程名
     */
    @NotNull
    private String key;
    /**
     * 时间轮每一个槽的表示时间，单位毫秒
     */
    private long tickMs = DEFAULT_TICK_MS;
    /**
     * 时间轮有多少个槽
     */
    private int wheelSize = DEFAULT_WHEEL_SIZE;

    /**
     * 单位分钟.<br>
     * 表示内存队列只保留该时间段内即将执行的任务.<br>
     * 超出该时间范围的，存储在磁盘，后续定时同步到内存队列.<br>
     */
    private long keepInMemoryDelayBeforeMinutes;
}
