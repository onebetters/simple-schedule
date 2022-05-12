package com.qianmi.b2b.scheduling.configuration.timer.prperties;

/**
 * <p>Date: 2022-03-24 16:54.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public enum CallbackType {

    /**
     * ActiveMq回调
     */
    JMS_ACTIVEMQ,

    /**
     * HTTP接口回调
     */
    REST,

    /**
     * rpc dubbo接口回调
     */
    RPC_DUBBO
}
