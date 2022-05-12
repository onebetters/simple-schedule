package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.commons.utils.collection.MapUtils;
import com.qianmi.b2b.scheduling.core.TaskElement;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 17:00.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class DefaultDocumentConverter implements DocumentConverter {

    static final String FIELD_ID = "_id";
    static final String FIELD_EXPECTED_FIRE_MS = "expectedFireMs";

    @Override
    public @NotNull Document toDocument(@NotNull TaskElement task) {
        return new Document(MapUtils.of(
                FIELD_ID,
                task.getId(),
                TaskElement.Fields.timestamp,
                task.getTimestamp(),
                TaskElement.Fields.template,
                task.getTemplate(),
                TaskElement.Fields.delayMs,
                task.getDelayMs(),
                TaskElement.Fields.payload,
                task.getPayload(),
                TaskElement.Fields.indexes,
                task.getIndexes(),
                TaskElement.Fields.extra,
                task.getExtra(),
                FIELD_EXPECTED_FIRE_MS,
                task.getExpectedFireMs()
        ));
    }

    @Override
    public @NotNull TaskElement toTask(@NotNull Document document) {
        return new TaskElement(
                document.getString(FIELD_ID),
                document.getLong(TaskElement.Fields.timestamp),
                document.getString(TaskElement.Fields.template),
                document.getLong(TaskElement.Fields.delayMs),
                document.getString(TaskElement.Fields.payload),
                document.getList(TaskElement.Fields.indexes, String.class),
                document.getString(TaskElement.Fields.extra)
        );
    }
}
