package com.qianmi.b2b.scheduling.acceptor.jms.activemq;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskAcceptType;
import com.qianmi.b2b.scheduling.acceptor.common.req.TaskRequest;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-04-21 10:26.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface ActiveMqTaskRequestHandler<T extends TaskRequest> {

    @NotNull
    TaskAcceptType acceptType();

    void handle(T request);
}
