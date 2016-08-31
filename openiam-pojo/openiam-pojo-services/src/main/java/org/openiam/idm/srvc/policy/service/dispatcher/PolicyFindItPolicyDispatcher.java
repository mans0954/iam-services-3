package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.CountResponse;
import org.openiam.base.response.ITPolicyResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyFindItPolicyDispatcher extends AbstractPolicyDispatcher<BaseServiceRequest, ITPolicyResponse> {

    public PolicyFindItPolicyDispatcher() {
        super(ITPolicyResponse.class);
    }

    @Override
    protected ITPolicyResponse processingApiRequest(PolicyAPI openIAMAPI, BaseServiceRequest requestBody) throws BasicDataServiceException {
        ITPolicyResponse response = new ITPolicyResponse();
        response.setItPolicy(policyService.findITPolicy());
        return response;
    }
}
