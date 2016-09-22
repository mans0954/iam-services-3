package org.openiam.base.request;

/**
 * Created by alexander on 20/09/16.
 */
public class SSOAttributesRequest extends BaseServiceRequest {
    private String userId;
    private String providerId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SSOAttributesRequest{");
        sb.append(super.toString());
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", providerId='").append(providerId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
