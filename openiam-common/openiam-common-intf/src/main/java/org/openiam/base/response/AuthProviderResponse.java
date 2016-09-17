package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 16/09/16.
 */
public class AuthProviderResponse extends Response {
    private AuthProvider authProvider;

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthProviderResponse{");
        sb.append(super.toString());
        sb.append("authProvider=").append(authProvider);
        sb.append('}');
        return sb.toString();
    }
}
