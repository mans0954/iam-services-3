package org.openiam.idm.srvc.auth.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Request to Logout a user
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogoutRequest", propOrder = {
        "userId",
        "contentProviderId"
})
public class LogoutRequest {
    private String userId;
    private String contentProviderId;

    public LogoutRequest() {
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContentProviderId() {
		return contentProviderId;
	}

	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}
    
    
}
