package com.qianmi.b2b.scheduling.core;

import com.qianmi.b2b.commons.utils.range.Range;
import com.qianmi.b2b.scheduling.core.taskqueue.RemoveRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * 任务存储
 * <p>Date: 2022-04-07 17:58.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface TaskRepository {

    /**
     * 批量提交任务。<br>
     * 存储层如果支持批量insert，可以重写该方法以提高保存性能
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param tasks        待提交任务
     */
    default void save(@NotNull final String scheduleName, @NotNull List<TaskElement> tasks) {
        tasks.forEach(task -> save(scheduleName, task));
    }

    /**
     * 提交任务
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param task         待提交任务
     */
    @NotNull TaskElement save(@NotNull final String scheduleName, @NotNull TaskElement task);

    /**
     * 删除任务
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param task         待删除任务
     */
    @Nullable TaskElement remove(@NotNull final String scheduleName, @NotNull RemoveRequest request);

    /**
     * 根据索引标签批量检索(未执行)任务.<br>
     * 备注：实现方自行实现是游标式查询(建议)还是分页式查询(不建议，你懂的，很多存储中间件，数据量大时性能可能是个问题)，无所谓
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param template     任务模板
     * @param index        索引标签
     * @return 任务列表
     */
    @NotNull Stream<TaskElement> findByIndex(
            @NotNull final String scheduleName, @NotNull final String template, @NotNull String index);

    /**
     * 根据任务id批量查询任务信息
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param ids          任务唯一id
     * @return 匹配的任务列表
     */
    @NotNull List<TaskElement> findByIds(@NotNull final String scheduleName, @NotNull Collection<String> ids);

    /**
     * 根据任务期望触发时间范围区间检索任务
     *
     * @param scheduleName 任务调度器分组，若有需要，可以规划用于底层存储数据分组
     * @param range        期望触发时间范围，开始结束时间至少必传一个。
     * @return 匹配的任务
     */
    @NotNull Stream<TaskElement> findByExpectedFireTimeBetween(
            @NotNull final String scheduleName, @NotNull Range<Long> range);
}
