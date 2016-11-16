package org.openiam.mq.constants.queue.am;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.openiam.mq.constants.RabbitMqExchangeType;
import org.openiam.mq.constants.queue.MqQueue;
import org.springframework.amqp.core.ExchangeTypes;


/**
 * Created by alexander on 09/11/16.
 */
public class AMQueue extends MqQueue {

    public AMQueue(String name) {
        super(RabbitMqExchange.AM_EXCHANGE, name, RabbitMQVHosts.AM_HOST);
    }

    public AMQueue(RabbitMqExchange exchange, String name) {
        super(exchange, name, RabbitMQVHosts.AM_HOST);
    }

//    public static final MqQueue AccessReviewQueue = new MqQueue("AccessReviewQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AccessRightQueue = new MqQueue("AccessRightQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AuthenticationQueue = new MqQueue("AuthenticationQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AMAdminQueue = new MqQueue("AMAdminQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AMMenuQueue = new MqQueue("AMMenuQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AMManagerQueue = new MqQueue("AMManagerQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AuthProviderQueue = new MqQueue("AuthProviderQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue AuthResourceAttributeQueue = new MqQueue("AuthResourceAttributeQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue ContentProviderQueue = new MqQueue("ContentProviderQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue GroupQueue = new MqQueue("GroupQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue ResourceQueue = new MqQueue("ResourceQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue OAuthQueue = new MqQueue("OAuthQueue", RabbitMQVHosts.AM_HOST);

    public static final MqQueue OrganizationTypeQueue = new MqQueue("OrganizationTypeQueue", RabbitMQVHosts.AM_HOST);
    public static final MqQueue RoleQueue = new MqQueue("RoleQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue URIFederationQueue = new MqQueue("URIFederationQueue", RabbitMQVHosts.AM_HOST);

//    public static final MqQueue AMCacheQueue = new MqQueue(RabbitMqExchange.AM_CACHE_EXCHANGE, "AMCacheQueue", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue RefreshOAuthCache = new MqQueue(RabbitMqExchange.REFRESH_OAUTH_CACHE_EXCHANGE, "RefreshOAuthCache", RabbitMQVHosts.AM_HOST);
//    public static final MqQueue RefreshUriFederationCache = new MqQueue(RabbitMqExchange.URI_FEDERATION_CACHE_EXCHANGE, "RefreshUriFederationCache", RabbitMQVHosts.AM_HOST);
}
