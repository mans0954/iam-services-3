package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.RoleAttributeGetResponse;
import org.openiam.base.response.RoleFindBeansResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class RoleGetAttributesDispatcher extends AbstractRoleDispatcher<IdServiceRequest, RoleAttributeGetResponse> {

    public RoleGetAttributesDispatcher() {
        super(RoleAttributeGetResponse.class);
    }

    @Override
    protected RoleAttributeGetResponse processingApiRequest(RoleAPI openIAMAPI, IdServiceRequest requestBody) throws BasicDataServiceException {
        RoleAttributeGetResponse response = new RoleAttributeGetResponse();
        final List<RoleAttribute> dtoList = roleDataService.getRoleAttributes(requestBody.getId());
        response.setRoleAttributes(dtoList);
        return response;
    }
}
