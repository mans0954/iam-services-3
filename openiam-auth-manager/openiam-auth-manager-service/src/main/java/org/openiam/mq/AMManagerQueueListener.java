package org.openiam.mq;

import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AMManagerQueue;
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
@RabbitListener(id="amManagerQueueListener",
        queues = "#{AMManagerQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AMManagerQueueListener extends AbstractListener<AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    @Autowired
    public AMManagerQueueListener(AMManagerQueue queue) {
        super(queue);
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMManagerAPI api, GetEntitlementRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMManagerAPI, GetEntitlementRequest>(){
            @Override
            public Response doProcess(AMManagerAPI api, GetEntitlementRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case IsMemberOfGroup:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfGroup(request.getUserId(),request.getTargetObjectId()));
                        break;
                    case IsMemberOfGroupWithRight:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfGroup(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                        break;
                    case IsMemberOfOrganization:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfOrganization(request.getUserId(),request.getTargetObjectId()));
                        break;
                    case IsMemberOfOrganizationWithRight:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfOrganization(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                        break;
                    case IsMemberOfRole:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfRole(request.getUserId(),request.getTargetObjectId()));
                        break;
                    case IsMemberOfRoleWithRight:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isMemberOfRole(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                        break;
                    case IsUserEntitledToResource:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isEntitled(request.getUserId(),request.getTargetObjectId()));
                        break;
                    case IsUserEntitledToResourceWithRight:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authManagerService.isEntitled(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                        break;
                    case GetGroupsForUser:
                        response = new GroupAuthorizationRightSetResponse();
                        ((GroupAuthorizationRightSetResponse)response).setGroupSet(authManagerService.getGroupsForUser(request.getUserId()));
                        break;
                    case GetRolesForUser:
                        response = new RoleAuthorizationRightSetResponse();
                        ((RoleAuthorizationRightSetResponse)response).setRoleSet(authManagerService.getRolesForUser(request.getUserId()));
                        break;
                    case GetResourcesForUser:
                        response = new ResourceAuthorizationRightSetResponse();
                        ((ResourceAuthorizationRightSetResponse)response).setResourceSet(authManagerService.getResourcesForUser(request.getUserId()));
                        break;
                    case GetOrganizationsForUser:
                        response = new OrganizationAuthorizationRightSetResponse();
                        ((OrganizationAuthorizationRightSetResponse)response).setOrganizationSet(authManagerService.getOrganizationsForUser(request.getUserId()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
