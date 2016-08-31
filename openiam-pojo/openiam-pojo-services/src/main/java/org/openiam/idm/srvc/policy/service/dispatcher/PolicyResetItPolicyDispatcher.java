package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.ITPolicyResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.PolicyAPI;
import org.springframework.stereotype.Component;

/**
 * Created by zaporozhec on 8/29/16.
 */
@Component
public class PolicyResetItPolicyDispatcher extends AbstractPolicyDispatcher<BaseServiceRequest, Response> {

    public PolicyResetItPolicyDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(PolicyAPI openIAMAPI, BaseServiceRequest requestBody) throws BasicDataServiceException {
        policyService.resetITPolicy();
        return new Response(ResponseStatus.SUCCESS);
    }
}
