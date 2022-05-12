package com.qianmi.b2b.scheduling.acceptor.jms;

import lombok.experimental.UtilityClass;

/**
 * <p>Date: 2022-03-25 13:43.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@UtilityClass
public class JmsConstants {

    /**
     * 统一任务添加入口
     *
     * @see com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptRequest
     */
    public static final String TASK_ACCEPT_DESTINATION = "simple_scheduling_accept";

    /**
     * 添加任务
     */
    public static final String TASK_SUBMIT_DESTINATION = "simple_scheduling_submit";

    /**
     * 销毁任务重建.<br>
     * 相当于CANCEL和SUBMIT的组合
     */
    public static final String TASK_REBUILD_DESTINATION = "simple_scheduling_rebuild";

    /**
     * 取消任务
     */
    public static final String TASK_CANCEL_DESTINATION = "simple_scheduling_cancel";

    /**
     * 重置之前的定时任务，一般用于重新设置超时时间。<br>
     * 比如，之前设置的是延时2天后执行，改成1天（提前，有可能触发立即执行）或5天后（延后）执行
     */
    public static final String TASK_RESET_DESTINATION = "simple_scheduling_reset";

    /**
     * 回调到未知任务队列
     */
    public static final String COMMON_UNKNOWN_REPLY_DESTINATION = "simple_scheduling_unknown_reply";
}
