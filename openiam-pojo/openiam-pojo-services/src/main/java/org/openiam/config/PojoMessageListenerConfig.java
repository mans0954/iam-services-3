package org.openiam.config;

import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.openiam.mq.MetaDataListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

/**
 * Created by alexander on 12/07/16.
 */
@Configuration
public class PojoMessageListenerConfig {

    @Autowired
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    @Bean
    @Autowired
    public SimpleMessageListenerContainer metaDataListenerContainer(MetaDataListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("metaDataListenerContainer",
                OpenIAMQueue.MetadataQueue,  listener, connectionFactory, String.format("AMQP-%s-", OpenIAMQueue.MetadataQueue.getName()));
    }
}
