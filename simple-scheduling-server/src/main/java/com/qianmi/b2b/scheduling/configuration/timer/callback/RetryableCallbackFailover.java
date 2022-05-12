package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskSchedule;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailedRepository;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailover;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * 任务重试
 *
 * <p>Date: 2022-04-08 16:58.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class RetryableCallbackFailover implements CallbackFailover {

    private final CallbackFailedRepository failedRepository;
    private final RetryableExpression retryableExpression;

    /**
     * 任务回调失败动作 <br>
     * <p>
     * 手工指定时间点，如10s,30s,60s,5m,30m <br>
     * 固定时间间隔，如每隔x时间 <br>
     * 固定公式，如2的指数倍 <br>
     * <p>
     * 最小值、最大值、限制最大次数。
     *
     * @param schedule 任务调度器
     * @param task     任务
     * @param cause    失败原因
     */
    @Override
    public void onFailed(@NotNull TaskSchedule schedule, @NotNull TaskElement task, @NotNull Throwable cause) {
        final int failedCount = failedRepository.alreadyFailedCount(task);
        final long delayMs = retryableExpression.delayRetryMs(failedCount);
        if (delayMs >= 0) {
            task.setDelayMs(delayMs);
            schedule.submit(task);
        }
        failedRepository.saveFailedRecord(task, cause);
        if (delayMs < 0) {
            failedRepository.saveFinalFailed(task);
        }
    }
}
