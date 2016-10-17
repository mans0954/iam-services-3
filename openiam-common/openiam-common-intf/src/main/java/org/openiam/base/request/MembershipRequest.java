package org.openiam.base.request;

import java.util.Date;
import java.util.Set;

/**
 * Created by zaporozhec on 8/30/16.
 */
public class MembershipRequest extends BaseServiceRequest {
    private String objectId;
    private String linkedObjectId;
    private Set<String> rightIds;
    private Date startDate;
    private Date endDate;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLinkedObjectId() {
        return linkedObjectId;
    }

    public void setLinkedObjectId(String linkedObjectId) {
        this.linkedObjectId = linkedObjectId;
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
