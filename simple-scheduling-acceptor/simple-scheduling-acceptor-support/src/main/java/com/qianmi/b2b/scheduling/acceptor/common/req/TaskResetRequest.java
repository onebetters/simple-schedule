package com.qianmi.b2b.scheduling.acceptor.common.req;

import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 重置任务延时时间
 *
 * <p>Date: 2022-04-06 16:18.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResetRequest implements TaskRequest {
    private static final long serialVersionUID = -2700921770400759847L;

    /**
     * 任务模板编码。非必传。
     * 但建议预先定义好并传送该值，方便后续维护扩展。
     */
    private String template;

    /**
     * 索引关系。必传。<br/>
     * 用于批量检索任务信息
     */
    @NotNull
    private String index;

    /**
     * 时间差异调整，单位：毫秒。<br>
     * 如1000表示在原计划延时时间基础上，再延后1秒执行任务。<br>
     * -5000，表示比之前计划的再提早5秒执行
     */
    private long delayMsDiff;

    public String getTemplate() {
        return StringUtils.defaultIfBlank(template, TaskElement.DEFAULT_TEMPLATE);
    }

    public boolean shouldReset() {
        return 0 != delayMsDiff;
    }
}
