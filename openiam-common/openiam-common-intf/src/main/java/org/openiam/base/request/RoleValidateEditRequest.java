package org.openiam.base.request;

import org.openiam.idm.srvc.role.dto.Role;

/**
 * Created by zaporozhec on 8/29/16.
 */
public class RoleValidateEditRequest extends BaseServiceRequest {
    Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
