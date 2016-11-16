package org.openiam.mq.constants.queue;

import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.RabbitMqExchange;
import org.springframework.amqp.core.ExchangeTypes;

/**
 * Created by alexander on 06/07/16.
 */
public class OpenIAMQueue  {
    public static final MqQueue MetadataQueue = new MqQueue("MetadataQueue");
    public static final MqQueue UserAttributeQueue = new MqQueue("UserAttributeQueue");
    public static final MqQueue RoleAttributeQueue = new MqQueue("RoleAttributeQueue");
    public static final MqQueue GroupAttributeQueue = new MqQueue("GroupAttributeQueue");
    public static final MqQueue ResourceAttributeQueue = new MqQueue("ResourceAttributeQueue");
    public static final MqQueue OrganizationAttributeQueue = new MqQueue("OrganizationAttributeQueue");
    public static final MqQueue MailQueue = new MqQueue("MailQueue");
    public static final MqQueue AuditLog = new MqQueue("AuditLog");
    public static final MqQueue ProvisionQueue = new MqQueue("ProvisionQueue");
    public static final MqQueue PolicyQueue = new MqQueue("PolicyQueue");
//    public static final MqQueue LanguageServiceQueue = new MqQueue("LanguageServiceQueue");
    public static final MqQueue UserQueue = new MqQueue("UserQueue");
    public static final MqQueue ActivitiQueue = new MqQueue("ActivitiQueue");
    public static final MqQueue ManagedSysQueue = new MqQueue("ManagedSysQueue");
    public static final MqQueue BatchTaskQueue = new MqQueue("BatchTaskQueue");



//
//    private String routingKey=this.name();
//    private String queueName=this.name();
//    private RabbitMqExchange exchange;
//
//    private OpenIAMQueue(RabbitMqExchange exchange) {
//        this.exchange = exchange;
//        if(ExchangeTypes.FANOUT==this.exchange.getType()){
//            // routingKey is not necessary if the exchange is FANOUT due to the message will be broadcasted
//            this.routingKey="";
//        }
//    }
//    private OpenIAMQueue(){
//        this(RabbitMqExchange.COMMON_EXCHANGE);
//    }
//
//    private OpenIAMQueue(RabbitMqExchange exchange, String routingKey){
//        this(exchange);
//        this.routingKey=routingKey;
//    }
//
//    public String getName(){
//        return this.queueName;
//    }
//    public void setName(String name){
//        this.queueName=name;
//    }
//    public RabbitMqExchange getExchange() {
//        return exchange;
//    }
//    public String getRoutingKey(){
//        return routingKey;
//    }
//    public String  getVHost(){
//        return RabbitMQVHosts.AM_HOST;
//    }
}
