package org.openiam.mq.template;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.openiam.util.OpenIAMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexander on 26/07/16.
 */
public class CustomRabbitTemplate extends RabbitTemplate {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${org.openiam.mq.broker.reply.timeout}")
    private Long replyTimeout;

    @Value("${org.openiam.mq.broker.encoding}")
    private String encoding;

    @Autowired
    private MessagePropertiesConverter messagePropertiesConverter;

    public CustomRabbitTemplate() {
        initDefaultStrategies();
    }

    /**
     * Create a rabbit template with default strategies and settings.
     *
     * @param connectionFactory the connection factory to use
     */
    public CustomRabbitTemplate(ConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    public Object convertSendAndReceive(String exchange, String routingKey,
                                           Object request, MessagePostProcessor messagePostProcessor,
                                           String callbackQueueName) throws AmqpException {
        MessageProperties messageProperties = new MessageProperties();
        MessageConverter messageConverter = getMessageConverter();
        if (messageConverter == null) {
            throw new AmqpIllegalStateException(
                    "No 'messageConverter' specified. Check configuration of RabbitTemplate.");
        }
        Message requestMessage = messageConverter.toMessage(request,
                messageProperties);
        if (messagePostProcessor != null) {
            requestMessage = messagePostProcessor
                    .postProcessMessage(requestMessage);
        }
        Message replyMessage = doSendAndReceive(exchange, routingKey,
                requestMessage, callbackQueueName);
        if (replyMessage == null) {
            return null;
        }
        return messageConverter.fromMessage(replyMessage);
    }

    /**
     * @param exchange
     * @param routingKey
     * @param requestMessage
     * @param callbackQueueName
     * @return
     */
    private Message doSendAndReceive(final String exchange,
                                     final String routingKey,
                                     final Message requestMessage,
                                     final String callbackQueueName) {
        Message replyMessage = this.execute(new ChannelCallback<Message>() {
            public Message doInRabbit(Channel channel) throws Exception {

                final SynchronousQueue<Message> replyHandoff = new SynchronousQueue<Message>();
                requestMessage.getMessageProperties().setReplyTo(callbackQueueName);
                byte[] expectedCorrelationId = requestMessage.getMessageProperties().getCorrelationId();
                boolean noAck = false;
                String consumerTag = UUID.randomUUID().toString();
                boolean noLocal = true;
                boolean exclusive = false;
                DefaultConsumer consumer = new DefaultConsumer(channel) {

                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope, AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        MessageProperties messageProperties = messagePropertiesConverter
                                .toMessageProperties(properties, envelope, encoding);
                        Message reply = new Message(body, messageProperties);
                        log.debug("GOT REPLY: {}", reply);
                        try {
                            replyHandoff.put(reply);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                };
                channel.basicConsume(callbackQueueName, noAck, consumerTag, noLocal, exclusive, null, consumer);
                doSend(channel, exchange, routingKey, requestMessage, true, new CorrelationData(new String(expectedCorrelationId)));
                Message reply = (replyTimeout < 0) ? replyHandoff
                        .take() : replyHandoff.poll(replyTimeout, TimeUnit.MILLISECONDS);
                if (reply == null) {
                    log.warn(
                            "No messages received during reply timeout. Callback queue: {} CANCEL CONSUMER",
                            callbackQueueName);
                } else {
                    if (Arrays.equals(reply.getMessageProperties()
                            .getCorrelationId(), expectedCorrelationId)) {
                        log.debug("Got reply from rabbitmq. Correlation Id matches, push mq back to waiting thread");
                        channel.basicAck(reply.getMessageProperties()
                                .getDeliveryTag(), false);
                        log.debug("Reply is caught. Message is acknowledged");
                    } else {
                        log.debug(
                                "Got reply from rabbitmq. However, correlation Id doesn't match, DO NOTHING. Message: {}; Expected: {}; Received: {}",
                                new Object[] {
                                        reply,
                                        OpenIAMUtils.byteArrayToString(expectedCorrelationId),
                                        OpenIAMUtils.byteArrayToString(reply.getMessageProperties().getCorrelationId()) });
                        log.debug("Rejecting mq andwipe it out from callback queue. NEXT ATTEMPT TO GET THE CORRECT MESSAGE SHOULD WORK!!!");
                        channel.basicNack(reply.getMessageProperties()
                                .getDeliveryTag(), false, false);
                        log.debug("Cleaning queue is finished");
                        reply = null;

                    }
                }
                channel.basicCancel(consumerTag);
                return reply;
            }
        });
        return replyMessage;
    }
}
