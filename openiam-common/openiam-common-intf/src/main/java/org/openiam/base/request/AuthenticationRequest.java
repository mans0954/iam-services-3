package org.openiam.base.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Request object used to authenticate users.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationRequest", propOrder = {
        "principal",
        "password",
        "clientIP",
        "nodeIP",
        "requestSource",
        "patternId",
        "methodId",
        "socialUserProfile",
        "authPolicyId",
        "skipPasswordCheck",
        "skipUserStatusCheck"
})
public class AuthenticationRequest  extends BaseServiceRequest  {
    private String principal;
    private String password;
    private String clientIP;
    private String nodeIP;
    private String requestSource; // where did this request come from
    private String patternId;
    private String methodId;
    String authPolicyId; // auth policy for connection
    private String socialUserProfile; // user profile in socials networks
    private boolean skipUserStatusCheck;

    /* replaces kerbAuth and certAuth flags */
    private boolean skipPasswordCheck;

    public AuthenticationRequest() {
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}

    public String getSocialUserProfile() {
        return socialUserProfile;
    }

    public void setSocialUserProfile(String socialUserProfile) {
        this.socialUserProfile = socialUserProfile;
    }

    public String getAuthPolicyId() {
        return authPolicyId;
    }

    public void setAuthPolicyId(String authPolicyId) {
        this.authPolicyId = authPolicyId;
    }

	public boolean isSkipPasswordCheck() {
		return skipPasswordCheck;
	}

	public void setSkipPasswordCheck(boolean skipPasswordCheck) {
		this.skipPasswordCheck = skipPasswordCheck;
	}

	public boolean isSkipUserStatusCheck() {
		return skipUserStatusCheck;
	}

	public void setSkipUserStatusCheck(boolean skipUserStatusCheck) {
		this.skipUserStatusCheck = skipUserStatusCheck;
	}

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthenticationRequest{");
        sb.append(super.toString());
        sb.append(", principal='").append(principal).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", clientIP='").append(clientIP).append('\'');
        sb.append(", nodeIP='").append(nodeIP).append('\'');
        sb.append(", requestSource='").append(requestSource).append('\'');
        sb.append(", patternId='").append(patternId).append('\'');
        sb.append(", methodId='").append(methodId).append('\'');
        sb.append(", authPolicyId='").append(authPolicyId).append('\'');
        sb.append(", socialUserProfile='").append(socialUserProfile).append('\'');
        sb.append(", skipUserStatusCheck=").append(skipUserStatusCheck);
        sb.append(", skipPasswordCheck=").append(skipPasswordCheck);
        sb.append('}');
        return sb.toString();
    }
}
