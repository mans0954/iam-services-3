package org.openiam.base.request;

/**
 * Created by alexander on 15/09/16.
 */
public class GetEntitlementRequest extends BaseServiceRequest {
    private String userId;
    private String targetObjectId;
    private String rightId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetObjectId() {
        return targetObjectId;
    }

    public void setTargetObjectId(String targetObjectId) {
        this.targetObjectId = targetObjectId;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GetEntitlementRequest{");
        sb.append(super.toString());
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", targetObjectId='").append(targetObjectId).append('\'');
        sb.append(", rightId='").append(rightId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
