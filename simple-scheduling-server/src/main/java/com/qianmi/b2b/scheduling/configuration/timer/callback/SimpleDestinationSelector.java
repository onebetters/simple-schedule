package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.callback.activemq.DestinationSelector;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.CallbackProperties;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerCallbackActiveMq;
import com.qianmi.b2b.scheduling.core.TaskElement;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskDestination;
import com.qianmi.b2b.scheduling.core.extra.JmsTaskExtra;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.jms.Destination;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p>Date: 2022-03-24 16:31.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
public class SimpleDestinationSelector implements DestinationSelector {

    private final CallbackProperties properties;
    private final Function<String, Destination> topic;
    private final Function<String, Destination> queue;

    @Override
    public Destination select(@NotNull TaskElement task) {
        final Optional<Destination> fromConfig = buildFromConfig(properties.getActiveMq());
        if (fromConfig.isPresent()) {
            return fromConfig.get();
        }
        if (StringUtils.isNotBlank(task.getExtra())) {
            final Optional<JmsTaskExtra> extraOp = task.pickExtra();
            final JmsTaskDestination destinationExtra = extraOp.map(JmsTaskExtra::getDestination).orElse(null);
            if (Objects.nonNull(destinationExtra)) {
                return buildFromExtra(destinationExtra);
            }
        }
        throw new DestinationNoFoundException(task);
    }

    private Destination buildFromExtra(@NotNull final JmsTaskDestination extra) {
        return extra.isPubSubDomain() ? topic.apply(extra.getDestination()) : queue.apply(extra.getDestination());
    }

    private Optional<Destination> buildFromConfig(TimerCallbackActiveMq config) {
        return Optional.ofNullable(config)
                .filter(m -> StringUtils.isNotBlank(m.getDestination()))
                .map(this::buildDestination);
    }

    private Destination buildDestination(@NotNull TimerCallbackActiveMq mq) {
        return mq.isPubSubDomain() ? topic.apply(mq.getDestination()) : queue.apply(mq.getDestination());
    }
}
