package org.openiam.idm.srvc.auth.dto;

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
        "authPolicyId"
})
public class AuthenticationRequest {
    String principal;
    String password;
    String clientIP;
    String nodeIP;
    String requestSource; // where did this request come from
    String authPolicyId; // auth policy for connection
    public final static String AUTH_POLICY_ID = "AUTH_POLICY_ID";
    public final static String MANAGED_SYS_ID = "MANAGED_SYS_ID";
    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String principal, String password, String clientIP, String nodeIP) {
        this.principal = principal;
        this.password = password;
        this.clientIP = clientIP;
        this.nodeIP = nodeIP;

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

    public String getAuthPolicyId() {
        return authPolicyId;
    }

    public void setAuthPolicyId(String authPolicyId) {
        this.authPolicyId = authPolicyId;
    }
}
