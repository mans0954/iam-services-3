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

    public void send(OpenIAMQueue queue, final MQRequest response) throws Exception{
        this.convertAndSend(queue,response);
    }
    /**
     * @param queue
     * @param request
     * @return
     */
    public MQResponse sendAndReceive(OpenIAMQueue queue, final MQRequest request) {
        request.setReplyTo(rabbitMQAdminUtils.getReplyQuequeName(queue.getName()));
        long startTime = System.currentTimeMillis();
        log.debug("Send to QUEUE : {}; Request: {};", queue.toString(), request.toString());
        Object response = ((CustomRabbitTemplate) getRabbitTemplate())
                .convertSendAndReceive(queue.getExchange().name(),
                        queue.getName(), request, new MessagePostProcessor() {
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
            log.info("Received response from backend: " + response.toString());
        } else {

            log.warn("Response is not received from backend!");
            response = new MQResponse<String>();
            ((MQResponse<String>) response)
                    .setErrorCode(ResponseCode.INTERNAL_ERROR);
            ((MQResponse<String>) response).setResponseBody("");
        }
        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("Received {} API response. Total time: {}", request.getRequestApi().name(), totalTime / 1000.0f);
        return (MQResponse) response;
    }

    private void convertAndSend(OpenIAMQueue queue, final MQRequest request) throws Exception {
        this.convertAndSendWithName(queue, request, queue.getName());
    }

    public void convertAndSendToAll(OpenIAMQueue queue, final MQRequest request) throws Exception {
        this.convertAndSendWithName(queue, request, "");
    }

    private void convertAndSendWithName(OpenIAMQueue queue, final MQRequest request, String queueName) throws Exception {
        log.debug("Send to QUEUE : QUEUE = " + queue.toString() + "; "
                + request.toString());
        ((CustomRabbitTemplate) getRabbitTemplate()).convertAndSend(queue
                .getExchange().name(), queueName, request,
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
