package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceAuthorizationRight", propOrder = {
	"entity"
})
public class ResourceAuthorizationRight extends AbstractAuthorizationRight<AuthorizationResource> {
	
	private AuthorizationResource entity;
	public ResourceAuthorizationRight() {}

	public ResourceAuthorizationRight(final AuthorizationResource entity) {
		this.entity = entity;
	}

	public AuthorizationResource getEntity() {
		return entity;
	}

	public void setEntity(AuthorizationResource entity) {
		this.entity = entity;
	}

}
