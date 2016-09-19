package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 16/09/16.
 */
public class AuthProviderListResponse extends Response {
    private List<AuthProvider> authProviderList;

    public List<AuthProvider> getAuthProviderList() {
        return authProviderList;
    }

    public void setAuthProviderList(List<AuthProvider> authProviderList) {
        this.authProviderList = authProviderList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthProviderListResponse{");
        sb.append(super.toString());
        sb.append("authProviderList=").append(authProviderList);
        sb.append('}');
        return sb.toString();
    }
}
