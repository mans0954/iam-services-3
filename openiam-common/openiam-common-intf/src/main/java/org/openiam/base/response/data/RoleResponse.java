package org.openiam.base.response.data;

import org.openiam.base.response.data.BaseDataResponse;
import org.openiam.idm.srvc.role.dto.Role;

/**
 * Created by alexander on 22/09/16.
 */
public final class RoleResponse extends BaseDataResponse<Role> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoleResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
