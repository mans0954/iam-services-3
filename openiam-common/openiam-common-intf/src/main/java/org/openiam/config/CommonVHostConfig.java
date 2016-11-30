package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.common.*;
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
public class CommonVHostConfig extends BaseVHostConfig{
    @Bean
    public ConnectionFactory commonCF() {
        return createConnectionFactory(RabbitMQVHosts.COMMON_HOST);
    }
    @Bean(name = "commonRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory commonRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(commonCF());
    }
    @Bean
    public AmqpAdmin commonAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(commonCF());
        return amqpAdmin;
    }


    @Bean
    public LanguageServiceQueue LanguageServiceQueue() {
        LanguageServiceQueue queue =  new LanguageServiceQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public BatchTaskQueue BatchTaskQueue() {
        BatchTaskQueue queue =  new BatchTaskQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public MailQueue MailQueue() {
        MailQueue queue =  new MailQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public PolicyQueue PolicyQueue() {
        PolicyQueue queue =  new PolicyQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }

    @Bean
    public EsReindexQueue EsReindexQueue() {
        EsReindexQueue queue =  new EsReindexQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
}
