package org.openiam.base.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Request to Logout a user
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogoutRequest", propOrder = {
        "userId",
        "patternId"
})
public class LogoutRequest  extends BaseServiceRequest  {
    private String userId;
    private String patternId;

    public LogoutRequest() {
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
    
}
