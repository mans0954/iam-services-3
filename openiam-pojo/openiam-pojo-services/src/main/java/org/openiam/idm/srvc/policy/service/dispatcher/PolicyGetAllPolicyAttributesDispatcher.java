package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.PolicyGetAppPolicyAttrubutesRequest;
import org.openiam.base.response.PolicyDefParamFindBeansResponse;
import org.openiam.base.response.PolicyGetResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyGetAllPolicyAttributesDispatcher extends AbstractPolicyDispatcher<PolicyGetAppPolicyAttrubutesRequest,
        PolicyDefParamFindBeansResponse> {
    public PolicyGetAllPolicyAttributesDispatcher() {
        super(PolicyDefParamFindBeansResponse.class);
    }

    @Override
    protected PolicyDefParamFindBeansResponse processingApiRequest(PolicyAPI openIAMAPI,
                                                                   PolicyGetAppPolicyAttrubutesRequest requestBody)
            throws BasicDataServiceException {
        PolicyDefParamFindBeansResponse response = new PolicyDefParamFindBeansResponse();
        response.setPolicyDefParams(policyService.findPolicyDefParamByGroup(requestBody.getId(), requestBody.getPswdGroup()));
        return response;
    }
}
