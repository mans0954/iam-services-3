package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.GroupAuthorizationRightSetResponse;
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
public class GetRolesForUserDispatcher extends AbstractAPIDispatcher<GetEntitlementRequest, RoleAuthorizationRightSetResponse, AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    public GetRolesForUserDispatcher() {
        super(RoleAuthorizationRightSetResponse.class);
    }

    @Override
    protected RoleAuthorizationRightSetResponse processingApiRequest(AMManagerAPI openIAMAPI, GetEntitlementRequest request) throws BasicDataServiceException {
        RoleAuthorizationRightSetResponse response = new RoleAuthorizationRightSetResponse();
        response.setRoleSet(authManagerService.getRolesForUser(request.getUserId()));
        return response;
    }
}
