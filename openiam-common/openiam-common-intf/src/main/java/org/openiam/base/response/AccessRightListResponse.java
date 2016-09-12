package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;

import java.util.List;

/**
 * Created by alexander on 07/09/16.
 */
public class AccessRightListResponse extends Response {
    List<AccessRight> accessRightList;

    public List<AccessRight> getAccessRightList() {
        return accessRightList;
    }

    public void setAccessRightList(List<AccessRight> accessRightList) {
        this.accessRightList = accessRightList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccessRightListResponse{");
        sb.append(super.toString());
        sb.append(", accessRightList=").append(accessRightList);
        sb.append('}');
        return sb.toString();
    }
}
