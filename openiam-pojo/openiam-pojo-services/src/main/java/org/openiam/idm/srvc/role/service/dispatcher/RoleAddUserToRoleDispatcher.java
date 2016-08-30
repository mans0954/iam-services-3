package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.EntitleToRoleRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleAddUserToRoleDispatcher extends AbstractRoleDispatcher<EntitleToRoleRequest, BooleanResponse> {
    public RoleAddUserToRoleDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(RoleAPI openIAMAPI, EntitleToRoleRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        roleDataService.addUserToRole(requestBody.getRoleId(), requestBody.getLinkedObjectId(),
                requestBody.getRequesterId(),
                requestBody.getRightIds(),
                requestBody.getStartDate(),
                requestBody.getEndDate());
        response.setValue(Boolean.TRUE);
        return response;
    }
}
