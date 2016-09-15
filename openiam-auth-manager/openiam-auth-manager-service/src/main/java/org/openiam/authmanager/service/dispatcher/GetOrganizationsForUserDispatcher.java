package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.OrganizationAuthorizationRightSetResponse;
import org.openiam.base.response.ResourceAuthorizationRightSetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 15/09/16.
 */
@Component
public class GetOrganizationsForUserDispatcher extends AbstractAPIDispatcher<GetEntitlementRequest, OrganizationAuthorizationRightSetResponse, AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    public GetOrganizationsForUserDispatcher() {
        super(OrganizationAuthorizationRightSetResponse.class);
    }

    @Override
    protected OrganizationAuthorizationRightSetResponse processingApiRequest(AMManagerAPI openIAMAPI, GetEntitlementRequest request) throws BasicDataServiceException {
        OrganizationAuthorizationRightSetResponse response = new OrganizationAuthorizationRightSetResponse();
        response.setOrganizationSet(authManagerService.getOrganizationsForUser(request.getUserId()));
        return response;
    }
}
