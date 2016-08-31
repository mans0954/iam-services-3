package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.PolicySavePolicyRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyDeletePolicyDispatcher extends AbstractPolicyDispatcher<IdServiceRequest, BooleanResponse> {

    public PolicyDeletePolicyDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(PolicyAPI openIAMAPI, IdServiceRequest requestBody) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        policyService.deletePolicy(requestBody.getId());
        response.setValue(Boolean.TRUE);
        return response;
    }
}
