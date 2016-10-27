package org.openiam.mq.constants;

/**
 * Created by alexander on 06/07/16.
 */
public enum OpenIAMQueue implements MqQueue {
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
    ActivitiQueue,
    AccessReviewQueue,
    ManagedSysQueue,
    AccessRightQueue,
    AMAdminQueue,
    AMMenuQueue,
    AMManagerQueue,
    AMCacheQueue(RabbitMqExchange.AM_CACHE_EXCHANGE, true),
    AuthProviderQueue,
    AuthProviderAPI,
    AuthResourceAttributeQueue,
    ContentProviderQueue,
    OAuthQueue,
    RefreshOAuthCache(RabbitMqExchange.REFRESH_OAUTH_CACHE_EXCHANGE, true),
    RefreshUriFederationCache(RabbitMqExchange.URI_FEDERATION_CACHE_EXCHANGE, true),
    OrganizationTypeQueue,
    BatchTaskQueue;

    private String routingKey=this.name();
    private String queueName=this.name();
    private RabbitMqExchange exchange;
    private boolean tempQueue = false;

    private OpenIAMQueue(boolean tempQueue) {
        this.tempQueue = tempQueue;
        if (tempQueue)
            this.queueName = null;
    }
    private OpenIAMQueue(RabbitMqExchange exchange){
        this(false);
        this.exchange=exchange;
    }
    private OpenIAMQueue(RabbitMqExchange exchange, boolean tempQueue) {
        this(tempQueue);
        this.exchange = exchange;
        if(RabbitMqExchangeType.FANOUT==this.exchange.getType()){
            // routingKey is not necessary if the exchange is FANOUT due to the message will be broadcasted
            this.routingKey="";
        }
    }
    private OpenIAMQueue(){
        this(RabbitMqExchange.COMMON_EXCHANGE, false);
    }

    private OpenIAMQueue(RabbitMqExchange exchange, String routingKey){
        this(exchange, false);
        this.routingKey=routingKey;
    }

    public String getName(){
        return this.queueName;
    }
    public void setName(String name){
        this.queueName=name;
    }
    public RabbitMqExchange getExchange() {
        return exchange;
    }
    public boolean getTempQueue(){
        return tempQueue;
    }
    public String getRoutingKey(){
        return routingKey;
    }

}
