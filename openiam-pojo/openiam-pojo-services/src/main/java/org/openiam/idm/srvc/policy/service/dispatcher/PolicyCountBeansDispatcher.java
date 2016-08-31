package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.CountResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.service.dispatcher.AbstractRoleDispatcher;
import org.openiam.mq.constants.PolicyAPI;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyCountBeansDispatcher extends AbstractPolicyDispatcher<BaseSearchServiceRequest<PolicySearchBean>, CountResponse> {

    public PolicyCountBeansDispatcher() {
        super(CountResponse.class);
    }

    @Override
    protected CountResponse processingApiRequest(PolicyAPI openIAMAPI, BaseSearchServiceRequest<PolicySearchBean> requestBody) throws BasicDataServiceException {
        CountResponse response = new CountResponse();
        int count = policyService.count(requestBody.getSearchBean());
        response.setRowCount(count);
        return response;
    }
}
