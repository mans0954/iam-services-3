package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserRequest", propOrder = {
        "userId",
        "loginId"
})
public class UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private String userId;
	private AuthorizationManagerLoginId loginId;
	
	public UserRequest() {
		
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AuthorizationManagerLoginId getLoginId() {
		return loginId;
	}

	public void setLoginId(AuthorizationManagerLoginId loginId) {
		this.loginId = loginId;
	}

	@Override
	public String toString() {
		return String.format(
				"AuthorizationManagerUserRequest [userId=%s, loginId=%s]",
				userId, loginId);
	}
	
	
}
