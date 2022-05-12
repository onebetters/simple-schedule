package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.Sorts;
import com.qianmi.b2b.commons.utils.range.Range;
import com.qianmi.b2b.commons.utils.stream.StreamUtils;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.taskqueue.RemoveRequest;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;
import static com.qianmi.b2b.scheduling.persistent.mongodb.DefaultDocumentConverter.FIELD_EXPECTED_FIRE_MS;
import static com.qianmi.b2b.scheduling.persistent.mongodb.DefaultDocumentConverter.FIELD_ID;

/**
 * <p>Date: 2022-04-07 18:07.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class MongoTaskRepository implements TaskRepository {

    @NotNull
    private final CollectionFactory collectionFactory;
    @NotNull
    private final DocumentConverter documentConverter;
    @Nullable
    private final MongoTaskHistoryBackup mongoTaskHistoryBackup;

    public MongoTaskRepository(
            @NotNull CollectionFactory collectionFactory, @Nullable MongoTaskHistoryBackup mongoTaskHistoryBackup) {
        this(collectionFactory, new DefaultDocumentConverter(), mongoTaskHistoryBackup);
    }

    @Override
    public @NotNull TaskElement save(@NotNull final String scheduleName, @NotNull TaskElement task) {
        final Document saved = documentConverter.toDocument(task);
        collectionFactory.getCollectionBySchedule(scheduleName).insertOne(saved);
        return task;
    }

    @Override
    public @Nullable TaskElement remove(@NotNull final String scheduleName, @NotNull RemoveRequest request) {
        final String taskId = request.getTaskId();
        final Document document = collectionFactory.getCollectionBySchedule(scheduleName)
                .findOneAndDelete(eq(FIELD_ID, taskId), new FindOneAndDeleteOptions());
        if (Objects.nonNull(document) && Objects.nonNull(mongoTaskHistoryBackup)) {
            final TaskElement task = documentConverter.toTask(document);
            final Map<String, Object> extend = Map.of("removeType", request.getRemoveType().name());
            mongoTaskHistoryBackup.backup(scheduleName, task, extend);
            return task;
        }
        return null;
    }

    @Override
    public @NotNull Stream<TaskElement> findByIndex(
            @NotNull final String scheduleName, @NotNull String template, @NotNull String index) {
        return find(scheduleName,
                and(
                        eq(TaskElement.Fields.template, template),
                        elemMatch(TaskElement.Fields.indexes,
                                StreamUtils.map(List.of(index), a -> "$eq", v -> v, Document::new)
                        )
                )
        );
    }

    @Override
    public @NotNull List<TaskElement> findByIds(@NotNull final String scheduleName, @NotNull Collection<String> ids) {
        final FindIterable<Document> documents = collectionFactory.getCollectionBySchedule(scheduleName)
                .find(in(FIELD_ID, ids));
        return StreamUtils.list(documents, documentConverter::toTask);
    }

    @Override
    public @NotNull Stream<TaskElement> findByExpectedFireTimeBetween(
            @NotNull String scheduleName, @NotNull Range<Long> range) {

        final List<Bson> filters = new ArrayList<>(2);
        range.consumerFrom((included, from) -> {
            if (Boolean.TRUE.equals(included)) {
                filters.add(gte(FIELD_EXPECTED_FIRE_MS, from));
            } else {
                filters.add(gt(FIELD_EXPECTED_FIRE_MS, from));
            }
        });
        range.consumerTo((included, to) -> {
            if (Boolean.TRUE.equals(included)) {
                filters.add(lte(FIELD_EXPECTED_FIRE_MS, to));
            } else {
                filters.add(lt(FIELD_EXPECTED_FIRE_MS, to));
            }
        });

        return find(scheduleName, and(filters));
    }

    // @SuppressWarnings("deprecation")
    private @NotNull Stream<TaskElement> find(@NotNull final String scheduleName, @NotNull final Bson filter) {
        final FindIterable<Document> documents = collectionFactory.getCollectionBySchedule(scheduleName)
                .find(filter)
                .sort(Sorts.ascending(FIELD_ID))
                .batchSize(1000)
                // .cursorType(CursorType.Tailable)
                .noCursorTimeout(true)
                // .oplogReplay(true)
                ;
        return StreamUtils.of(documents).map(documentConverter::toTask);
    }
}
