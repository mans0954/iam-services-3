package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.PolicySavePolicyRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.response.ITPolicyResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicySavePolicyDispatcher extends AbstractPolicyDispatcher<PolicySavePolicyRequest, StringResponse> {

    public PolicySavePolicyDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(PolicyAPI openIAMAPI, PolicySavePolicyRequest requestBody) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(policyService.savePolicy(requestBody.getPolicy()));
        return response;
    }
}
