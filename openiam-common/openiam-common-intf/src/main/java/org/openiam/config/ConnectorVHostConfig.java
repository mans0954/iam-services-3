package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
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
public class ConnectorVHostConfig extends BaseVHostConfig{
    @Bean
    public ConnectionFactory connectorCF() {
        return createConnectionFactory(RabbitMQVHosts.CONNECTOR_HOST);
    }

    @Bean(name = "connectorRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory connectorRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(connectorCF());
    }

    @Bean
    public AmqpAdmin connectorAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(connectorCF());
        return amqpAdmin;
    }
}
