package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.GetEntitlementRequest;
import org.openiam.base.response.GroupAuthorizationRightSetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 15/09/16.
 */
@Component
public class GetGroupsForUserDispatcher extends AbstractAPIDispatcher<GetEntitlementRequest, GroupAuthorizationRightSetResponse, AMManagerAPI> {
    @Autowired
    private AuthorizationManagerService authManagerService;

    public GetGroupsForUserDispatcher() {
        super(GroupAuthorizationRightSetResponse.class);
    }

    @Override
    protected GroupAuthorizationRightSetResponse processingApiRequest(AMManagerAPI openIAMAPI, GetEntitlementRequest request) throws BasicDataServiceException {
        GroupAuthorizationRightSetResponse response = new GroupAuthorizationRightSetResponse();
        response.setGroupSet(authManagerService.getGroupsForUser(request.getUserId()));
        return response;
    }
}
