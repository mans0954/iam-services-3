package org.openiam.config;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.consumer.AbstractMessageListener;
import org.openiam.message.consumer.AbstractRedisMessageListener;
import org.openiam.message.utils.KafkaAdmin;
import org.openiam.mq.MetaDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

/**
 * Created by alexander on 12/07/16.
 */
@Configuration
public class PojoMessageListenerConfig {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Bean
    @Autowired
    public ConcurrentMessageListenerContainer metaDataListenerContainer(MetaDataListener listener) {
        return kafkaAdmin.createMessageListenerContainer(OpenIAMQueue.MetadataQueue, listener);
    }
}
