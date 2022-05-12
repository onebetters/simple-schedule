package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-22 16:51.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class DefaultCollectionFactory implements CollectionFactory {
    private final MongoDatabase mongoDatabase;

    @Override
    public MongoCollection<Document> getCollection(@NotNull String collectionName) {
        return TaskCollections.ensureIndex(mongoDatabase.getCollection(collectionName));
    }

    @Override
    public <T> MongoCollection<T> getCollection(@NotNull String collectionName, @NotNull Class<T> clazz) {
        return TaskCollections.ensureIndex(mongoDatabase.getCollection(collectionName, clazz));
    }
}
