package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.GetParentsRequest;
import org.openiam.base.response.RoleFindBeansResponse;
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
public class RoleGetParentsDispatcher extends AbstractRoleDispatcher<GetParentsRequest, RoleFindBeansResponse> {

    public RoleGetParentsDispatcher() {
        super(RoleFindBeansResponse.class);
    }

    @Override
    protected RoleFindBeansResponse processingApiRequest(RoleAPI openIAMAPI, GetParentsRequest requestBody) throws BasicDataServiceException {
        RoleFindBeansResponse response = new RoleFindBeansResponse();
        final List<Role> dtoList = roleDataService.getParentRolesDto(requestBody.getId(), requestBody.getRequesterId(), requestBody.getFrom(), requestBody.getSize());
        response.setRoles(dtoList);
        return response;
    }
}
