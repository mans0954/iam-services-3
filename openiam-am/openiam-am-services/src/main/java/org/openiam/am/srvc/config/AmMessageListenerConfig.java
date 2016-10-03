package org.openiam.am.srvc.config;

import org.openiam.am.srvc.mq.*;
import org.openiam.mq.*;
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
public class AmMessageListenerConfig {

    @Autowired
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    @Bean
    @Autowired
    public SimpleMessageListenerContainer uriFederationListenerContainer(URIFederationListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("uriFederationListenerContainer",
                OpenIAMQueue.URIFederationQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer uriFederationCacheListenerContainer(URIFederationCacheListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("uriFederationCacheListenerContainer",
                OpenIAMQueue.RefreshUriFederationCache,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer accessReviewListenerContainer(AccessReviewListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("accessReviewListenerContainer",
                OpenIAMQueue.AccessReviewQueue,  listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer authProviderListenerContainer(AuthProviderListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("authProviderListenerContainer",
                OpenIAMQueue.AuthProviderQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer authResourceAttributeListenerContainer(AuthResourceAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("authResourceAttributeListenerContainer",
                OpenIAMQueue.AuthResourceAttributeQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer contentProviderListenerContainer(ContentProviderListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("contentProviderListenerContainer",
                OpenIAMQueue.ContentProviderQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer oAuthListenerContainer(OAuthListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("oAuthListenerContainer",
                OpenIAMQueue.OAuthQueue,  listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer oAuthCacheListenerContainer(OAuthCacheListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("oAuthCacheListenerContainer",
                OpenIAMQueue.RefreshOAuthCache,  listener, connectionFactory);
    }
}
