package org.openiam.srvc;

import org.openiam.base.KeyDTO;
import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

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
    protected <API extends OpenIAMAPI> void publish(API apiName, BaseServiceRequest apiRequest){
        rabbitMQSender.publish(rabbitMqQueue, apiName, apiRequest);
    }
    protected <API extends OpenIAMAPI> void publish(OpenIAMQueue queue, API apiName, BaseServiceRequest apiRequest){
        rabbitMQSender.publish(queue, apiName, apiRequest);
    }

    protected <API extends OpenIAMAPI> boolean getBooleanValue(API apiName, BaseServiceRequest request){
        Boolean val = this.getValue(apiName, request, BooleanResponse.class);
        return (val==null)?false:val.booleanValue();
    }
    protected <API extends OpenIAMAPI> int getIntValue(API apiName, BaseServiceRequest request){
        Integer val = this.getValue(apiName, request, IntResponse.class);
        return (val==null)?0:val.intValue();
    }

    protected <ApiResponse extends Response, API extends OpenIAMAPI> ApiResponse getResponse(API apiName, BaseServiceRequest request, Class<ApiResponse> clazz){
        return this.manageApiRequest(apiName, request, clazz);
    }

    protected <V, ApiResponse extends BaseDataResponse<V>, API extends OpenIAMAPI> V getValue(API apiName, BaseServiceRequest request, Class<ApiResponse> clazz){
        ApiResponse response = getResponse(apiName, request, clazz);
//                ApiResponse response = this.manageApiRequest(apiName, request, clazz);
        if(response.isFailure()){
            return null;
        }
        return response.getValue();
    }
    protected <V, ApiResponse extends BaseListResponse<V>, API extends OpenIAMAPI> List<V> getValueList(API apiName, BaseServiceRequest request, Class<ApiResponse> clazz){
        ApiResponse response = getResponse(apiName, request, clazz);
// ApiResponse response = this.manageApiRequest(apiName, request, clazz);
        if(response.isFailure()){
            return null;
        }
        return response.getList();
    }

    protected <V extends KeyDTO, API extends OpenIAMAPI, ApiResponse extends BaseDataResponse<V>> Response manageGrudApiRequest(API apiName, V data, Class<ApiResponse> clazz){
        BaseCrudServiceRequest<V> request = new BaseCrudServiceRequest<>(data);
        ApiResponse response = getResponse(apiName, request, clazz);
        return response.convertToBase();
    }
    protected <V extends KeyDTO, API extends OpenIAMAPI> Response manageGrudApiRequest(API apiName, V data){
        BaseCrudServiceRequest<V> request = new BaseCrudServiceRequest<>(data);
        StringResponse response = getResponse(apiName, request, StringResponse.class);
        return response.convertToBase();
    }
    protected <API extends OpenIAMAPI> Response manageGrudApiRequest(API apiName, String id){
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getResponse(apiName, request, Response.class);
    }


}
