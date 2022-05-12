package com.qianmi.b2b.scheduling.acceptor.common.req;

import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 取消任务
 * <p>Date: 2022-04-06 14:13.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCancelRequest implements TaskRequest {
    private static final long serialVersionUID = 8753558188911609900L;

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

    public String getTemplate() {
        return StringUtils.defaultIfBlank(template, TaskElement.DEFAULT_TEMPLATE);
    }
}
