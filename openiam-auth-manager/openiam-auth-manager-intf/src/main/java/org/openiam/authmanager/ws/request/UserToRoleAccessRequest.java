package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationRole;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToRoleAccessRequest", propOrder = {
	"role"
})
public class UserToRoleAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private AuthorizationRole role;
	
	public UserToRoleAccessRequest() {
		
	}

	public AuthorizationRole getRole() {
		return role;
	}

	public void setRole(AuthorizationRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return String.format(
				"AuthorizationManagerUserToRoleAccessRequest [role=%s]", role);
	}
}
