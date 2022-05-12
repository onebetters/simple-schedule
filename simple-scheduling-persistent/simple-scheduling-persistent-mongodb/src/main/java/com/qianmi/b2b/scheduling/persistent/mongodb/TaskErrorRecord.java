package com.qianmi.b2b.scheduling.persistent.mongodb;

import com.qianmi.b2b.commons.utils.exception.StackTraceUtils;
import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * <p>Date: 2022-04-11 09:28.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class TaskErrorRecord implements Serializable {
    private static final long serialVersionUID = 6094985989220766260L;

    @BsonId
    private String id;

    @BsonProperty
    private String taskId;

    @BsonProperty
    private LocalDateTime createTime;

    @BsonProperty
    private String cause;

    public static TaskErrorRecord from(@NotNull TaskElement task, @NotNull Throwable cause) {
        final TaskErrorRecord error = new TaskErrorRecord();
        error.setTaskId(task.getId());
        error.setCreateTime(LocalDateTime.now());
        error.setCause(StackTraceUtils.stackTrace(cause, 8192, StandardCharsets.UTF_8));
        return error;
    }
}
