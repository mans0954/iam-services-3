package org.openiam.config;

import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.impl.RequestServiceGatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 27/07/16.
 */
@Configuration
@EnableRabbit
@Import(value={CommonVHostConfig.class,AuditVHostConfig.class,UserVHostConfig.class,AmVHostConfig.class,IdmVHostConfig.class,ActivitiVHostConfig.class,ConnectorVHostConfig.class})
public class RabbitMQConfig {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${org.openiam.mq.broker.reply.timeout}")
    private Long replyTimeout;

    @Autowired
    private ConnectionFactory amCF;
    @Autowired
    private ConnectionFactory idmCF;
    @Autowired
    private ConnectionFactory auditCF;
    @Autowired
    private ConnectionFactory commonCF;
    @Autowired
    private ConnectionFactory connectorCF;
    @Autowired
    private ConnectionFactory activitiCF;
    @Autowired
    private ConnectionFactory userCF;

    //TODO: need to get some statistic of working in RabbitMQ cluster. If we experience with performance issue we need to
    //TODO: implement LocalizedQueueConnectionFactory. See details in http://docs.spring.io/spring-amqp/reference/htmlsingle/#queue-affinity
    //TODO: for now it is ok to manage connection with CachingConnectionFactory

    @Bean
    public ConnectionFactory connectionFactory() {
        // this bean is only for send messages to the proper virtual host
        SimpleRoutingConnectionFactory routingConnectionFactory  = new SimpleRoutingConnectionFactory();
        Map<Object, ConnectionFactory> targetConnectionFactories = new HashMap<>();

        targetConnectionFactories.put(amCF.getVirtualHost(), amCF);
        targetConnectionFactories.put(idmCF.getVirtualHost(), idmCF);
        targetConnectionFactories.put(auditCF.getVirtualHost(), auditCF);
        targetConnectionFactories.put(commonCF.getVirtualHost(), commonCF);
        targetConnectionFactories.put(connectorCF.getVirtualHost(), connectorCF);
        targetConnectionFactories.put(activitiCF.getVirtualHost(), activitiCF);
        targetConnectionFactories.put(userCF.getVirtualHost(), userCF);
        routingConnectionFactory.setTargetConnectionFactories(targetConnectionFactories);
        return routingConnectionFactory;
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

        return template;
    }

    @Bean(name = "rabbitRequestServiceGateway")
    public RequestServiceGateway requestServiceGateway() {
        RequestServiceGatewayImpl gateway = new RequestServiceGatewayImpl();
        gateway.setConnectionFactory(connectionFactory());
        gateway.setRabbitOperations(rabbitTemplate());
        return gateway;
    }
}
