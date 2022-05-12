package com.qianmi.b2b.scheduling.core.callback;

import org.jetbrains.annotations.NotNull;

/**
 * 任务回调锁定器 <br>
 * 主要适用于分布式环境，多台负载抢占回调处理同一个任务。<br>
 * 可以实现为：
 * 1、非分布式环境，jdk本地锁即可；
 * 2、分布式环境
 * a)、最简单粗暴的做法，直接每个task加锁，呵呵，性能可能就得关注下了；
 * b)、直接应用层加锁，可以，顶多就是多个jvm进程，同时只会有一个在工作，其他都是备胎；
 * c)、分段式加锁，比如schedule层级加锁（假设一个进程有多个schedule）、id分段锁【简单的如hash取模，直观的如每个任务入队+counter】等等
 *
 * <p>Date: 2022-04-07 20:10.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskCallbackLocker {

    /**
     * 任务回调抢锁
     *
     * @param scheduleName 调度器
     * @param task         待执行回调的任务
     * @return true需要后续回调动作，false忽略回调动作
     */
    boolean lock(@NotNull final TaskLockContext lockContext);
}
