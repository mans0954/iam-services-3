package org.openiam.mq.gateway.impl;

import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.openiam.util.OpenIAMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitGatewaySupport;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexander Dukkardt
 * 
 */
public class ResponseServiceGatewayImpl extends RabbitGatewaySupport implements ResponseServiceGateway {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private RabbitMQAdminUtils rabbitMQAdminUtils;

    public RabbitMQAdminUtils getRabbitMQAdminUtils() {
        return rabbitMQAdminUtils;
    }

    public void setRabbitMQAdminUtils(RabbitMQAdminUtils rabbitMQAdminUtils) {
        this.rabbitMQAdminUtils = rabbitMQAdminUtils;
    }

    public void send(String routingKey, final MQResponse<?> response, final byte[] correlationId) {
        log.debug(
                "Sending mq to {}: {} correlationId: {}",
                new Object[] { routingKey, response,
                        OpenIAMUtils.byteArrayToString(correlationId) });
        getRabbitTemplate().convertAndSend(routingKey, response,
                new MessagePostProcessor() {
                    public Message postProcessMessage(Message message)
                            throws AmqpException {
                        message.getMessageProperties().setReplyToAddress(
                                new Address(""));
                        try {
                            if (correlationId != null) {

                                message.getMessageProperties()
                                        .setCorrelationId(correlationId);
                                log.debug(
                                        "Send mq reply with correlationID: {}",
                                        OpenIAMUtils.byteArrayToString(message
                                                .getMessageProperties()
                                                .getCorrelationId()));
                            } else {
                                message.getMessageProperties()
                                        .setCorrelationId(rabbitMQAdminUtils.generateCorrelationId());
                                log.debug(
                                        "Message reply correlationID is overriden to: {}",
                                        OpenIAMUtils.byteArrayToString(message
                                                .getMessageProperties()
                                                .getCorrelationId()));
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new AmqpException(e);
                        }
                        return message;
                    }
                });
    }

    public Message receive(String routingKey) {
        return getRabbitTemplate().receive(routingKey);
    }
}
