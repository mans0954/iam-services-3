package org.openiam.config;

import org.openiam.mq.ManagedSystemMessageListener;
import org.openiam.mq.MetaDataListener;
import org.openiam.mq.ProvisionMessageListener;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alexander on 01/08/16.
 */
@Configuration
public class IdmMessageListenerConfig {

    @Autowired
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    @Bean
    @Autowired
    public SimpleMessageListenerContainer provisionListenerContainer(ProvisionMessageListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("provisionListenerContainerContainer",
                OpenIAMQueue.ProvisionQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer managedSystemMessageListenerContainer(ManagedSystemMessageListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("managedSystemMessageListenerContainer",
                OpenIAMQueue.ManagedSysQueue,  listener, connectionFactory);
    }


}
