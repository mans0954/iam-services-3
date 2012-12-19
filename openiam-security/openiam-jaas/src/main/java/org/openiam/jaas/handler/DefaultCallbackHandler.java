package org.openiam.jaas.handler;


import org.openiam.jaas.callback.TokenCallback;

import javax.security.auth.callback.*;
import java.io.IOException;

public class DefaultCallbackHandler extends AbstractCalbackHandler{
    private String userName;
    private String password;
    private String token;
    private String userId;

    @Override
    protected void processCallback(Callback callback)throws IOException, UnsupportedCallbackException {
        if (callback instanceof TokenCallback) {
            log.debug("TokenCallback found");
            ((TokenCallback)callback).setSecurityToken(this.getToken());
            ((TokenCallback)callback).setUserId(this.getUserId());
        } else {
            super.processCallback(callback);
        }
    }

    @Override
    public void clean(){
        userName = null;
        password = null;
        token = null;
        userId = null;
    }

    @Override
    protected String getUserName() {
        return (userName==null)?"":userName;
    }

    @Override
    protected char[] getPassword() {
        return (password==null || password.isEmpty()) ? new char[0]: password.toCharArray();
    }

    private char[] getToken() {
        return (token==null || token.isEmpty()) ? new char[0]: token.toCharArray();
    }
    private String getUserId() {
        return (userId==null || userId.isEmpty()) ? null: userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
