package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.RoleValidateEditRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class RoleValidateEditDispatcher extends AbstractRoleDispatcher<RoleValidateEditRequest, BooleanResponse> {

    public RoleValidateEditDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(RoleAPI openIAMAPI, RoleValidateEditRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        response.setValue(false);
        response.setValue(roleDataService.validateEdit(requestBody.getRole()));
        return response;
    }
}
