package org.openiam.base.request;

import org.openiam.idm.srvc.policy.dto.ITPolicy;

/**
 * Created by alexander on 08/08/16.
 */
public class PolicySaveOrUpdateITPolicyRequest extends BaseServiceRequest {
    private ITPolicy itPolicy;

    public ITPolicy getItPolicy() {
        return itPolicy;
    }

    public void setItPolicy(ITPolicy itPolicy) {
        this.itPolicy = itPolicy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicySaveOrUpdateITPolicyRequest{");
        sb.append("itPolicy=").append(itPolicy);
        sb.append('}');
        return sb.toString();
    }
}
