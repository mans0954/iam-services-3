package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.RoleRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleSaveRoleDispatcher extends AbstractRoleDispatcher<RoleRequest, Response> {
    public RoleSaveRoleDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(RoleAPI openIAMAPI, RoleRequest requestBody) throws BasicDataServiceException {
        Response response = new Response();
        roleDataService.saveRole(requestBody.getRole(), requestBody.getRequesterId());
        return response;
    }
}
