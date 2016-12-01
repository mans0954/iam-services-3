package org.openiam.base.response.list;

import org.openiam.base.response.list.BaseListResponse;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;


/**
 * @author zaporozhec
 */
public class PolicyDefParamListResponse extends BaseListResponse<PolicyDefParam> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PolicyDefParamListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
