package com.qianmi.b2b.scheduling.core.extra;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2022-03-25 11:30.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Data
public class JmsTaskDestination implements Serializable {
    private static final long serialVersionUID = 8135023033238627633L;

    /**
     * 回调mq broker，可以是地址，也可以是地址模板
     */
    private String broker;

    /**
     * 回调目标
     */
    private String destination;

    /**
     * true为topic，false为queue
     */
    private boolean pubSubDomain;
}
