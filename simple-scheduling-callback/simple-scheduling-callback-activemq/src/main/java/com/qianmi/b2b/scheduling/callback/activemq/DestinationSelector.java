package com.qianmi.b2b.scheduling.callback.activemq;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

import javax.jms.Destination;

/**
 * <p>Date: 2022-03-24 15:34.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface DestinationSelector {

    /**
     * 决策选择回调mq地址
     *
     * @param task 调度任务
     * @return 回调mq地址
     */
    Destination select(@NotNull TaskElement task);
}
