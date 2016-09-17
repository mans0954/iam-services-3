package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 16/09/16.
 */
public class AuthProviderTypeResponse  extends Response {
    private AuthProviderType authProviderType;

    public AuthProviderType getAuthProviderType() {
        return authProviderType;
    }

    public void setAuthProviderType(AuthProviderType authProviderType) {
        this.authProviderType = authProviderType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthProviderTypeResponse{");
        sb.append(super.toString());
        sb.append("authProviderType=").append(authProviderType);
        sb.append('}');
        return sb.toString();
    }
}
