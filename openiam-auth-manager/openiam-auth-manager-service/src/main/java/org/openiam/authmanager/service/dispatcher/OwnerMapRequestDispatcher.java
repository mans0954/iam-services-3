package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.request.EntityOwnerRequest;
import org.openiam.base.request.UserEntitlementsMatrixRequest;
import org.openiam.base.response.EntityOwnerResponse;
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
public class OwnerMapRequestDispatcher extends AbstractAPIDispatcher<EntityOwnerRequest, EntityOwnerResponse, AMAdminAPI> {

    @Autowired
    private AuthorizationManagerAdminService authManagerAdminService;

    public OwnerMapRequestDispatcher() {
        super(EntityOwnerResponse.class);
    }

    @Override
    protected EntityOwnerResponse processingApiRequest(AMAdminAPI openIAMAPI, EntityOwnerRequest request) throws BasicDataServiceException {
        EntityOwnerResponse response = new EntityOwnerResponse();
        switch (openIAMAPI){
            case OwnerIdsForResourceSet:
                response.setOwnersMap(authManagerAdminService.getOwnerIdsForResourceSet(request.getEntityIdSet(), request.getDate()));
                break;
            case OwnerIdsForGroupSet:
                response.setOwnersMap(authManagerAdminService.getOwnerIdsForGroupSet(request.getEntityIdSet(), request.getDate()));
                break;
        }
        return response;
    }
}
