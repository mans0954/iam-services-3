package org.openiam.authmanager.ws.response;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RolesForUserResponse", propOrder = {
    "roles"
})
public class RolesForUserResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private Set<AuthorizationRole> roles = new HashSet<AuthorizationRole>();
	
	public RolesForUserResponse() {
		
	}

	public Set<AuthorizationRole> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<AuthorizationRole> roles) {
		this.roles = roles;
	}


	public RolesForUserResponse(final ResponseStatus responseStatus) {
		setResponseStatus(responseStatus);
	}
}
