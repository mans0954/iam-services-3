package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;

import java.util.List;

/**
 * Created by alexander on 11/08/16.
 */
public class AuthStateListResponse extends Response {
    private List<AuthStateEntity> authStateList;

    public List<AuthStateEntity> getAuthStateList() {
        return authStateList;
    }

    public void setAuthStateList(List<AuthStateEntity> authStateList) {
        this.authStateList = authStateList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthStateListResponse{");
        sb.append(super.toString());
        sb.append(", authStateList=").append(authStateList);
        sb.append('}');
        return sb.toString();
    }
}
