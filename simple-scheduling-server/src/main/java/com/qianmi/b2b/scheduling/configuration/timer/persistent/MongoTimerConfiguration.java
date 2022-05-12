package com.qianmi.b2b.scheduling.configuration.timer.persistent;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerPersistentMongo;
import com.qianmi.b2b.scheduling.configuration.timer.prperties.TimerProperties;
import com.qianmi.b2b.scheduling.core.TaskRepository;
import com.qianmi.b2b.scheduling.core.callback.CallbackFailedRepository;
import com.qianmi.b2b.scheduling.core.callback.TaskCallbackLocker;
import com.qianmi.b2b.scheduling.persistent.mongodb.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>Date: 2022-03-22 17:45.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 1000)
@ConditionalOnClass(MongoClient.class)
@ConditionalOnProperty(prefix = "timer", name = "persistent", havingValue = "MONGO")
@EnableConfigurationProperties(TimerProperties.class)
public class MongoTimerConfiguration {

    @Bean
    MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder.codecRegistry(CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        ));
    }

    @Bean
    // @RefreshScope
    @ConfigurationProperties(prefix = "timer.persistent.mongo")
    TimerPersistentMongo persistentMongo() {
        return new TimerPersistentMongo();
    }

    @Bean
    CollectionFactory mongoCollectionFactory(MongoTemplate mongoTemplate) {
        return new DefaultCollectionFactory(mongoTemplate.getDb());
    }

    @Bean
    MongoTaskHistoryBackup mongoTaskHistoryBackup(
            TimerPersistentMongo timerPersistentMongo, CollectionFactory mongoCollectionFactory) {
        if (timerPersistentMongo.isBackupHistory()) {
            return new SingleMongoTaskHistoryBackup(mongoCollectionFactory);
        } else {
            return new NoneTaskHistoryBackup();
        }
    }

    @Bean
    TaskRepository taskRepository(CollectionFactory mongoCollectionFactory, MongoTaskHistoryBackup historyBackup) {
        return new MongoTaskRepository(mongoCollectionFactory, historyBackup);
    }

    @Bean
    CallbackFailedRepository callbackFailedRepository(CollectionFactory mongoCollectionFactory) {
        return new MongoCallbackFailedRepository(mongoCollectionFactory);
    }

    @Bean
    TaskCallbackLocker taskCallbackLocker(CollectionFactory mongoCollectionFactory) {
        return new MongoTaskCallbackLocker(mongoCollectionFactory);
    }
}
