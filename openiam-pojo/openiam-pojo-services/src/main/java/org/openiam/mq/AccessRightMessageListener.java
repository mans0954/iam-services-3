package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.AccessRightListResponse;
import org.openiam.base.response.AccessRightResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.mq.constants.api.AccessRightAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AccessRightQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 07/09/16.
 */
@Component
@RabbitListener(id="accessRightMessageListener",
        queues = "#{AccessRightQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AccessRightMessageListener extends AbstractListener<AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    public AccessRightMessageListener(AccessRightQueue queue) {
        super(queue);
    }


    protected RequestProcessor<AccessRightAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<AccessRightAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(AccessRightAPI accessRightAPI, IdServiceRequest request) throws BasicDataServiceException {
                AccessRightResponse response = new AccessRightResponse();
                response.setAccessRight(accessRightService.get(request.getId()));
                return response;
            }
        };
    }
    protected RequestProcessor<AccessRightAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<AccessRightAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(AccessRightAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindBeans:
                         response = new AccessRightListResponse();
                        ((AccessRightListResponse)response).setAccessRightList(accessRightService.findBeans(((BaseSearchServiceRequest<AccessRightSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize(), request.getLanguage()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(accessRightService.count(((BaseSearchServiceRequest<AccessRightSearchBean>)request).getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    protected RequestProcessor<AccessRightAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<AccessRightAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(AccessRightAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Save:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(accessRightService.save(((BaseCrudServiceRequest<AccessRight>)request).getObject()));
                        break;
                    case Delete:
                        response = new Response();
                        accessRightService.delete(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AccessRightAPI api, IdsServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new AbstractListener.RequestProcessor<AccessRightAPI, IdsServiceRequest>(){
            @Override
            public Response doProcess(AccessRightAPI api, IdsServiceRequest request) throws BasicDataServiceException {
                AccessRightListResponse response = new AccessRightListResponse();
                response.setAccessRightList(accessRightService.findByIds(request.getIds()));
                return response;
            }
        });
    }
}
