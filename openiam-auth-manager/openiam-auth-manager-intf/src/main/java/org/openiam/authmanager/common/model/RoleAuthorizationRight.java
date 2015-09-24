package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleAuthorizationRight", propOrder = {
	"entity"
})
public class RoleAuthorizationRight extends AbstractAuthorizationRight<AuthorizationRole> {
	
	private AuthorizationRole entity;
	public RoleAuthorizationRight() {}

	public RoleAuthorizationRight(final AuthorizationRole entity) {
		this.entity = entity;
	}

	public AuthorizationRole getEntity() {
		return entity;
	}

	public void setEntity(AuthorizationRole entity) {
		this.entity = entity;
	}

}
