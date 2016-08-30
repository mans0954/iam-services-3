package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.role.dto.Role;

import java.util.List;

/**
 * @author zaporozhec
 */
public class RoleGetResponse extends Response {
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleGetResponse{");
        sb.append("role=").append(role);
        sb.append('}');
        return sb.toString();
    }
}
