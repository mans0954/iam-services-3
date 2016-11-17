package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.AuthResourceAttributeAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AuthResourceAttributeQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
@RabbitListener(id="authResourceAttributeListener",
        queues = "#{AuthResourceAttributeQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AuthResourceAttributeListener  extends AbstractListener<AuthResourceAttributeAPI> {

    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    @Autowired
    public AuthResourceAttributeListener(AuthResourceAttributeQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthResourceAttributeAPI api, SSOAttributesRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthResourceAttributeAPI, SSOAttributesRequest>(){
            @Override
            public Response doProcess(AuthResourceAttributeAPI api, SSOAttributesRequest request) throws BasicDataServiceException {
                switch (api){
                    case GetSSOAttributes:
                        SSOAttributeListResponse response = new SSOAttributeListResponse();
                        response.setSsoAttributeList(authResourceAttributeService.getSSOAttributes(request.getProviderId(), request.getUserId()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        });
    }

    @Override
    protected RequestProcessor<AuthResourceAttributeAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<AuthResourceAttributeAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(AuthResourceAttributeAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAmAttributeList:
                        response = new AuthResourceAMAttributeListResponse();
                        ((AuthResourceAMAttributeListResponse)response).setAmAttributeList(authResourceAttributeService.getAmAttributeList());
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }
    @Override
    protected RequestProcessor<AuthResourceAttributeAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<AuthResourceAttributeAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(AuthResourceAttributeAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAttribute:
                        response = new AuthResourceAttributeMapResponse();
                        ((AuthResourceAttributeMapResponse)response).setAttributeMap(authResourceAttributeService.getAttribute(request.getId()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<AuthResourceAttributeAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<AuthResourceAttributeAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(AuthResourceAttributeAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case SaveAttributeMap:
                        StringResponse response = new StringResponse();
                        response.setValue(authResourceAttributeService.saveAttributeMap(((BaseCrudServiceRequest<AuthResourceAttributeMap>)request).getObject()));
                        return response;
                    case DeleteAttributeMap:
                        authResourceAttributeService.removeAttributeMap(request.getObject().getId());
                        return new Response();
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }
}
