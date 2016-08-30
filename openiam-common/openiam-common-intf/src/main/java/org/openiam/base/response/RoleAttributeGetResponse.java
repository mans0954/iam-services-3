package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

import java.util.List;

/**
 * @author zaporozhec
 */
public class RoleAttributeGetResponse extends Response {
    private List<RoleAttribute> roleAttributes;

    public List<RoleAttribute> getRoleAttributes() {
        return roleAttributes;
    }

    public void setRoleAttributes(List<RoleAttribute> roleAttributes) {
        this.roleAttributes = roleAttributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleAttributeGetResponse{");
        sb.append("roleAttributes=").append(roleAttributes);
        sb.append('}');
        return sb.toString();
    }
}
