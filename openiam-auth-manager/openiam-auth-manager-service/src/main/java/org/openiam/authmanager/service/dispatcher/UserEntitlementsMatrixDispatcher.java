package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.request.UserEntitlementsMatrixRequest;
import org.openiam.base.response.UserEntitlementsMatrixResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMAdminAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/09/16.
 */
@Component
public class UserEntitlementsMatrixDispatcher extends AbstractAPIDispatcher<UserEntitlementsMatrixRequest, UserEntitlementsMatrixResponse, AMAdminAPI> {

    @Autowired
    private AuthorizationManagerAdminService authManagerAdminService;

    public UserEntitlementsMatrixDispatcher() {
        super(UserEntitlementsMatrixResponse.class);
    }

    @Override
    protected UserEntitlementsMatrixResponse processingApiRequest(AMAdminAPI openIAMAPI, UserEntitlementsMatrixRequest request) throws BasicDataServiceException {
        UserEntitlementsMatrixResponse response = new UserEntitlementsMatrixResponse();
        response.setMatrix(authManagerAdminService.getUserEntitlementsMatrix(request.getUserId(), request.getDate()));
        return response;
    }
}
