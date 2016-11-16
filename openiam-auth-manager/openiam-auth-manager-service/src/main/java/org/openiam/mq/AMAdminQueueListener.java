package org.openiam.mq;

import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.request.*;
import org.openiam.base.response.EntityOwnerResponse;
import org.openiam.base.response.UserEntitlementsMatrixResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMAdminAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AMAdminQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="amAdminQueueListener",
        queues = "#{AMAdminQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AMAdminQueueListener extends AbstractListener<AMAdminAPI> {

    @Autowired
    private AuthorizationManagerAdminService authManagerAdminService;

    @Autowired
    public AMAdminQueueListener(AMAdminQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMAdminAPI api, UserEntitlementsMatrixRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMAdminAPI, UserEntitlementsMatrixRequest>(){
            @Override
            public Response doProcess(AMAdminAPI api, UserEntitlementsMatrixRequest request) throws BasicDataServiceException {
                UserEntitlementsMatrixResponse response = new UserEntitlementsMatrixResponse();
                response.setMatrix(authManagerAdminService.getUserEntitlementsMatrix(request.getUserId(), request.getDate()));
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMAdminAPI api, EntityOwnerRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMAdminAPI, EntityOwnerRequest>(){
            @Override
            public Response doProcess(AMAdminAPI api, EntityOwnerRequest request) throws BasicDataServiceException {
                EntityOwnerResponse response = new EntityOwnerResponse();
                switch (api){
                    case OwnerIdsForResourceSet:
                        response.setOwnersMap(authManagerAdminService.getOwnerIdsForResourceSet(request.getEntityIdSet(), request.getDate()));
                        break;
                    case OwnerIdsForGroupSet:
                        response.setOwnersMap(authManagerAdminService.getOwnerIdsForGroupSet(request.getEntityIdSet(), request.getDate()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
