package org.openiam.idm.srvc.policy.service.dispatcher;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.openiam.base.request.PolicyGetAppPolicyAttrubutesRequest;
import org.openiam.base.request.PolicySaveOrUpdateITPolicyRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.response.PolicyDefParamFindBeansResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicySaveOrUpdateITPolicyDispatcher extends AbstractPolicyDispatcher<PolicySaveOrUpdateITPolicyRequest,
        BooleanResponse> {
    public PolicySaveOrUpdateITPolicyDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(PolicyAPI openIAMAPI,
                                                   PolicySaveOrUpdateITPolicyRequest requestBody)
            throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        policyService.saveITPolicy(requestBody.getItPolicy());
        response.setValue(Boolean.TRUE);
        return response;
    }
}
