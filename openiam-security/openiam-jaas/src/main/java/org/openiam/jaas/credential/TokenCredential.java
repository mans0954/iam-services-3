package org.openiam.jaas.credential;

import org.openiam.idm.srvc.auth.context.Credential;

public class TokenCredential implements Credential{
    String userId;
    String token;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public TokenCredential(String userId,String token) {
        super();
        this.token = token;
        this.userId = userId;
    }
}
