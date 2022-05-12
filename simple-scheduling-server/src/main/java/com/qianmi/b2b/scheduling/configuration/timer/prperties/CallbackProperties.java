package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-24 15:54.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class CallbackProperties implements Serializable {
    private static final long serialVersionUID = -7235428628069473881L;

    /**
     * 回调方式
     */
    private CallbackType type;

    /**
     * Mq回调方式时，回调信息配置
     */
    private TimerCallbackActiveMq activeMq;
}
