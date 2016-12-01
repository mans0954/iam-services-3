package org.openiam.base.response;

import org.openiam.idm.srvc.role.dto.RoleAttribute;

/**
 * Created by alexander on 16/11/16.
 */
public final class RoleAttributeListResponse extends BaseListResponse<RoleAttribute> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RoleAttributeListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
