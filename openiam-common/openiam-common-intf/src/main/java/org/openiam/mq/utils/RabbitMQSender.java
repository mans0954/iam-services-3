package org.openiam.mq.utils;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.api.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.gateway.RequestServiceGateway;
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

    public <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse sendAndReceive(MqQueue queue, API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass){
        ApiResponse rabbitMqResponse =  (ApiResponse) requestServiceGateway.sendAndReceive(queue, apiName, apiRequest);

        if (rabbitMqResponse == null){
            return getFailedResponse(apiResponseClass);
        }

        if(rabbitMqResponse.isFailure()){
            // convert to proper ApiResponse class to avoid ClassCastException
            ApiResponse response = getFailedResponse(apiResponseClass);
            response.setErrorCode(rabbitMqResponse.getErrorCode());
            response.setErrorText(rabbitMqResponse.getErrorText());
            response.setErrorTokenList(rabbitMqResponse.getErrorTokenList());
            response.setFieldMappings(rabbitMqResponse.getFieldMappings());
            response.setStacktraceText(rabbitMqResponse.getStacktraceText());
            return response;
        }
        return rabbitMqResponse;
    }
    public <API extends OpenIAMAPI> void send(MqQueue queue, API apiName, final BaseServiceRequest apiRequest){
        apiRequest.setAsych(true);
        requestServiceGateway.send(queue, apiName, apiRequest);
    }
    public <API extends OpenIAMAPI>  void publish(MqQueue queue, API apiName, final BaseServiceRequest apiRequest){
        apiRequest.setAsych(true);
        requestServiceGateway.publish(queue, apiName, apiRequest);
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
