package org.openiam.base.response;

import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.base.ws.Response;

import java.util.Set;

/**
 * Created by alexander on 15/09/16.
 */
public class RoleAuthorizationRightSetResponse extends Response {
    private Set<RoleAuthorizationRight> roleSet;

    public Set<RoleAuthorizationRight> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<RoleAuthorizationRight> roleSet) {
        this.roleSet = roleSet;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoleAuthorizationRightSetResponse{");
        sb.append(super.toString());
        sb.append(", roleSet=").append(roleSet);
        sb.append('}');
        return sb.toString();
    }
}
