package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.callback.activemq.JmsMessagingTemplateSelector;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskDestination;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskExtra;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>Date: 2022-03-25 11:35.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class ByBrokerJmsMessagingTemplateSelector implements JmsMessagingTemplateSelector {

    private final BrokerManager brokerManager;
    private final String templateBroker;

    @Override
    public @NotNull JmsMessagingTemplate select(@NotNull TaskElement task) {
        final String broker = this.selectBroker(task);
        return brokerManager.get(broker);
    }

    @Nullable
    private String selectBroker(@NotNull TaskElement task) {
        return Optional.ofNullable(templateBroker).flatMap(t -> brokerFromExtra(task)).orElse(null);
    }

    private Optional<String> brokerFromExtra(@NotNull TaskElement task) {
        final Optional<JmsTaskExtra> extraOp = task.pickExtra();
        final JmsTaskDestination destinationExtra = extraOp.map(JmsTaskExtra::getDestination).orElse(null);
        if (Objects.nonNull(destinationExtra)) {
            return Optional.of(destinationExtra.getBroker());
        }
        return Optional.empty();
    }
}
