package com.qianmi.b2b.scheduling.acceptor.common.req;

import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskExtra;
import com.qianmi.b2b.scheduling.core.supports.json.JsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 提交新任务
 * <p>Date: 2022-03-25 10:12.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class TaskSubmitRequest implements TaskRequest {
    private static final long serialVersionUID = -4944869251625996500L;

    /**
     * 任务模板编码。非必传。
     * 但建议预先定义好并传送该值，方便后续维护扩展。
     */
    private String template;

    /**
     * 延时时间长度，单位：毫秒。 表示该时间长度后，开始调度执行任务
     * 接受非负整数值。如果不传，将从jms消息的JMSExpiration自动判断。
     */
    private long delayMs;

    /**
     * jms任务内容。内容随意，由业务方自己约定即使用。
     * 回调时，将直接回传该内容（TextMessage格式）给业务方
     */
    private String payload;

    /**
     * 索引关系。非必传。<br/>
     * 典型的用法是用于可取消型任务，后续能根据该索引关系，匹配到之前添加的一条/多条任务进行取消；<br/>
     */
    private Collection<String> indexes;

    /**
     * 扩展信息。建议非必传，通过{@link #template 任务模板编码}预先配置。
     * 如果未配置任务模板，回调mq默认为消息发送方的broker地址。
     * <p>
     * 回调destination，按优先级自动尝试获取：
     * 1、template模板预先配置好的destination；
     * 2、消息发送者设置好该字段；
     * 3、消息发送者设置的JMSReplyTo；
     * 4、任务添加出错。
     */
    private JmsTaskExtra extra;

    public TaskElement toTask() {
        final TaskElement task = new TaskElement();
        if (StringUtils.isNotBlank(template)) {
            task.setTemplate(template);
        }
        task.setDelayMs(delayMs);
        task.setPayload(payload);
        task.setIndexes(indexes);
        if (Objects.nonNull(extra)) {
            task.setExtra(JsonUtils.toJson(extra));
        }
        return task;
    }
}
