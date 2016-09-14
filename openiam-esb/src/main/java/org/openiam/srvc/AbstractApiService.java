package org.openiam.srvc;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.utils.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Created by alexander on 08/08/16.
 */
public abstract class AbstractApiService {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private OpenIAMQueue rabbitMqQueue;
    @Autowired
    protected RabbitMQSender rabbitMQSender;
    
    @Autowired
    protected ApplicationContext applicationContext;

    public AbstractApiService(OpenIAMQueue rabbitMqQueue){
        this.rabbitMqQueue=rabbitMqQueue;
    }


    protected <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse manageApiRequest(API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass) {
        return manageApiRequest(rabbitMqQueue, apiName, apiRequest, apiResponseClass);
    }

    protected <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse manageApiRequest(OpenIAMQueue queue, API apiName, BaseServiceRequest apiRequest, Class<ApiResponse> apiResponseClass) {
        return rabbitMQSender.sendAndReceive(queue, apiName, apiRequest, apiResponseClass);
    }

    protected <API extends OpenIAMAPI> void sendAsync(API apiName, BaseServiceRequest apiRequest){
        sendAsync(rabbitMqQueue, apiName, apiRequest);
    }
    protected <API extends OpenIAMAPI> void sendAsync(OpenIAMQueue queue, API apiName, BaseServiceRequest apiRequest){
        rabbitMQSender.send(queue, apiName, apiRequest);
    }
}
