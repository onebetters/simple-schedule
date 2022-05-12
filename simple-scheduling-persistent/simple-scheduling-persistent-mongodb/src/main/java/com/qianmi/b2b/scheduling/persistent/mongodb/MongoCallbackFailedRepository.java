package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailedRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static com.mongodb.client.model.Filters.eq;

/**
 * <p>Date: 2022-04-11 09:21.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class MongoCallbackFailedRepository implements CallbackFailedRepository {

    @NotNull
    private final CollectionFactory collectionFactory;

    @Override
    public int alreadyFailedCount(@NotNull TaskElement task) {
        final String collection = TaskCollections.selectErrorRecord(task.getTimestamp());
        final long count = collectionFactory.getCollection(collection)
                .countDocuments(eq(TaskErrorRecord.Fields.taskId, task.getId()));
        return (int) count;
    }

    @Override
    public void saveFailedRecord(
            @NotNull TaskElement task, @NotNull Throwable cause) {
        final String collection = TaskCollections.selectErrorRecord(task.getTimestamp());
        final TaskErrorRecord taskErrorRecord = TaskErrorRecord.from(task, cause);
        collectionFactory.getCollection(collection, TaskErrorRecord.class).insertOne(taskErrorRecord);
    }

    @Override
    public void saveFinalFailed(@NotNull TaskElement task) {
        final String collection = TaskCollections.selectError();
        collectionFactory.getCollection(collection, TaskElement.class).insertOne(task);
    }
}
