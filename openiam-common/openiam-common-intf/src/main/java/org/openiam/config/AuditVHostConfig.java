package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
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
public class AuditVHostConfig extends BaseVHostConfig{
    @Bean
    public ConnectionFactory auditCF() {
        return createConnectionFactory(RabbitMQVHosts.AUDIT_HOST);
    }
    @Bean(name = "auditRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory auditRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(auditCF());
    }
    @Bean
    public AmqpAdmin auditAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(auditCF());
        return amqpAdmin;
    }
    @Bean
    public AuditLogQueue AuditLogQueue() {
        AuditLogQueue queue =  new AuditLogQueue();
        bindQueue(auditAmqpAdmin(), queue);
        return queue;
    }
}
