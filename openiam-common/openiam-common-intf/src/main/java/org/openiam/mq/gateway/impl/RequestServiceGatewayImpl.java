package org.openiam.mq.gateway.impl;

import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.template.CustomRabbitTemplate;
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
public class RequestServiceGatewayImpl extends RabbitGatewaySupport implements RequestServiceGateway {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private Long replyTimeout;

    private RabbitMQAdminUtils rabbitMQAdminUtils;

    public Long getReplyTimeout() {
        return replyTimeout;
    }

    public void setReplyTimeout(Long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    public RabbitMQAdminUtils getRabbitMQAdminUtils() {
        return rabbitMQAdminUtils;
    }

    public void setRabbitMQAdminUtils(RabbitMQAdminUtils rabbitMQAdminUtils) {
        this.rabbitMQAdminUtils = rabbitMQAdminUtils;
    }

    public void send(OpenIAMQueue queue, final MQRequest request){
        try {
            this.convertAndSend(queue,request);
        } catch (Exception e) {
            log.error(String.format("Cannot send a message {%s} to queue {%s}", request.toString(), queue.name()), e);
        }
    }
    public void send(String exchange, String routingKey, final MQRequest request) {
        try {
            this.convertAndSendWithName(exchange, request, routingKey);
        } catch (Exception e) {
            log.error(String.format("Cannot send a message {%s} to exchange {%s} with routingKey {%s}", request.toString(), exchange, routingKey), e);
        }
    }
    /**
     * @param queue
     * @param request
     * @return
     */
    public MQResponse sendAndReceive(OpenIAMQueue queue, final MQRequest request) {
        request.setReplyTo(rabbitMQAdminUtils.getReplyQuequeName(queue.name()));
        long startTime = System.currentTimeMillis();
        log.debug("Send to QUEUE : {}; Request: {};", queue.toString(), request.toString());
        Object response = ((CustomRabbitTemplate) getRabbitTemplate())
                .convertSendAndReceive(queue.getExchange().name(),
                        queue.getRoutingKey(), request, new MessagePostProcessor() {
                            @Override
                            public Message postProcessMessage(Message message)
                                    throws AmqpException {
                                message.getMessageProperties()
                                        .setReplyToAddress(
                                                new Address(request
                                                        .getReplyTo()));
                                try {
                                    message.getMessageProperties()
                                            .setCorrelationId( rabbitMQAdminUtils.generateCorrelationId());
                                    log.debug(
                                            "CorrelationID before SEND: {}",
                                            OpenIAMUtils
                                                    .byteArrayToString(
                                                            message.getMessageProperties()
                                                                    .getCorrelationId()));
                                } catch (UnsupportedEncodingException e) {
                                    throw new AmqpException(e);
                                }
                                return message;
                            }
                        }, request.getReplyTo());
        if (response != null) {
            ((MQResponse<String>) response).succeed();
            log.info("Received response from backend: " + response.toString());
        } else {

            log.warn("Response is not received from backend!");
            response = new MQResponse<String>();
            ((MQResponse<String>) response).fail();
            ((MQResponse<String>) response).setErrorCode(ResponseCode.INTERNAL_ERROR);
            ((MQResponse<String>) response).setErrorText("Response is not received from RabbitMQ during reply timeout");
            ((MQResponse<String>) response).setResponseBody("");
        }
        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("Received {} API response. Total time: {}", request.getRequestApi().name(), totalTime / 1000.0f);
        return (MQResponse) response;
    }

    private void convertAndSend(OpenIAMQueue queue, final MQRequest request) throws Exception {
        this.convertAndSendWithName(queue, request, queue.getRoutingKey());
    }

    public void convertAndSendToAll(OpenIAMQueue queue, final MQRequest request) throws Exception {
        this.convertAndSendWithName(queue, request, "");
    }

    private void convertAndSendWithName(OpenIAMQueue queue, final MQRequest request, String routingKey) throws Exception {
        log.debug("Send to QUEUE : QUEUE = " + queue.toString() + "; " + request.toString());
        convertAndSendWithName(queue.getExchange().name(), request, routingKey);
    }

    private void convertAndSendWithName(String exchange, final MQRequest request, String routingKey) throws Exception {
        log.debug("Send to exchange : EXCHANGE = " + exchange + "; RoutingKey: " + routingKey + ";" + request.toString());
        ((CustomRabbitTemplate) getRabbitTemplate()).convertAndSend(exchange, routingKey, request,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message)
                            throws AmqpException {
                        message.getMessageProperties().setReplyToAddress(
                                new Address(request.getReplyTo()));
                        try {
                            message.getMessageProperties().setCorrelationId(
                                    rabbitMQAdminUtils.generateCorrelationId());
                            log.debug("CorrelationID before SEND: {}",
                                    OpenIAMUtils.byteArrayToString(message
                                            .getMessageProperties()
                                            .getCorrelationId()));
                        } catch (UnsupportedEncodingException e) {
                            throw new AmqpException(e);
                        }
                        return message;
                    }
                });
    }
}
