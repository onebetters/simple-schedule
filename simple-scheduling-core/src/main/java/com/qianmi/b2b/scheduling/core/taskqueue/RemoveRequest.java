package com.qianmi.b2b.scheduling.core.taskqueue;

import com.qianmi.b2b.scheduling.core.TaskRemoveType;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-04-21 13:45.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Value
public class RemoveRequest {

    @NotNull String taskId;
    @NotNull TaskRemoveType removeType;
}
