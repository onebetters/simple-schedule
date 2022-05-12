package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.mongodb.MongoServerException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.qianmi.b2b.commons.utils.system.NetworkUtils;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackLocker;
import com.qianmi.b2b.scheduling.core.callback.TaskLockContext;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * <p>Date: 2022-04-11 13:43.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class MongoTaskCallbackLocker implements TaskCallbackLocker {

    @NotNull
    private final CollectionFactory collectionFactory;

    @Override
    public boolean lock(@NotNull TaskLockContext lockContext) {
        final LocalDateTime now = LocalDateTime.now();
        final String collectionName = TaskCollections.selectLock();
        final MongoCollection<Document> collection = collectionFactory.getCollection(collectionName);

        final Bson filter = Filters.and(Filters.eq(DefaultDocumentConverter.FIELD_ID, lockContext.identifier()),
                Filters.lte("lockUntil", now)
        );
        final Bson update = combine(set("lockUntil", now.plus(lockContext.getTtlMs(), ChronoUnit.MILLIS)),
                set("lockedAt", now),
                set("lockedBy", NetworkUtils.getLocalIpString().orElse("127.0.0.1"))
        );
        try {
            // 记录不存在，insert，锁成功；
            // 记录存在，lockUtil <= now，update，锁成功；
            // 记录存在，lockUtil > now，主键冲突，锁失败；
            collection.findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().upsert(true));
            return true;
        } catch (MongoServerException e) {
            if (e.getCode() == 11000) { // duplicate key
                return false;
            } else {
                throw e;
            }
        }
    }
}
