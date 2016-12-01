package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.activiti.ActivitiServiceQueue;
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
public class ActivitiVHostConfig extends BaseVHostConfig{

    @Bean
    public ConnectionFactory activitiCF() {
        return createConnectionFactory(RabbitMQVHosts.ACTIVITI_HOST);
    }

    @Bean(name = "activitiRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory activitiRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(activitiCF());
    }
    @Bean
    public AmqpAdmin activitiAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(activitiCF());
        return amqpAdmin;
    }
    @Bean
    public ActivitiServiceQueue ActivitiServiceQueue() {
        ActivitiServiceQueue queue =  new ActivitiServiceQueue();
        bindQueue(activitiAmqpAdmin(), queue);
        return queue;
    }
}
