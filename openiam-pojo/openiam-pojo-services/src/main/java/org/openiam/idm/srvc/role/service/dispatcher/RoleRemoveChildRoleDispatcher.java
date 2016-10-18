package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.MembershipRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleRemoveChildRoleDispatcher extends AbstractRoleDispatcher<MembershipRequest, BooleanResponse> {
    public RoleRemoveChildRoleDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(RoleAPI openIAMAPI, MembershipRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        roleDataService.removeChildRole(requestBody.getObjectId(), requestBody.getLinkedObjectId());
        response.setValue(Boolean.TRUE);
        return response;
    }
}
