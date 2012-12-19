package org.openiam.jaas.callback;

import javax.security.auth.callback.Callback;
import java.io.Serializable;

public class TokenCallback implements Callback, Serializable {
    private String prompt;
    private boolean echoOn;
    private char[] securityToken;
    private String userId;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isEchoOn() {
        return echoOn;
    }

    public void setEchoOn(boolean echoOn) {
        this.echoOn = echoOn;
    }

    public char[] getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(char[] securityToken) {
        this.securityToken = securityToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TokenCallback(String prompt, boolean echoOn) {
        super();
        this.echoOn = echoOn;
        this.prompt = prompt;
    }
}
