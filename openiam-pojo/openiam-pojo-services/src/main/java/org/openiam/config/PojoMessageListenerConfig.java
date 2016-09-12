package org.openiam.config;

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
public class PojoMessageListenerConfig {

    @Autowired
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    @Bean
    @Autowired
    public SimpleMessageListenerContainer metaDataListenerContainer(MetaDataListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("metaDataListenerContainer",
                OpenIAMQueue.MetadataQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer userAttributeListenerContainer(UserAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("userAttributeListenerContainer",
                OpenIAMQueue.UserAttributeQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer roleAttributeListenerContainer(RoleAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("roleAttributeListenerContainer",
                OpenIAMQueue.RoleAttributeQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer groupAttributeListenerContainer(GroupAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("groupAttributeListenerContainer",
                OpenIAMQueue.GroupAttributeQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer resourceAttributeListenerContainer(ResourceAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("resourceAttributeListenerContainer",
                OpenIAMQueue.ResourceAttributeQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer organizationAttributeListenerContainer(OrganizationAttributeListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("organizationAttributeListenerContainer",
                OpenIAMQueue.OrganizationAttributeQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer emailSenderListenerContainer(EmailSenderListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("emailSenderListenerContainer",
                OpenIAMQueue.MailQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer auditLogListenerContainer(AuditLogListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("auditLogListenerContainer",
                OpenIAMQueue.AuditLog, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer languageListenerContainer(LanguageListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("languageListenerContainer",
                OpenIAMQueue.LanguageServiceQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer authenticationListenerContainer(AuthenticationListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("authenticationListenerContainer",
                OpenIAMQueue.AuthenticationQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer roleListenerContainer(RoleListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("roleListenerContainer",
                OpenIAMQueue.RoleQueue, listener, connectionFactory);
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer policyListenerContainer(PolicyListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("policyListenerContainer",
                OpenIAMQueue.PolicyQueue, listener, connectionFactory);
    }
    @Bean
    @Autowired
    public SimpleMessageListenerContainer accessRightListenerContainer(AccessRightMessageListener listener, ConnectionFactory connectionFactory) {
        return rabbitMQAdminUtils.createMessageListenerContainer("accessRightListenerContainer",
                OpenIAMQueue.AccessRightQueue, listener, connectionFactory);
    }


}
