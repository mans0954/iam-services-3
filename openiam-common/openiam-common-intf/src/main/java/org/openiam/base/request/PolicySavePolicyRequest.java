package org.openiam.base.request;

import org.openiam.idm.srvc.policy.dto.Policy;

/**
 * Created by alexander on 08/08/16.
 */
public class PolicySavePolicyRequest extends BaseServiceRequest {
    private Policy policy;

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicySavePolicyRequest{");
        sb.append("policy=").append(policy);
        sb.append('}');
        return sb.toString();
    }
}
