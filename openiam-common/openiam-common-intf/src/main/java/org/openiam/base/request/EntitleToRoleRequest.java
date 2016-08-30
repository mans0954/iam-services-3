package org.openiam.base.request;

import java.util.Date;
import java.util.Set;

/**
 * Created by zaporozhec on 8/30/16.
 */
public class EntitleToRoleRequest extends BaseServiceRequest {
    private String roleId;
    private String linkedObjectId;
    private String requesterId;
    private Set<String> rightIds;
    private Date startDate;
    private Date endDate;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getLinkedObjectId() {
        return linkedObjectId;
    }

    public void setLinkedObjectId(String linkedObjectId) {
        this.linkedObjectId = linkedObjectId;
    }

    @Override
    public String getRequesterId() {
        return requesterId;
    }

    @Override
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Set<String> getRightIds() {
        return rightIds;
    }

    public void setRightIds(Set<String> rightIds) {
        this.rightIds = rightIds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
