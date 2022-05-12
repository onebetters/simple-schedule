package com.qianmi.b2b.scheduling.configuration.timer.callback;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.jms.core.JmsMessagingTemplate;

/**
 * <p>Date: 2022-03-25 12:11.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class BrokerManager {

    private static final String DEFAULT_BROKER = "default";
    private final JmsMessagingTemplate jmsMessagingTemplate;

    JmsMessagingTemplate get(@Nullable final String broker) {
        final String brokerUse = StringUtils.defaultIfBlank(broker, DEFAULT_BROKER);

        return jmsMessagingTemplate; // TODO
    }
}
