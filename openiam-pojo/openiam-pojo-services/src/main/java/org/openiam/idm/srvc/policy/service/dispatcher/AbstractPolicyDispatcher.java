package org.openiam.idm.srvc.policy.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.policy.service.PolicyService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.mq.constants.PolicyAPI;
import org.openiam.mq.constants.RoleAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zaporozhec on 8/29/16.
 */
public abstract class AbstractPolicyDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response> extends AbstractAPIDispatcher<RequestBody, ResponseBody, PolicyAPI> {

    @Autowired
    protected PolicyService policyService;

    public AbstractPolicyDispatcher(Class<ResponseBody> responseBodyClass) {
        super(responseBodyClass);
    }

}
