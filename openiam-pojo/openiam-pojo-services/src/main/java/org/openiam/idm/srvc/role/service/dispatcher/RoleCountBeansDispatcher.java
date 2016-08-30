package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.CountResponse;
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
public class RoleCountBeansDispatcher extends AbstractRoleDispatcher<BaseSearchServiceRequest<RoleSearchBean>, CountResponse> {

    public RoleCountBeansDispatcher() {
        super(CountResponse.class);
    }

    @Override
    protected CountResponse processingApiRequest(RoleAPI openIAMAPI, BaseSearchServiceRequest<RoleSearchBean> requestBody) throws BasicDataServiceException {
        CountResponse response = new CountResponse();
        int count = roleDataService.countBeans(requestBody.getSearchBean(), requestBody.getRequesterId());
        response.setRowCount(count);
        return response;
    }
}
