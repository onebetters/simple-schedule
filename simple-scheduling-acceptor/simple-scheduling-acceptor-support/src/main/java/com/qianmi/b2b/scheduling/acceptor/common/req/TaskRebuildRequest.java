package com.qianmi.b2b.scheduling.acceptor.common.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * 销毁重建任务
 * <p>Date: 2022-04-21 12:54.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskRebuildRequest extends TaskSubmitRequest implements TaskRequest {
    private static final long serialVersionUID = 1909008020437387983L;

    /**
     * 唯一索引。用于匹配任务
     */
    @NotNull
    private String uniqueIndex;
}
