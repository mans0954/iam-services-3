package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.PolicyFindBeansResponse;
import org.openiam.base.response.RoleFindBeansResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.dispatcher.AbstractRoleDispatcher;
import org.openiam.mq.constants.PolicyAPI;
import org.openiam.mq.constants.RoleAPI;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyFindBeansDispatcher extends AbstractPolicyDispatcher<BaseSearchServiceRequest<PolicySearchBean>,
        PolicyFindBeansResponse> {
    public PolicyFindBeansDispatcher() {
        super(PolicyFindBeansResponse.class);
    }

    @Override
    protected PolicyFindBeansResponse processingApiRequest(PolicyAPI openIAMAPI,
                                                           BaseSearchServiceRequest<PolicySearchBean> requestBody)
            throws BasicDataServiceException {
        PolicyFindBeansResponse response = new PolicyFindBeansResponse();
        response.setPolicies(policyService.findBeans(requestBody.getSearchBean(), requestBody.getFrom(), requestBody.getSize()));
        return response;
    }
}
