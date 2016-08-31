package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.role.dto.Role;

import java.util.List;

/**
 * @author zaporozhec
 */
public class PolicyFindBeansResponse extends Response {
    private List<Policy> policies;

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicyFindBeansResponse{");
        sb.append("policies=").append(policies);
        sb.append('}');
        return sb.toString();
    }
}
