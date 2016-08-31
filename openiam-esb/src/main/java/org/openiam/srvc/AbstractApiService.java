package org.openiam.srvc;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alexander on 08/08/16.
 */
public abstract class AbstractApiService {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private OpenIAMQueue rabbitMqQueue;
    @Autowired
    private RequestServiceGateway requestServiceGateway;

    public AbstractApiService(OpenIAMQueue rabbitMqQueue){
        this.rabbitMqQueue=rabbitMqQueue;
    }


    protected <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse manageApiRequest(API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass) {
        return manageApiRequest(rabbitMqQueue, apiName, apiRequest, apiResponseClass);
    }

    protected <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse manageApiRequest(OpenIAMQueue queue, API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass) {
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
