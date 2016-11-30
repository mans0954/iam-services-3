package org.openiam.config;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.am.*;
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
public class AmVHostConfig extends BaseVHostConfig {

    @Bean
    public ConnectionFactory amCF() {
        return createConnectionFactory(RabbitMQVHosts.AM_HOST);
    }
    @Bean(name = "amRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory amRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(amCF());
    }

    @Bean
    public AmqpAdmin amAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(amCF());
        return amqpAdmin;
    }


    @Bean
    public URIFederationQueue URIFederationQueue() {
        URIFederationQueue queue =  new URIFederationQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AuthProviderQueue AuthProviderQueue() {
        AuthProviderQueue queue =  new AuthProviderQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public ContentProviderQueue ContentProviderQueue() {
        ContentProviderQueue queue =  new ContentProviderQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AuthResourceAttributeQueue AuthResourceAttributeQueue() {
        AuthResourceAttributeQueue queue =  new AuthResourceAttributeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AccessReviewQueue AccessReviewQueue() {
        AccessReviewQueue queue =  new AccessReviewQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public RefreshOAuthCache RefreshOAuthCache() {
        RefreshOAuthCache queue =  new RefreshOAuthCache();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public OAuthQueue OAuthQueue() {
        OAuthQueue queue =  new OAuthQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public RefreshUriFederationCache RefreshUriFederationCache() {
        RefreshUriFederationCache queue =  new RefreshUriFederationCache();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AMAdminQueue AMAdminQueue() {
        AMAdminQueue queue =  new AMAdminQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AMCacheQueue AMCacheQueue() {
        AMCacheQueue queue =  new AMCacheQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AMManagerQueue AMManagerQueue() {
        AMManagerQueue queue =  new AMManagerQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AMMenuQueue AMMenuQueue() {
        AMMenuQueue queue =  new AMMenuQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AccessRightQueue AccessRightQueue() {
        AccessRightQueue queue =  new AccessRightQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public AuthenticationQueue AuthenticationQueue() {
        AuthenticationQueue queue =  new AuthenticationQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public GroupQueue GroupQueue() {
        GroupQueue queue =  new GroupQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public ResourceQueue ResourceQueue() {
        ResourceQueue queue =  new ResourceQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public OrganizationTypeQueue OrganizationTypeQueue(){
        OrganizationTypeQueue queue =  new OrganizationTypeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public RoleQueue RoleQueue(){
        RoleQueue queue =  new RoleQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public GroupAttributeQueue GroupAttributeQueue(){
        GroupAttributeQueue queue =  new GroupAttributeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public OrganizationAttributeQueue OrganizationAttributeQueue(){
        OrganizationAttributeQueue queue =  new OrganizationAttributeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public ResourceAttributeQueue ResourceAttributeQueue(){
        ResourceAttributeQueue queue =  new ResourceAttributeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
    @Bean
    public RoleAttributeQueue RoleAttributeQueue(){
        RoleAttributeQueue queue =  new RoleAttributeQueue();
        bindQueue(amAmqpAdmin(), queue);
        return queue;
    }
}
