package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * 单个单个保存。未来如果需要考虑性能，可以队列批量保存(或者直接桥接mq异步对接，mq不就是干这事的嘛).
 *
 * <p>Date: 2022-03-22 17:05.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class SingleMongoTaskHistoryBackup implements MongoTaskHistoryBackup {

    private final CollectionFactory collectionFactory;
    private final DocumentConverter documentConverter;

    public SingleMongoTaskHistoryBackup(CollectionFactory collectionFactory) {
        this(collectionFactory, new DefaultDocumentConverter());
    }

    @Override
    public void backup(
            @NotNull final String scheduleName, @NotNull TaskElement task, @Nullable final Map<String, Object> extend) {
        final String hisCollection = TaskCollections.selectHistory(scheduleName);
        final Document saved = documentConverter.toDocument(task);
        if (Objects.nonNull(extend) && !extend.isEmpty()) {
            extend.forEach((k, v) -> {
                if (Objects.nonNull(v)) {
                    saved.append(k, v);
                }
            });
        }
        collectionFactory.getCollection(hisCollection).insertOne(saved);
    }
}
