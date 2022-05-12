package com.qianmi.b2b.scheduling.configuration.timer.callback;

/**
 * <p>Date: 2022-04-08 17:50.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface RetryableExpression {

    /**
     * 决策是否需要重试
     *
     * @param failedCount 已失败的次数
     * @return 负数，表示不需要重试，0表示立即重试，大于0表示延后多少时间后（单位：毫秒）重试
     */
    long delayRetryMs(final int failedCount);
}
