package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.ResourceAuthorizationRightSetResponse;
import org.openiam.base.response.RoleAuthorizationRightSetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 15/09/16.
 */
@Component
public class GetResourcesForUserDispatcher extends AbstractAPIDispatcher<GetEntitlementRequest, ResourceAuthorizationRightSetResponse, AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    public GetResourcesForUserDispatcher() {
        super(ResourceAuthorizationRightSetResponse.class);
    }

    @Override
    protected ResourceAuthorizationRightSetResponse processingApiRequest(AMManagerAPI openIAMAPI, GetEntitlementRequest request) throws BasicDataServiceException {
        ResourceAuthorizationRightSetResponse response = new ResourceAuthorizationRightSetResponse();
        response.setResourceSet(authManagerService.getResourcesForUser(request.getUserId()));
        return response;
    }
}
