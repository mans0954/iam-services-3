package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.PolicyFindBeansResponse;
import org.openiam.base.response.PolicyGetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyGetPolicyDispatcher extends AbstractPolicyDispatcher<IdServiceRequest,
        PolicyGetResponse> {
    public PolicyGetPolicyDispatcher() {
        super(PolicyGetResponse.class);
    }

    @Override
    protected PolicyGetResponse processingApiRequest(PolicyAPI openIAMAPI,
                                                     IdServiceRequest requestBody)
            throws BasicDataServiceException {
        PolicyGetResponse response = new PolicyGetResponse();
        response.setPolicy(policyService.getPolicy(requestBody.getId()));
        return response;
    }
}
