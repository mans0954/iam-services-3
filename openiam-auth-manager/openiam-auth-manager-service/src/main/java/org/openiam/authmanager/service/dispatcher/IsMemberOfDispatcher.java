package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/09/16.
 */
@Component
public class IsMemberOfDispatcher extends AbstractAPIDispatcher<GetEntitlementRequest, BooleanResponse, AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    public IsMemberOfDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(AMManagerAPI openIAMAPI, GetEntitlementRequest request) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        switch (openIAMAPI){
            case IsMemberOfGroup:
                response.setValue(authManagerService.isMemberOfGroup(request.getUserId(),request.getTargetObjectId()));
                break;
            case IsMemberOfGroupWithRight:
                response.setValue(authManagerService.isMemberOfGroup(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                break;
            case IsMemberOfOrganization:
                response.setValue(authManagerService.isMemberOfOrganization(request.getUserId(),request.getTargetObjectId()));
                break;
            case IsMemberOfOrganizationWithRight:
                response.setValue(authManagerService.isMemberOfOrganization(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                break;
            case IsMemberOfRole:
                response.setValue(authManagerService.isMemberOfRole(request.getUserId(),request.getTargetObjectId()));
                break;
            case IsMemberOfRoleWithRight:
                response.setValue(authManagerService.isMemberOfRole(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                break;
            case IsUserEntitledToResource:
                response.setValue(authManagerService.isEntitled(request.getUserId(),request.getTargetObjectId()));
                break;
            case IsUserEntitledToResourceWithRight:
                response.setValue(authManagerService.isEntitled(request.getUserId(),request.getTargetObjectId(), request.getRightId()));
                break;
        }
        return response;
    }
}
