package org.openiam.authmanager.config;

import org.openiam.mq.AMAdminQueueListener;
import org.openiam.mq.AMManagerQueueListener;
import org.openiam.mq.AMMenuQueueListener;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alexander on 12/07/16.
 */
@Configuration
public class AuthManagerMessageListenerConfig {

    @Autowired
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    @Bean
    @Autowired
    public SimpleMessageListenerContainer amAdminQueueListenerContainer(AMAdminQueueListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("amAdminQueueListenerContainer",
                OpenIAMQueue.AMAdminQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer amMenuQueueListenerContainer(AMMenuQueueListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("amMenuQueueListenerContainer",
                OpenIAMQueue.AMMenuQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer amManagerQueueListenerContainer(AMManagerQueueListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("amManagerQueueListenerContainer",
                OpenIAMQueue.AMManagerQueue,  listener, connectionFactory);
    }
}
