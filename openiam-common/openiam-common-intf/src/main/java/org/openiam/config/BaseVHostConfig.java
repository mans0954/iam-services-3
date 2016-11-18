package org.openiam.config;

import com.rabbitmq.client.Channel;
import org.openiam.mq.constants.queue.MqQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by alexander on 18/11/16.
 */
public class BaseVHostConfig {
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

    protected ConnectionFactory createConnectionFactory(final String vhost){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        logger.trace("RabbitMQ user: {}", userName);
        logger.trace("RabbitMQ pass: {}", password);
        logger.trace("RabbitMQ vhost: {}", vhost);
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

    protected SimpleRabbitListenerContainerFactory createRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
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


    protected void bindQueue(AmqpAdmin amqpAdmin, MqQueue queue) {
        Queue rabbitQueue;
        AbstractExchange exchange;


        if (queue.getExchange().getType().equals(ExchangeTypes.FANOUT)) {
            rabbitQueue = amqpAdmin.declareQueue();
            queue.setName(rabbitQueue.getName());
        } else {
            rabbitQueue = new Queue(queue.getName(), false, false, false, null);
        }

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
