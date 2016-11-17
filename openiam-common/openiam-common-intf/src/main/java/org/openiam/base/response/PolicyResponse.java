package org.openiam.base.response;

import org.openiam.idm.srvc.policy.dto.Policy;


/**
 * @author zaporozhec
 */
public class PolicyResponse extends BaseDataResponse<Policy> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PolicyResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
