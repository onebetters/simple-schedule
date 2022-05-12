package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.callback.activemq.ActiveMqCallback;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackProperties;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackType;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerCallbackActiveMq;
import com.qianmi.b2b.scheduling.core.callback.TaskCallback;
import com.qianmi.b2b.scheduling.core.TaskElement;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * <p>Date: 2022-03-24 17:00.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class ActiveMqTaskCallbackFactory implements TaskCallbackFactory {
    private final BrokerManager brokerManager;

    @Override
    public @NotNull CallbackType type() {
        return CallbackType.JMS_ACTIVEMQ;
    }

    @Override
    public @Nullable TaskCallback get(@NotNull CallbackProperties properties, @NotNull TaskElement task) {
        return new ActiveMqCallback(
                new ByBrokerJmsMessagingTemplateSelector(brokerManager, this.brokerFromConfig(properties).orElse(null)),
                new SimpleDestinationSelector(properties, ActiveMQTopic::new, ActiveMQQueue::new)
        );
    }

    private Optional<String> brokerFromConfig(@NotNull CallbackProperties properties) {
        return Optional.ofNullable(properties.getActiveMq())
                .map(TimerCallbackActiveMq::getBroker)
                .filter(StringUtils::isNotBlank);
    }
}
