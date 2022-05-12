package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * <p>Date: 2022-03-22 17:02.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface MongoTaskHistoryBackup {

    void backup(
            @NotNull final String scheduleName,
            @NotNull TaskElement task,
            @Nullable final Map<String, Object> extend);
}
