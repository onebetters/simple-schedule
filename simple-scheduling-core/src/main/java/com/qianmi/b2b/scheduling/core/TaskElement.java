package com.qianmi.b2b.scheduling.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qianmi.b2b.scheduling.core.extra.TaskExtra;
import com.qianmi.b2b.scheduling.core.supports.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

import static com.qianmi.b2b.scheduling.core.supports.TaskIds.next;
import static java.lang.System.currentTimeMillis;

/**
 * <p>Date: 2022-03-22 11:40.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class TaskElement implements Serializable {
    private static final long serialVersionUID = 129981226874205100L;
    public static final String DEFAULT_TEMPLATE = "common";

    /**
     * 任务唯一ID
     */
    private String id = next();
    /**
     * 任务创建时间，毫秒
     */
    private long timestamp = currentTimeMillis();
    /**
     * 任务模板编码
     */
    private String template = DEFAULT_TEMPLATE;
    /**
     * 期望任务延时时间长度，毫秒。
     * 表示该时间长度后，开始调度执行任务
     */
    private long delayMs;
    /**
     * 任务内容，主要业务使用，一般是业务参数
     */
    private String payload;
    /**
     * 索引关系。非必传。<br/>
     * 典型的用法是用于可取消型任务，后续能根据该索引关系，匹配到之前添加的一条/多条任务进行取消；<br/>
     */
    private Collection<String> indexes;
    /**
     * 扩展参数，约定为json格式，主要是用于扩展辅助一些内部逻辑执行，如回调
     */
    private String extra;

    @NotNull
    public String getTemplate() {
        if (StringUtils.isBlank(template)) {
            template = DEFAULT_TEMPLATE;
        }
        return template;
    }

    /**
     * 期望任务触发时间
     *
     * @return 该任务的期望触发时间，单位：毫秒
     */
    @JsonIgnore
    public long getExpectedFireMs() {
        return timestamp + delayMs;
    }

    public <T extends TaskExtra> Optional<T> pickExtra() {
        if (StringUtils.isNotBlank(extra)) {
            final TaskExtra extraBean = JsonUtils.parseObject(extra, TaskExtra.class);
            if (Objects.nonNull(extraBean)) {
                @SuppressWarnings("unchecked") final T result = (T) extraBean;
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    public TaskElement resetDelayMs(final long delayMsDiff) {
        final TaskElement task = copy(this);
        task.setId(next());
        // 此处可能为负数，不用care
        task.setDelayMs(delayMs + delayMsDiff);
        return task;
    }

    public static TaskElement copy(TaskElement source) {
        final TaskElement target = new TaskElement();
        target.setTimestamp(source.timestamp);
        target.setTemplate(source.template);
        target.setDelayMs(source.delayMs);
        target.setPayload(source.payload);
        if (null != source.indexes && !source.indexes.isEmpty()) {
            target.setIndexes(new LinkedHashSet<>(source.indexes));
        }
        target.setExtra(source.extra);
        return target;
    }
}