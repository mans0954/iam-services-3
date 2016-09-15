package org.openiam.base.response;

import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.base.ws.Response;

import java.util.Set;

/**
 * Created by alexander on 15/09/16.
 */
public class GroupAuthorizationRightSetResponse extends Response {
    private Set<GroupAuthorizationRight> groupSet;

    public Set<GroupAuthorizationRight> getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(Set<GroupAuthorizationRight> groupSet) {
        this.groupSet = groupSet;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GroupAuthorizationRightSetResponse{");
        sb.append(super.toString());
        sb.append(", groupSet=").append(groupSet);
        sb.append('}');
        return sb.toString();
    }
}
