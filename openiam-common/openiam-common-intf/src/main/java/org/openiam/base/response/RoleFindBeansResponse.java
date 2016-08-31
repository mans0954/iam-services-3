package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.role.dto.Role;

import java.util.List;

/**
 * @author zaporozhec
 */
public class RoleFindBeansResponse extends Response {
    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleFindBeansResponse{");
        sb.append("roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }
}
