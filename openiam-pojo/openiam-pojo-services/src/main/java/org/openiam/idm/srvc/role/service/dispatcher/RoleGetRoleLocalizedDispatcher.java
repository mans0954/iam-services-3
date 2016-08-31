package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.RoleFindBeansResponse;
import org.openiam.base.response.RoleGetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class RoleGetRoleLocalizedDispatcher extends AbstractRoleDispatcher<IdServiceRequest, RoleGetResponse> {

    public RoleGetRoleLocalizedDispatcher() {
        super(RoleGetResponse.class);
    }

    @Override
    protected RoleGetResponse processingApiRequest(RoleAPI openIAMAPI, IdServiceRequest requestBody) throws BasicDataServiceException {
        RoleGetResponse response = new RoleGetResponse();
        Role role = roleDataService.getRoleDtoLocalized(requestBody.getId(), requestBody.getRequesterId(), requestBody.getLanguage());
        response.setRole(role);
        return response;
    }
}
