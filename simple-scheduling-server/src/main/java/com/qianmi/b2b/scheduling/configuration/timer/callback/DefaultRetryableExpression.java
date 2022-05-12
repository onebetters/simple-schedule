package com.qianmi.b2b.scheduling.configuration.timer.callback;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * <p>Date: 2022-04-11 09:31.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class DefaultRetryableExpression implements RetryableExpression {

    @Override
    public long delayRetryMs(int failedCount) {
        if (failedCount > 10) {
            return -1;
        }
        if (failedCount <= 0) {
            return 5_000;
        }
        final long minutes = Math.min(5L * failedCount, 60);
        return Duration.of(minutes, ChronoUnit.MILLIS).toMillis();
    }
}
