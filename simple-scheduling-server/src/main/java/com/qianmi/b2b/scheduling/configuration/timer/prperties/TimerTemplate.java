package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-23 14:46.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class TimerTemplate implements Serializable {
    private static final long serialVersionUID = -816334981227683537L;

    /**
     * 定时任务模板ID
     */
    private String key;

    /**
     * 任务描述说明信息
     */
    private String description;

    /**
     * 关联时间轮分组
     *
     * @see TimerWheelConfig#getKey()
     */
    private String timeWheel;

    /**
     * 回调配置
     */
    private CallbackProperties callback;
}
