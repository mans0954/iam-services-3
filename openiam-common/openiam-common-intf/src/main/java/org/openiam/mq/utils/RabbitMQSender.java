package org.openiam.mq.utils;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 05/09/16.
 */
@Component
public class RabbitMQSender {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequestServiceGateway requestServiceGateway;
    @Autowired
    private ResponseServiceGateway responseServiceGateway;

    public <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse sendAndReceive(OpenIAMQueue queue, API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass){
        MQResponse<ApiResponse> rabbitMqResponse =  (MQResponse<ApiResponse>) requestServiceGateway.sendAndReceive(queue, new MQRequest<BaseServiceRequest, API>(apiName, apiRequest));

        if (rabbitMqResponse == null){
            return getFailedResponse(apiResponseClass);
        }
        if(rabbitMqResponse.isFailure()){
            ApiResponse response = getFailedResponse(apiResponseClass);
            response.setErrorCode(rabbitMqResponse.getErrorCode());
            response.setErrorText(rabbitMqResponse.getErrorText());
            return response;
        }
        return rabbitMqResponse.getResponseBody();
    }
    public <API extends OpenIAMAPI> void send(OpenIAMQueue queue, API apiName, final BaseServiceRequest apiRequest){
        requestServiceGateway.send(queue, new MQRequest<BaseServiceRequest, API>(apiName, apiRequest));
    }
    public <API extends OpenIAMAPI>  void send(String exchange, String routingKey, API apiName, final BaseServiceRequest apiRequest){
        requestServiceGateway.send(exchange, routingKey, new MQRequest<BaseServiceRequest, API>(apiName, apiRequest));
    }
    public <API extends OpenIAMAPI>  void publish(OpenIAMQueue queue, API apiName, final BaseServiceRequest apiRequest){
        requestServiceGateway.publish(queue, new MQRequest<BaseServiceRequest, API>(apiName, apiRequest));
    }

    private <ApiResponse extends Response> ApiResponse getFailedResponse(Class<ApiResponse> apiResponseClass){
        ApiResponse response = null;
        try {
            response = apiResponseClass.newInstance();
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.fail();
        } catch (InstantiationException e) {
            log.error(e.getMessage(),e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(),e);
        }
        return response;
    }
}
