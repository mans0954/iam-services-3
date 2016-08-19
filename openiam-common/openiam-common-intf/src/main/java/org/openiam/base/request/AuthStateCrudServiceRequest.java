package org.openiam.base.request;

import org.openiam.idm.srvc.auth.domain.AuthStateEntity;

/**
 * Created by alexander on 09/08/16.
 */
public class AuthStateCrudServiceRequest extends BaseServiceRequest {
    private AuthStateEntity authStateEntity;

    public AuthStateCrudServiceRequest(AuthStateEntity authStateEntity) {
        this.authStateEntity = authStateEntity;
    }

    public AuthStateEntity getAuthStateEntity() {
        return authStateEntity;
    }

    public void setAuthStateEntity(AuthStateEntity authStateEntity) {
        this.authStateEntity = authStateEntity;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthStateCrudServiceRequest{");
        sb.append("authStateEntity=").append(authStateEntity);
        sb.append('}');
        return sb.toString();
    }
}
