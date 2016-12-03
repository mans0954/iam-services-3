package org.openiam.base.response.data;

import org.openiam.base.response.data.BaseDataResponse;
import org.openiam.idm.srvc.policy.dto.ITPolicy;

/**
 * @author zaporozhec
 */
public class ITPolicyResponse extends BaseDataResponse<ITPolicy> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ITPolicyResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
