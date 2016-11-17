package org.openiam.base.response;

import org.openiam.idm.srvc.role.dto.Role;

/**
 * Created by alexander on 22/09/16.
 */
public final class RoleListResponse extends BaseListResponse<Role> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoleListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
