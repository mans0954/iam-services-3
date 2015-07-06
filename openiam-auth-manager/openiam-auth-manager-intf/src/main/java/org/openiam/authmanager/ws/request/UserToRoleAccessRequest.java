package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationRole;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToRoleAccessRequest", propOrder = {
	"roleId"
})
public class UserToRoleAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private String roleId;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	
}
