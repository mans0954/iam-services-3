package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

import java.util.List;

/**
 * @author zaporozhec
 */
public class PolicyDefParamFindBeansResponse extends Response {
    private List<PolicyDefParam> policyDefParams;

    public List<PolicyDefParam> getPolicyDefParams() {
        return policyDefParams;
    }

    public void setPolicyDefParams(List<PolicyDefParam> policyDefParams) {
        this.policyDefParams = policyDefParams;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicyDefParamFindBeansResponse{");
        sb.append("policyDefParams=").append(policyDefParams);
        sb.append('}');
        return sb.toString();
    }
}
