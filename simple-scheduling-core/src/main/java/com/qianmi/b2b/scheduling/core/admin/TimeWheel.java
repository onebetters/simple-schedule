package com.qianmi.b2b.scheduling.core.admin;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-25 18:09.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class TimeWheel implements Serializable {
    private static final long serialVersionUID = -8023309849227551279L;

    private static final long DEFAULT_TICK_MS = 100;
    private static final int DEFAULT_WHEEL_SIZE = 20;

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
}
