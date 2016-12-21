package org.openiam.mq.gateway.impl;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.util.SpringSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitGatewaySupport;

/**
 * @author Alexander Dukkardt
 * 
 */
public class RequestServiceGatewayImpl extends RabbitGatewaySupport implements RequestServiceGateway {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public void send(MqQueue queue, final OpenIAMAPI api, final BaseServiceRequest request){
        try {
            this.convertAndSend(queue, api, request);
        } catch (Exception e) {
            log.error(String.format("Cannot send a message {%s} to queue {%s}", request.toString(), queue.getName()), e);
        }
    }
    public void schedule(MqQueue queue, OpenIAMAPI api, Long delayMillis, final BaseServiceRequest request){
        if(!queue.getExchange().isDelayed()){
            log.warn(String.format("Cannot schedule a message {%s} to queue {%s}. Exchange must be declared as x-delayed-type. The message will be sent immediately", request.toString(), queue.getName()));
            delayMillis=null;
        }
        try {
            this.convertAndSendWithName(queue, api, delayMillis, request, queue.getRoutingKey());
        } catch (Exception e) {
            log.error(String.format("Cannot publish a message {%s} to queue {%s}", request.toString(), queue.getName()), e);
        }

    }
    public void publish(MqQueue queue, final OpenIAMAPI api, final BaseServiceRequest request) {
        try {
            this.convertAndSendWithName(queue, api, null, request, "");
        } catch (Exception e) {
            log.error(String.format("Cannot publish a message {%s} to queue {%s}", request.toString(), queue.getName()), e);
        }
    }

    /**
     * @param queue
     * @param request
     * @return
     */
    public Response sendAndReceive(MqQueue queue, final OpenIAMAPI api, final BaseServiceRequest request) {
        long startTime = System.currentTimeMillis();
        log.debug("Send to QUEUE : {}; Request: {};", queue.toString(), request.toString());

        request.setRequesterId(SpringSecurityHelper.getRequestorUserId());
        Object response = getRabbitOperations().convertSendAndReceive(queue.getExchange().name(), queue.getRoutingKey(),
                            request, message -> {
                            message.getMessageProperties().setHeader(MQConstant.VIRTUAL_HOST, queue.getVHost());
                            message.getMessageProperties().setHeader(MQConstant.API_NAME, api);
                            return message;
                        });
        if (response != null) {
            //((Response) response).succeed();
            log.info("Received response from backend: " + response.toString());
        } else {

            log.warn("Response is not received from backend!");
            response = new Response();
            ((Response) response).fail();
            ((Response) response).setErrorCode(ResponseCode.INTERNAL_ERROR);
            ((Response) response).setErrorText("Response is not received from RabbitMQ during reply timeout");
        }
        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("sendAndReceive {} API response ends. Total time: {}", api.name(), totalTime / 1000.0f);
        return (Response) response;
    }

    private void convertAndSend(MqQueue queue, final OpenIAMAPI api, final BaseServiceRequest request) throws Exception {
        this.convertAndSendWithName(queue, api, null, request, queue.getRoutingKey());
    }

    private void convertAndSendWithName(MqQueue queue, final OpenIAMAPI api, final Long delayMillis, final BaseServiceRequest request, String routingKey) throws Exception {
        log.debug("Send to QUEUE : QUEUE = " + queue.toString() + "; " + request.toString());
        convertAndSendWithName(queue.getVHost(), queue.getExchange().name(), api, delayMillis, request, routingKey);
    }

    private void convertAndSendWithName(final String vhost, final String exchange, final OpenIAMAPI api, final Long delayMillis, final BaseServiceRequest request, final String routingKey) throws Exception {
        log.debug("Send to exchange : EXCHANGE = " + exchange + "; RoutingKey: " + routingKey + ";" + request.toString());
        
        request.setRequesterId(SpringSecurityHelper.getRequestorUserId());
        getRabbitOperations().convertAndSend(exchange, routingKey, request,
                message -> {
                    message.getMessageProperties().setHeader(MQConstant.VIRTUAL_HOST, vhost);
                    message.getMessageProperties().setHeader(MQConstant.API_NAME, api);
                    if(delayMillis!=null && delayMillis>=0){
                        message.getMessageProperties().setHeader(MessageProperties.X_DELAY, delayMillis);
                    }
                    return message;
                });
    }
}
