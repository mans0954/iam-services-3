package org.openiam.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.openiam.mq.gateway.impl.RequestServiceGatewayImpl;
import org.openiam.mq.gateway.impl.ResponseServiceGatewayImpl;
import org.openiam.mq.template.CustomRabbitTemplate;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alexander on 27/07/16.
 */
@Configuration
public class RabbitMQConfig {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${org.openiam.rabbitmq.hosts}")
    private String brokersAddress;
    @Value("${org.openiam.rabbitmq.VirtualHost}")
    private String virtualHost;
    @Value("${org.openiam.rabbitmq.Username}")
    private String userName;
    @Value("${org.openiam.rabbitmq.Password}")
    private String password;
    @Value("${org.openiam.rabbitmq.ConcurrentConsumers}")
    private Integer concurrentConsumers;
    @Value("${org.openiam.rabbitmq.channelTransacted}")
    private Boolean channelTransacted;
    @Value("${org.openiam.rabbitmq.channelCacheSize}")
    private Integer channelCacheSize;
    @Value("${org.openiam.mq.broker.reply.timeout}")
    private Long replyTimeout;
    @Value("${org.openiam.mq.broker.encoding}")
    protected String encoding;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        logger.info("RabbitMQ user: {}", userName);
        logger.info("RabbitMQ pass: {}", password);
        logger.info("RabbitMQ vhost: {}", virtualHost);
        connectionFactory.setAddresses(brokersAddress);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setChannelCacheSize(channelCacheSize);
        connectionFactory.addChannelListener(new ChannelListener() {
            protected Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onCreate(Channel channel, boolean transactional) {
                log.debug("New rabbitmq channel is created : {}, transactional: {}",
                        channel.toString(), transactional);
                channel.addShutdownListener(new ShutdownListener() {
                    @Override
                    public void shutdownCompleted(ShutdownSignalException cause) {
                        log.debug("Rabbitmq channel is closed. Cause: {}", cause.getMessage());
                    }
                });
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

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new CustomRabbitTemplate(connectionFactory());
        template.setReplyTimeout(replyTimeout);
        template.setMessagePropertiesConverter(messagePropertiesConverter());
        // template.setChannelTransacted(erpProperties.getChannelTransacted());
        return template;
    }
    @Bean
    public MessagePropertiesConverter messagePropertiesConverter() {
        return new DefaultMessagePropertiesConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
    @Bean
    public RabbitMQAdminUtils rabbitMQAdminUtils(){
        RabbitMQAdminUtils adminUtils = new RabbitMQAdminUtils();
        adminUtils.setConcurrentConsumer(concurrentConsumers);
        adminUtils.setAmqpAdmin(amqpAdmin());
        adminUtils.setEncoding(encoding);
        return adminUtils;
    }
    @Bean(name = "rabbitRequestServiceGateway")
    public RequestServiceGateway requestServiceGateway() {
        RequestServiceGatewayImpl gateway = new RequestServiceGatewayImpl();
        gateway.setConnectionFactory(connectionFactory());
        gateway.setRabbitTemplate(rabbitTemplate());
        gateway.setRabbitMQAdminUtils(rabbitMQAdminUtils());
        gateway.setReplyTimeout(replyTimeout);
        return gateway;
    }
    @Bean(name = "rabbitResponseServiceGateway")
    public ResponseServiceGateway responseServiceGateway() {
        ResponseServiceGatewayImpl responseServiceGateway = new ResponseServiceGatewayImpl();
        responseServiceGateway.setRabbitTemplate(rabbitTemplate());
        responseServiceGateway.setRabbitMQAdminUtils(rabbitMQAdminUtils());
        return responseServiceGateway;
    }
}
