package org.openiam.base.request;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class ChallengeResponseCountRequest extends BaseServiceRequest {
    private String userId;
    private boolean isEnterprise;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChallengeResponseCountRequest{");
        sb.append(super.toString());
        sb.append(",                 userId='").append(userId).append('\'');
        sb.append(",                 isEnterprise=").append(isEnterprise);
        sb.append('}');
        return sb.toString();
    }
}
