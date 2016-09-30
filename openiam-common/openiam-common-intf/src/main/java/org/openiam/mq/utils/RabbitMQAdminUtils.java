package org.openiam.mq.utils;

import org.openiam.mq.constants.MqQueue;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by alexander on 27/07/16.
 */
public class RabbitMQAdminUtils {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private AmqpAdmin amqpAdmin;
    private Integer concurrentConsumer;
    protected String encoding;

    public AmqpAdmin getAmqpAdmin() {
        return amqpAdmin;
    }

    public void setAmqpAdmin(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
    public String getEncoding(){
        return this.encoding;
    }
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getConcurrentConsumer() {
        return concurrentConsumer;
    }

    public void setConcurrentConsumer(Integer concurrentConsumer) {
        this.concurrentConsumer = concurrentConsumer;
    }

    public void bindQueues(MqQueue... queues) {
        Queue rabbitQueue;
        AbstractExchange exchange;

        for (MqQueue queue : queues) {
            if (queue.getTempQueue()) {
                rabbitQueue = amqpAdmin.declareQueue();
                queue.setName(rabbitQueue.getName());
            } else {
                rabbitQueue = new Queue(queue.getName(), false, false, false, null);
            }

            rabbitQueue = new Queue(queue.getName(), false, false, false, null);
            amqpAdmin.declareQueue(rabbitQueue);
            amqpAdmin.purgeQueue(queue.getName(), false);
            switch (queue.getExchange().getType()) {
                case DIRECT:

                    exchange = new DirectExchange(queue.getExchange().name());
                    amqpAdmin.declareExchange(exchange);
                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
                            .to((DirectExchange) exchange).with(queue.getRoutingKey()));
                    break;
                case FANOUT:
                    exchange = new FanoutExchange(queue.getExchange().name());
                    amqpAdmin.declareExchange(exchange);
                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue).to(
                            (FanoutExchange) exchange));
                    break;
                case HEADERS:
                case TOPIC:
                    exchange = new TopicExchange(queue.getExchange().name());
                    amqpAdmin.declareExchange(exchange);
                    amqpAdmin.declareBinding(BindingBuilder.bind(rabbitQueue)
                            .to((TopicExchange) exchange).with(queue.getRoutingKey()));
                    break;
            }
        }
    }
    public String getReplyQuequeName(String baseQueue){
        String queueName = baseQueue+".callback." + UUID.randomUUID().toString();
        amqpAdmin.declareQueue(new Queue(queueName, false, true, true, null));
        log.info("Callback queue " + queueName + " is created for base queue " + baseQueue);
        return queueName;
    }
    @SuppressWarnings("rawtypes")
    public <Listener extends AbstractRabbitMQListener> SimpleMessageListenerContainer createMessageListenerContainer(
            final String beanName, MqQueue rabbitMqQueue, Listener listener, ConnectionFactory connectionFactory) {
        bindQueues(rabbitMqQueue);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitMqQueue.getName());
        container.setMessageListener(new MessageListenerAdapter(listener));
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setTaskExecutor(new SimpleAsyncTaskExecutor(String.format("AMQP-%s-", rabbitMqQueue.getName())));
        container.setConcurrentConsumers(concurrentConsumer);
        container.setPrefetchCount(1);
        container.setErrorHandler(new ErrorHandler() {
            protected Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void handleError(Throwable t) {
                log.error("Problem in " + beanName + ": ", t);
            }
        });
        return container;
    }

    public byte[] generateCorrelationId() throws UnsupportedEncodingException {
        return UUID.randomUUID().toString().getBytes(encoding);
    }


}
