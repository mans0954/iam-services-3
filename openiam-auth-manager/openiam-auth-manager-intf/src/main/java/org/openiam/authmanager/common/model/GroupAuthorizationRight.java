package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupAuthorizationRight", propOrder = {
	"entity"
})
public class GroupAuthorizationRight extends AbstractAuthorizationRight<AuthorizationGroup> {
	
	private AuthorizationGroup entity;
	public GroupAuthorizationRight() {}

	public GroupAuthorizationRight(final AuthorizationGroup entity) {
		this.entity = entity;
	}

	public AuthorizationGroup getEntity() {
		return entity;
	}

	public void setEntity(AuthorizationGroup entity) {
		this.entity = entity;
	}
}
