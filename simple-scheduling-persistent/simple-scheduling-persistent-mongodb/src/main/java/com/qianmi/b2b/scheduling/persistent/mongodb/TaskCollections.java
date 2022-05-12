package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.qianmi.b2b.commons.utils.time.DateTimeUtils;
import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 任务对应collection表选择器
 *
 * <p>Date: 2022-03-22 17:08.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@UtilityClass
class TaskCollections {
    private static final DoubleCheckLock<String> INDEX_LOCK = new DoubleCheckLock<>();

    /**
     * 任务记录前缀
     */
    static final String COLLECTION_PREFIX = "task_record_";
    /**
     * 默认任务记录表
     */
    static final String COLLECTION_DEFAULT = COLLECTION_PREFIX + "common";
    /**
     * 任务历史备份表
     */
    static final String HIS_COLLECTION_SUFFIX = "_his";
    /**
     * 回调失败日志记录
     */
    static final String ERROR_COLLECTION_RECORD = "task_error_record";
    /**
     * 最终回调失败的。基本上是重试了N遍，仍然还失败的任务【除非配置为不允许重试】
     */
    static final String ERROR_COLLECTION = "task_error";
    static final String TASK_LOCK_COLLECTION = "task_lock";

    private static Optional<CollectionType> deduceCollectionType(final String collectionName) {
        if (StringUtils.startsWith(collectionName, ERROR_COLLECTION_RECORD)) {
            return Optional.of(CollectionType.ERROR_RECORD);
        } else if (StringUtils.equals(collectionName, ERROR_COLLECTION)) {
            return Optional.of(CollectionType.ERROR);
        } else if (StringUtils.startsWith(collectionName, COLLECTION_PREFIX)) {
            if (StringUtils.endsWith(collectionName, HIS_COLLECTION_SUFFIX)) {
                return Optional.of(CollectionType.TASK_HIS);
            } else {
                return Optional.of(CollectionType.TASK);
            }
        }
        return Optional.empty();
    }

    /**
     * @param scheduleName 调度器名称
     * @return 任务表名称
     */
    public static String select(@Nullable String scheduleName) {
        return Optional.ofNullable(scheduleName)
                .filter(StringUtils::isNotBlank)
                .map(e -> COLLECTION_PREFIX + e)
                .orElse(COLLECTION_DEFAULT);
    }

    /**
     * 执行成功后历史表备份
     *
     * @param scheduleName 调度器名称
     * @return 任务历史备份表名称
     */
    public static String selectHistory(@NotNull String scheduleName) {
        return select(scheduleName) + HIS_COLLECTION_SUFFIX;
    }

    /**
     * 回调失败日志表
     */
    public static String selectErrorRecord(long taskBaseTimeMillis) {
        return ERROR_COLLECTION_RECORD + "_" + DateTimeUtils.toString(taskBaseTimeMillis, "yyyyMM");
    }

    /**
     * 回调失败工单表（如果配置需要重试，此处已经是经过重试但最终还是失败的记录）
     *
     * @return 失败工单表
     */
    public static String selectError() {
        return ERROR_COLLECTION;
    }

    /**
     * 任务锁
     */
    public static String selectLock() {
        return TASK_LOCK_COLLECTION;
    }

    /**
     * 自动创建索引
     */
    public static <T> MongoCollection<T> ensureIndex(MongoCollection<T> collection) {
        final String collectionName = collection.getNamespace().getCollectionName();
        INDEX_LOCK.apply(() -> collectionName,
                () -> deduceCollectionType(collectionName).ifPresent(type -> type.ensureIndex(collection))
        );
        return collection;
    }

    /**
     * mongodb collection任务表类型推断
     */
    enum CollectionType {
        TASK {
            @Override
            public <T> void ensureIndex(MongoCollection<T> collection) {
                collection.createIndex(Indexes.ascending(TaskElement.Fields.indexes), DEFAULT_INDEX_OPTIONS);
                collection.createIndex(Indexes.ascending(DefaultDocumentConverter.FIELD_EXPECTED_FIRE_MS),
                        DEFAULT_INDEX_OPTIONS
                );
            }
        }, TASK_HIS {
            @Override
            public <T> void ensureIndex(MongoCollection<T> collection) {
                collection.createIndex(Indexes.ascending(TaskElement.Fields.indexes), DEFAULT_INDEX_OPTIONS);
            }
        }, ERROR_RECORD {
            @Override
            public <T> void ensureIndex(MongoCollection<T> collection) {
                collection.createIndex(Indexes.ascending(TaskErrorRecord.Fields.taskId), DEFAULT_INDEX_OPTIONS);
            }
        }, ERROR {
            @Override
            public <T> void ensureIndex(MongoCollection<T> collection) {
                //
            }
        };

        private static final IndexOptions DEFAULT_INDEX_OPTIONS = new IndexOptions().background(true);

        public abstract <T> void ensureIndex(MongoCollection<T> collection);
    }

    private static class DoubleCheckLock<K> {
        private final Set<K> keys = ConcurrentHashMap.newKeySet();
        private final Object lock = new Object();

        public void apply(final Supplier<K> key, final Runnable action) {
            final K k = key.get();
            if (!keys.contains(k)) {
                synchronized (lock) {
                    if (!keys.contains(k)) {
                        action.run();
                        keys.add(k);
                    }
                }
            }
        }
    }

}
