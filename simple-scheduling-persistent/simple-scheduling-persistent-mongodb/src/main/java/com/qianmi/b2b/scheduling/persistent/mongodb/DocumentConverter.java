package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 16:57.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface DocumentConverter {

    @NotNull Document toDocument(@NotNull TaskElement task);

    @NotNull TaskElement toTask(@NotNull Document document);
}
