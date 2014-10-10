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
        "languageId",
        "uriPatternId",
        "contentProviderId"
})
public class AuthenticationRequest {
	private String languageId;
    private String principal;
    private String password;
    private String clientIP;
    private String nodeIP;
    private String requestSource; // where did this request come from
    private String uriPatternId;
    private String contentProviderId;

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

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public String getUriPatternId() {
		return uriPatternId;
	}

	public void setUriPatternId(String uriPatternId) {
		this.uriPatternId = uriPatternId;
	}

	public String getContentProviderId() {
		return contentProviderId;
	}

	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}

	
}
