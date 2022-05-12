package com.qianmi.b2b.scheduling.core.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.Value;

/**
 * <p>Date: 2022-04-11 15:03.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Value
public class TaskLockContext {

    String scheduleName;
    TaskElement task;
    long ttlMs;

    public String identifier() {
        return scheduleName + "_" + task.getId();
    }
}
