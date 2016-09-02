package org.openiam.mq.constants;

import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 06/07/16.
 */
public enum OpenIAMQueue {
    MetadataQueue,
    UserAttributeQueue,
    RoleAttributeQueue,
    GroupAttributeQueue,
    ResourceAttributeQueue,
    OrganizationAttributeQueue,
    MailQueue,
    AuditLog,
    ProvisionQueue,
    RoleQueue,
    PolicyQueue,
    LanguageServiceQueue,
    URIFederationQueue,
    AuthenticationQueue,
    UserQueue,
    GroupQueue,
    ResourceQueue,
    ActivitiQueue;

    private String routingKey=this.name();
    private RabbitMqExchange exchange;
    private OpenIAMQueue(){
        this(RabbitMqExchange.COMMON_EXCHANGE);
    }
    private OpenIAMQueue(RabbitMqExchange exchange){
        this.exchange=exchange;
    }
    private OpenIAMQueue(RabbitMqExchange exchange, String routingKey){
        this.exchange=exchange;
        this.routingKey=routingKey;
    }

    public String getRoutingKey(){
        return this.routingKey;
    }
    public RabbitMqExchange getExchange() {
        return exchange;
    }

}
