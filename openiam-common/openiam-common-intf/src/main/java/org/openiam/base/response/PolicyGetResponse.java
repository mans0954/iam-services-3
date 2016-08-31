package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.policy.dto.Policy;

import java.util.List;

/**
 * @author zaporozhec
 */
public class PolicyGetResponse extends Response {
    private Policy policy;

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicyGetResponse{");
        sb.append("policy=").append(policy);
        sb.append('}');
        return sb.toString();
    }
}
