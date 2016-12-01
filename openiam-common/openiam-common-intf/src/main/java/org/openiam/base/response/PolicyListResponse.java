package org.openiam.base.response;

import org.openiam.idm.srvc.policy.dto.Policy;

/**
 * @author zaporozhec
 */
public class PolicyListResponse extends BaseListResponse<Policy> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PolicyListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
