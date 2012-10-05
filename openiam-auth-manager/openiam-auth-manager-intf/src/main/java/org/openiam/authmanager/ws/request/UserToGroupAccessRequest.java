package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationGroup;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToGroupAccessRequest", propOrder = {
	"group"
})
public class UserToGroupAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private AuthorizationGroup group;

	public AuthorizationGroup getGroup() {
		return group;
	}

	public void setGroup(AuthorizationGroup group) {
		this.group = group;
	}
	
	public UserToGroupAccessRequest() {
		
	}

	@Override
	public String toString() {
		return String.format(
				"AuthorizationManagerUserToGroupAccessRequest [group=%s]",
				group);
	}
	
	
}
