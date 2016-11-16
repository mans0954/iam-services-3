package org.openiam.config;

import com.rabbitmq.client.Channel;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.RabbitMQVHosts;
import org.openiam.mq.constants.queue.am.*;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.LanguageServiceQueue;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.openiam.mq.gateway.impl.RequestServiceGatewayImpl;
import org.openiam.mq.gateway.impl.ResponseServiceGatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 27/07/16.
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${org.openiam.rabbitmq.hosts}")
    private String brokersAddress;

    @Value("${org.openiam.rabbitmq.Username}")
    private String userName;
    @Value("${org.openiam.rabbitmq.Password}")
    private String password;
    @Value("${org.openiam.rabbitmq.concurrent.consumers}")
    private Integer concurrentConsumers;
    @Value("${org.openiam.rabbitmq.max.concurrent.consumers}")
    private Integer maxConcurrentConsumers;
    @Value("${org.openiam.rabbitmq.prefetch.count}")
    private Integer prefetchCount;
    @Value("${org.openiam.rabbitmq.channelTransacted}")
    private Boolean channelTransacted;
    @Value("${org.openiam.rabbitmq.channelCacheSize}")
    private Integer channelCacheSize;
    @Value("${org.openiam.mq.broker.reply.timeout}")
    private Long replyTimeout;
    @Value("${org.openiam.mq.broker.encoding}")
    protected String encoding;


    //TODO: need to get some statistic of working in RabbitMQ cluster. If we experience with performance issue we need to
    //TODO: implement LocalizedQueueConnectionFactory. See details in http://docs.spring.io/spring-amqp/reference/htmlsingle/#queue-affinity
    //TODO: for now it is ok to manage connection with CachingConnectionFactory

    @Bean
    public ConnectionFactory connectionFactory() {
        // this bean is only for send messages to the proper virtual host
        SimpleRoutingConnectionFactory routingConnectionFactory  = new SimpleRoutingConnectionFactory();
        Map<Object, ConnectionFactory> targetConnectionFactories = new HashMap<>();

        targetConnectionFactories.put(amCF().getVirtualHost(), amCF());
        targetConnectionFactories.put(idmCF().getVirtualHost(), idmCF());
        targetConnectionFactories.put(auditCF().getVirtualHost(), auditCF());
        targetConnectionFactories.put(commonCF().getVirtualHost(), commonCF());
        targetConnectionFactories.put(connectorCF().getVirtualHost(), connectorCF());
        targetConnectionFactories.put(activitiCF().getVirtualHost(), activitiCF());
        routingConnectionFactory.setTargetConnectionFactories(targetConnectionFactories);
        return routingConnectionFactory;
    }
    @Bean
    public ConnectionFactory amCF() {
        return createConnectionFactory(RabbitMQVHosts.AM_HOST);
    }
    @Bean
    public ConnectionFactory idmCF() {
        return createConnectionFactory(RabbitMQVHosts.IDM_HOST);
    }
    @Bean
    public ConnectionFactory auditCF() {
        return createConnectionFactory(RabbitMQVHosts.AUDIT_HOST);
    }
    @Bean
    public ConnectionFactory commonCF() {
        return createConnectionFactory(RabbitMQVHosts.COMMON_HOST);
    }
    @Bean
    public ConnectionFactory connectorCF() {
        return createConnectionFactory(RabbitMQVHosts.CONNECTOR_HOST);
    }
    @Bean
    public ConnectionFactory activitiCF() {
        return createConnectionFactory(RabbitMQVHosts.ACTIVITI_HOST);
    }

    private ConnectionFactory createConnectionFactory(final String vhost){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        logger.info("RabbitMQ user: {}", userName);
        logger.info("RabbitMQ pass: {}", password);
        logger.info("RabbitMQ vhost: {}", vhost);
        connectionFactory.setAddresses(brokersAddress);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setChannelCacheSize(channelCacheSize);
        connectionFactory.addChannelListener(new ChannelListener() {
            protected Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onCreate(Channel channel, boolean transactional) {
                log.debug("New rabbitmq channel is created : {}, transactional: {}", channel.toString(), transactional);
                channel.addShutdownListener(cause -> log.debug("Rabbitmq channel is closed. Cause: {}", cause.getMessage()));
            }
        });
        connectionFactory.addConnectionListener(new ConnectionListener() {
            protected Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onCreate(Connection connection) {
                log.debug("New rabbitmq connection is created: {}", connection.toString());
            }

            @Override
            public void onClose(Connection connection) {
                log.debug("Rabbitmq connection is closed {}", connection.toString());

            }
        });
        return connectionFactory;
    }

    @Bean(name = "amRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory amRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(amCF());
    }
    @Bean(name = "commonRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory commonRabbitListenerContainerFactory() {
        return createRabbitListenerContainerFactory(commonCF());
    }

    private SimpleRabbitListenerContainerFactory createRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        factory.setPrefetchCount(prefetchCount);
        factory.setDefaultRequeueRejected(true);
        factory.setIdleEventInterval(60000L);
        factory.setAutoStartup(true);
        return factory;
    }


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("messageProperties.headers['"+ MQConstant.VIRTUAL_HOST+"']");
        template.setSendConnectionFactorySelectorExpression(exp);

        //http://docs.spring.io/spring-amqp/reference/htmlsingle/#template-retry
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);

        template.setReplyTimeout(replyTimeout);

//        RabbitTemplate template = new CustomRabbitTemplate(connectionFactory());
//        template.setReplyTimeout(replyTimeout);
//        template.setMessagePropertiesConverter(messagePropertiesConverter());
        // template.setChannelTransacted(erpProperties.getChannelTransacted());
        return template;
    }
//    @Bean
//    public MessagePropertiesConverter messagePropertiesConverter() {
//        return new DefaultMessagePropertiesConverter();
//    }

    @Bean
    public AmqpAdmin amAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(amCF());
        return amqpAdmin;
    }
    @Bean
    public AmqpAdmin commonAmqpAdmin() {
        RabbitAdmin amqpAdmin = new RabbitAdmin(commonCF());
        return amqpAdmin;
    }

//    public void bindQueues(RabbitAdmin amqpAdmin, MqQueue... queues) {
//        Queue rabbitQueue;
//        AbstractExchange exchange;
//
//        for (MqQueue queue : queues) {
//
//            SimpleResourceHolder.bind(amqpAdmin.getRabbitTemplate().getConnectionFactory(), queue.getVHost());
//
//            if (queue.getExchange().getType().equals(ExchangeTypes.FANOUT)) {
//                rabbitQueue = amqpAdmin.declareQueue();
//                queue.setName(rabbitQueue.getName());
//            } else {
//                rabbitQueue = new Queue(queue.getName(), false, false, false, null);
//            }
//
////            rabbitQueue = new Queue(queue.getName(), false, false, false, null);
//            amqpAdmin.declareQueue(rabbitQueue);
//            amqpAdmin.purgeQueue(queue.getName(), false);
//            switch (queue.getExchange().getType()) {
//                case ExchangeTypes.DIRECT:
//
//                    exchange = new DirectExchange(queue.getExchange().name());
//                    amqpAdmin.declareExchange(exchange);
//                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
//                            .to((DirectExchange) exchange).with(queue.getRoutingKey()));
//                    break;
//                case ExchangeTypes.FANOUT:
//                    exchange = new FanoutExchange(queue.getExchange().name());
//                    amqpAdmin.declareExchange(exchange);
//                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue).to(
//                            (FanoutExchange) exchange));
//                    break;
//                case ExchangeTypes.HEADERS:
//                case ExchangeTypes.TOPIC:
//                    exchange = new TopicExchange(queue.getExchange().name());
//                    amqpAdmin.declareExchange(exchange);
//                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
//                            .to((TopicExchange) exchange).with(queue.getRoutingKey()));
//                    break;
//            }
//
//            SimpleResourceHolder.unbind(amqpAdmin.getRabbitTemplate().getConnectionFactory());
//        }
//    }

//    @Bean
//    public RabbitMQAdminUtils rabbitMQAdminUtils() throws IllegalAccessException {
//        RabbitMQAdminUtils adminUtils = new RabbitMQAdminUtils();
//        adminUtils.setConcurrentConsumer(concurrentConsumers);
//        adminUtils.setAmqpAdmin(amqpAdmin());
//        adminUtils.setEncoding(encoding);
//        return adminUtils;
//    }

    @Bean(name = "rabbitRequestServiceGateway")
    public RequestServiceGateway requestServiceGateway() {
        RequestServiceGatewayImpl gateway = new RequestServiceGatewayImpl();
        gateway.setConnectionFactory(connectionFactory());
        gateway.setRabbitOperations(rabbitTemplate());
//        gateway.setRabbitMQAdminUtils(rabbitMQAdminUtils());
//        gateway.setReplyTimeout(replyTimeout);
        return gateway;
    }


    @Bean(name = "rabbitResponseServiceGateway")
    public ResponseServiceGateway responseServiceGateway() {
        ResponseServiceGatewayImpl responseServiceGateway = new ResponseServiceGatewayImpl();
        responseServiceGateway.setRabbitTemplate(rabbitTemplate());
//        responseServiceGateway.setRabbitMQAdminUtils(rabbitMQAdminUtils());
        return responseServiceGateway;
    }
    // ************** AM QUEUEs
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

   // ************** COMMON QUEUEs
    @Bean
    public LanguageServiceQueue LanguageServiceQueue() {
        LanguageServiceQueue queue =  new LanguageServiceQueue();
        bindQueue(commonAmqpAdmin(), queue);
        return queue;
    }


    public void bindQueue(AmqpAdmin amqpAdmin, MqQueue  queue) {
        Queue rabbitQueue;
        AbstractExchange exchange;


        if (queue.getExchange().getType().equals(ExchangeTypes.FANOUT)) {
            rabbitQueue = amqpAdmin.declareQueue();
            queue.setName(rabbitQueue.getName());
        } else {
            rabbitQueue = new Queue(queue.getName(), false, false, false, null);
        }

//            rabbitQueue = new Queue(queue.getName(), false, false, false, null);
        amqpAdmin.declareQueue(rabbitQueue);
        amqpAdmin.purgeQueue(queue.getName(), false);
        switch (queue.getExchange().getType()) {
            case ExchangeTypes.DIRECT:

                exchange = new DirectExchange(queue.getExchange().name());
                amqpAdmin.declareExchange(exchange);
                amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
                        .to((DirectExchange) exchange).with(queue.getRoutingKey()));
                break;
            case ExchangeTypes.FANOUT:
                exchange = new FanoutExchange(queue.getExchange().name());
                amqpAdmin.declareExchange(exchange);
                amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue).to(
                        (FanoutExchange) exchange));
                break;
            case ExchangeTypes.HEADERS:
            case ExchangeTypes.TOPIC:
                exchange = new TopicExchange(queue.getExchange().name());
                amqpAdmin.declareExchange(exchange);
                amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
                        .to((TopicExchange) exchange).with(queue.getRoutingKey()));
                break;
        }
    }
}
