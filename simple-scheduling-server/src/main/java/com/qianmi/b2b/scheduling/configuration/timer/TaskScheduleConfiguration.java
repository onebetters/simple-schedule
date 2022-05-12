package com.qianmi.b2b.scheduling.configuration.timer;

import com.qianmi.b2b.scheduling.acceptor.common.req.TaskRequest;
import com.qianmi.b2b.scheduling.acceptor.jms.activemq.*;
import com.qianmi.b2b.scheduling.configuration.timer.callback.*;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TemplateManager;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerProperties;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailedRepository;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailover;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackLocker;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackSelector;
import com.qianmi.b2b.scheduling.core.schedule.DelayedOperationPurgatoryBuilder;
import com.qianmi.b2b.scheduling.core.schedule.SimpleTaskScheduleSelector;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleBuilder;
import com.qianmi.b2b.scheduling.core.schedule.TaskScheduleSelector;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.util.List;

/**
 * <p>Date: 2022-03-22 18:34.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10000)
@Configuration(proxyBeanMethods = false)
public class TaskScheduleConfiguration {

    /**
     * 任务模板管理器
     *
     * @param properties 配置信息
     * @return 任务模板管理器
     */
    @Bean
    TemplateManager templateFactory(TimerProperties properties) {
        return new TemplateManager(properties);
    }

    /**
     * 延时队列创建器
     *
     * @param templateManager 任务模板管理器
     * @return 延时队列创建器
     */
    @Bean
    DelayedOperationPurgatoryBuilder delayedOperationPurgatoryBuilder(TemplateManager templateManager) {
        return new ConfigurablePurgatoryBuilder(templateManager);
    }

    @Bean
    BrokerManager brokerManager(final JmsMessagingTemplate jmsMessagingTemplate) {
        return new BrokerManager(jmsMessagingTemplate);
    }

    /**
     * ActiveMq回调
     */
    @Bean
    TaskCallbackFactory activeMqTaskCallbackFactory(final BrokerManager brokerManager) {
        return new ActiveMqTaskCallbackFactory(brokerManager);
    }

    /**
     * 回调管理器
     */
    @Bean
    TaskCallbackManager taskCallbackFactory(final List<TaskCallbackFactory> factories) {
        return new TaskCallbackManager(factories);
    }

    /**
     * 回调逻辑选择器
     *
     * @param templateManager 任务模板管理器
     * @return 回调逻辑选择器
     */
    @Bean
    TaskCallbackSelector taskCallbackSelector(
            TaskCallbackManager taskCallbackManager, TemplateManager templateManager) {
        return new ConfigurableTaskCallbackSelector(taskCallbackManager, templateManager);
    }

    @Bean
    CallbackFailover callbackFailover(CallbackFailedRepository callbackFailedRepository) {
        return new RetryableCallbackFailover(callbackFailedRepository, new DefaultRetryableExpression());
    }

    /**
     * 任务调度器构建器
     */
    @Bean
    TaskScheduleBuilder taskScheduleBuilder(
            TaskRepository taskRepository,
            TaskCallbackSelector callbackSelector,
            TaskCallbackLocker taskCallbackLocker,
            CallbackFailover callbackFailover,
            TemplateManager templateManager) {
        return new TaskScheduleBuilder(taskRepository,
                callbackSelector,
                taskCallbackLocker,
                callbackFailover,
                scheduleName -> templateManager.findTimerWheel(scheduleName).getKeepInMemoryDelayBeforeMinutes()
        );
    }

    /**
     * 任务队列选择器
     *
     * @param purgatoryBuilder    时间轮队列构建器
     * @param taskScheduleBuilder 任务调度器构建器
     * @return 任务调度器
     */
    @Bean
    TaskScheduleSelector taskScheduleFactory(
            DelayedOperationPurgatoryBuilder purgatoryBuilder,
            TaskScheduleBuilder taskScheduleBuilder,
            TemplateManager templateManager) {
        return new SimpleTaskScheduleSelector(purgatoryBuilder,
                taskScheduleBuilder,
                taskTemplate -> templateManager.findTimerWheelByTemplate(taskTemplate).getKey()
        );
    }

    @Bean
    ActiveMqSubmitAcceptor activeMqSubmitAcceptor(final TaskScheduleSelector scheduleSelector) {
        return new ActiveMqSubmitAcceptor(scheduleSelector);
    }

    @Bean
    ActiveMqCanceller activeMqCanceller(
            final TaskRepository taskRepository, final TaskScheduleSelector taskScheduleSelector) {
        return new ActiveMqCanceller(taskRepository, taskScheduleSelector);
    }

    @Bean
    ActiveMqReset activeMqReset(final TaskRepository taskRepository, final TaskScheduleSelector taskScheduleSelector) {
        return new ActiveMqReset(taskRepository, taskScheduleSelector);
    }

    @Bean
    ActiveMqRebuildAcceptor activeMqRebuildAcceptor(
            final TaskRepository taskRepository,
            final TaskScheduleSelector taskScheduleSelector) {
        return new ActiveMqRebuildAcceptor(taskRepository, taskScheduleSelector);
    }

    @Bean
    ActiveMqAcceptor activeMqAcceptor(final List<ActiveMqTaskRequestHandler<? extends TaskRequest>> activeMqTaskRequestHandlers) {
        return new ActiveMqAcceptor(activeMqTaskRequestHandlers);
    }
}
