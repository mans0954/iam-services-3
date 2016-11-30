package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.user.UserAttributeQueue;
import org.openiam.mq.constants.queue.user.UserServiceQueue;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alexander on 18/11/16.
 */
@Configuration
public class UserVHostConfig extends BaseVHostConfig{
    @Bean
    public ConnectionFactory userCF() {
        return createConnectionFactory(RabbitMQVHosts.USER_HOST);
    }
    @Bean(name = "userRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory userRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(userCF());
    }

    @Bean
    public AmqpAdmin userAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(userCF());
        return amqpAdmin;
    }

    @Bean
    public UserAttributeQueue UserAttributeQueue() {
        UserAttributeQueue queue =  new UserAttributeQueue();
        bindQueue(userAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public UserServiceQueue UserServiceQueue() {
        UserServiceQueue queue =  new UserServiceQueue();
        bindQueue(userAmqpAdmin(), queue);
        return queue;
    }
}
