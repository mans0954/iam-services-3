package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.common.*;
import org.openiam.mq.constants.queue.common.PolicyQueue;
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
    public MailServiceQueue MailServiceQueue() {
        MailServiceQueue queue =  new MailServiceQueue();
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
    @Bean
    public FileQueue FileQueue() {
        FileQueue queue =  new FileQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public MetadataElementTemplateQueue MetadataElementTemplateQueue() {
        MetadataElementTemplateQueue queue =  new MetadataElementTemplateQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public MetadataQueue MetadataQueue() {
        MetadataQueue queue =  new MetadataQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public PropertyValueQueue PropertyValueQueue() {
        PropertyValueQueue queue =  new PropertyValueQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public UIThemeQueue UIThemeQueue() {
        UIThemeQueue queue =  new UIThemeQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public EncryptionQueue EncryptionQueue() {
        EncryptionQueue queue =  new EncryptionQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public ReportQueue ReportQueue() {
        ReportQueue queue =  new ReportQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }

}
