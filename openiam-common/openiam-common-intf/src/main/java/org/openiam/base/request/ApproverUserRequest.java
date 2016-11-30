package org.openiam.base.request;

import java.util.List;

/**
 * Created by alexander on 17/11/16.
 */
public class ApproverUserRequest extends IdServiceRequest {
    private List<String> associationIds;

    public List<String> getAssociationIds() {
        return associationIds;
    }

    public void setAssociationIds(List<String> associationIds) {
        this.associationIds = associationIds;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApproverUserRequest{");
        sb.append(super.toString());
        sb.append(", associationIds=").append(associationIds);
        sb.append('}');
        return sb.toString();
    }
}
