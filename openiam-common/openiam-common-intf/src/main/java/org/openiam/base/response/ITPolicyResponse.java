package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.role.dto.Role;

/**
 * @author zaporozhec
 */
public class ITPolicyResponse extends Response {
    private ITPolicy itPolicy;

    public ITPolicy getItPolicy() {
        return itPolicy;
    }

    public void setItPolicy(ITPolicy itPolicy) {
        this.itPolicy = itPolicy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ITPolicyResponse{");
        sb.append("itPolicy=").append(itPolicy);
        sb.append('}');
        return sb.toString();
    }
}
