package com.qianmi.b2b.scheduling.callback.activemq;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.JmsMessagingTemplate;

/**
 * <p>Date: 2022-03-24 15:37.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public interface JmsMessagingTemplateSelector {

    @NotNull JmsMessagingTemplate select(@NotNull TaskElement task);
}
