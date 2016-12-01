package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.idm.ManagedSysQueue;
import org.openiam.mq.constants.queue.idm.ProvisionQueue;
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
public class IdmVHostConfig extends BaseVHostConfig{
    @Bean
    public ConnectionFactory idmCF() {
        return createConnectionFactory(RabbitMQVHosts.IDM_HOST);
    }

    @Bean(name = "idmRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory idmRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(idmCF());
    }
    @Bean
    public AmqpAdmin idmAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(idmCF());
        return amqpAdmin;
    }

    @Bean
    public ProvisionQueue ProvisionQueue() {
        ProvisionQueue queue =  new ProvisionQueue();
        bindQueue(idmAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public ManagedSysQueue ManagedSysQueue() {
        ManagedSysQueue queue =  new ManagedSysQueue();
        bindQueue(idmAmqpAdmin(), queue);
        return queue;
    }
}
