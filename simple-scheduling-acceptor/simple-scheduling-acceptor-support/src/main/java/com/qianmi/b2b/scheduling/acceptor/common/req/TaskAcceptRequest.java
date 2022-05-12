package com.qianmi.b2b.scheduling.acceptor.common.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 统一的任务接收器，包含其他任务接收器.<br>
 * 主要使用场景：jms接收消息场景，不同队列接收消息并处理，理论上消费顺序没法保证。<br>
 * 比如：同一个业务ID，业务取消了之前的任务，立马又提交了任务。<br>
 * 如果是2个jms队列，理论上有可能先接收并处理了提交任务，再处理取消任务。
 *
 * <p>Date: 2022-04-21 10:07.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class TaskAcceptRequest<T extends TaskRequest> implements TaskRequest {
    private static final long serialVersionUID = 6765436044292159384L;

    /**
     * 任务提交类型，不区分大小写
     *
     * @see TaskAcceptType#name()
     */
    private String requestType;

    /**
     * 具体不同任务类型的提交请求内容
     *
     * @see TaskRequest
     */
    private T requestPayload;
}
