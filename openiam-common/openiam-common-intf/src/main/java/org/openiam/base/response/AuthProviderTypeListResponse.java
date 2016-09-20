package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 16/09/16.
 */
public class AuthProviderTypeListResponse extends Response {
    private List<AuthProviderType> authProviderTypeList;

    public List<AuthProviderType> getAuthProviderTypeList() {
        return authProviderTypeList;
    }

    public void setAuthProviderTypeList(List<AuthProviderType> authProviderTypeList) {
        this.authProviderTypeList = authProviderTypeList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthProviderTypeListResponse{");
        sb.append(super.toString());
        sb.append("authProviderTypeList=").append(authProviderTypeList);
        sb.append('}');
        return sb.toString();
    }
}
