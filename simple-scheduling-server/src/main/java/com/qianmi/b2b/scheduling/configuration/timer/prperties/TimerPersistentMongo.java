package com.qianmi.b2b.scheduling.configuration.timer.prperties;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-23 14:54.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class TimerPersistentMongo implements Serializable {
    private static final long serialVersionUID = 4768641996704875077L;

    /**
     * 任务执行完后，是否备份历史调度数据.
     * false不保存任何数据，true保存源数据到历史备份表
     */
    private boolean backupHistory;
}
