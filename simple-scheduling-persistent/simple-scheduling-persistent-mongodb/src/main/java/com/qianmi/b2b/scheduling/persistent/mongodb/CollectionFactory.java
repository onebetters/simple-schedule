package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 16:50.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface CollectionFactory {

    default MongoCollection<Document> getCollectionBySchedule(@NotNull String scheduleName) {
        final String collection = TaskCollections.select(scheduleName);
        return getCollection(collection);
    }

    MongoCollection<Document> getCollection(@NotNull final String collectionName);

    <T> MongoCollection<T> getCollection(@NotNull String collectionName, @NotNull Class<T> clazz);
}
