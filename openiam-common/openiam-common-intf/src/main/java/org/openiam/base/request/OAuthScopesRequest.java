package org.openiam.base.request;

/**
 * Created by alexander on 28/09/16.
 */
public class OAuthScopesRequest extends BaseServiceRequest {
    private String clientId;
    private String userId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OAuthScopesRequest{");
        sb.append(super.toString());
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
