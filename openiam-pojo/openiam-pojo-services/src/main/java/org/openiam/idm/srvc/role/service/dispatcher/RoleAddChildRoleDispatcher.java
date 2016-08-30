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
public class RoleAddChildRoleDispatcher extends AbstractRoleDispatcher<EntitleToRoleRequest, BooleanResponse> {
    public RoleAddChildRoleDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(RoleAPI openIAMAPI, EntitleToRoleRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        roleDataService.addChildRole(requestBody.getRoleId(), requestBody.getLinkedObjectId(), requestBody.getRightIds(),
                requestBody.getStartDate(), requestBody.getEndDate());
        response.setValue(Boolean.TRUE);
        return response;
    }
}
