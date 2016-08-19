package org.openiam.base.request;

/**
 * Created by alexander on 11/08/16.
 */
public class RenewTokenRequest extends BaseServiceRequest {
    private String principal;
    private String token;
    private String tokenType;
    private String patternId;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RenewTokenRequest{");
        sb.append("principal='").append(principal).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", tokenType='").append(tokenType).append('\'');
        sb.append(", patternId='").append(patternId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
