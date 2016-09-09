package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;

/**
 * Created by alexander on 07/09/16.
 */
public class AccessRightResponse extends Response {
    AccessRight accessRight;

    public AccessRight getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(AccessRight accessRight) {
        this.accessRight = accessRight;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccessRightResponse{");
        sb.append(super.toString());
        sb.append(", accessRight=").append(accessRight);
        sb.append('}');
        return sb.toString();
    }
}
