package com.qianmi.b2b.scheduling.core.extra;

import lombok.Data;

/**
 * <p>Date: 2022-03-25 10:24.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class JmsTaskExtra implements TaskExtra {
    private static final long serialVersionUID = -2656974736508630483L;

    /**
     * 回调目标
     * 适用场景：
     * 1、如果业务未配置模板，自动生成回调相关目标信息
     */
    private JmsTaskDestination destination;
}
