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

    protected AbstractApiService(OpenIAMQueue rabbitMqQueue){
        this.rabbitMqQueue=rabbitMqQueue;
    }


    protected <ApiResponse extends Response> ApiResponse manageApiRequest( OpenIAMAPI apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass) {
        MQResponse<ApiResponse> rabbitMqResponse =  (MQResponse<ApiResponse>) requestServiceGateway.sendAndReceive(rabbitMqQueue, new MQRequest(apiName, apiRequest));

        if (rabbitMqResponse == null){
            return getFailedResponse(apiResponseClass);
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
