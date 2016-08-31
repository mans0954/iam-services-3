package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.RoleRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleRemoveRoleDispatcher extends AbstractRoleDispatcher<IdServiceRequest, Response> {
    public RoleRemoveRoleDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(RoleAPI openIAMAPI, IdServiceRequest requestBody) throws BasicDataServiceException {
        Response response = new Response();
        roleDataService.removeRole(requestBody.getId(), requestBody.getRequesterId());
        return response;
    }
}
