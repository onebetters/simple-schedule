package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-24 15:39.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class TimerCallbackActiveMq implements Serializable {
    private static final long serialVersionUID = 8258426620504624551L;

    /**
     *
     */
    private String broker;

    /**
     * 回调目标
     */
    private String destination;

    /**
     * true为topic，false为queue
     */
    private boolean pubSubDomain;

    /**
     * 等待ack确认。如果ack未确定，后续尝试重试通知
     */
    private boolean ackWaiting;

    /**
     * 如果配置需要等待{@link #ackWaiting ack}且未等到对端的ack，最多重试通知多少次.
     */
    private int maxRetryCount;

    /**
     * 配合{@link #maxRetryCount}使用，指定重试时间点策略.
     * 如，
     * 指定延时点：5s、60s、300s
     * 指数延时点：2秒、4秒、8秒
     * 固定间隔点：per1min
     * ...
     * 自定义
     */
    private String retryTimeRule;
}
