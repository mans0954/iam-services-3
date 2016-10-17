package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleHasChildEntitiesDispatcher extends AbstractRoleDispatcher<IdServiceRequest, BooleanResponse> {
    public RoleHasChildEntitiesDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(RoleAPI openIAMAPI, IdServiceRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        boolean responseValue = roleDataService.hasChildEntities(requestBody.getId());
        response.setValue(responseValue);
        return response;
    }
}
