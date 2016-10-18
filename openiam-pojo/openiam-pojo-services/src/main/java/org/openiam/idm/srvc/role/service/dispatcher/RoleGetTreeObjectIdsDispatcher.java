package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.TreeObjectId;
import org.openiam.base.request.IdsServiceRequest;
import org.openiam.base.response.TreeObjectIdListServiceResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zaporozhec on 8/30/16.
 */
@Component
public class RoleGetTreeObjectIdsDispatcher extends AbstractRoleDispatcher<IdsServiceRequest, TreeObjectIdListServiceResponse> {
    public RoleGetTreeObjectIdsDispatcher() {
        super(TreeObjectIdListServiceResponse.class);
    }

    @Override
    protected TreeObjectIdListServiceResponse processingApiRequest(RoleAPI openIAMAPI, IdsServiceRequest requestBody) throws BasicDataServiceException {
        TreeObjectIdListServiceResponse response = new TreeObjectIdListServiceResponse();
        List<TreeObjectId> result = roleDataService.getRolesWithSubRolesIds(requestBody.getIds(), requestBody.getRequesterId());
        response.setTreeObjectIds(result);
        return response;
    }
}
