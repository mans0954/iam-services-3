package org.openiam.am.srvc.dto.jdbc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationAuthorizationRight", propOrder = {
	"entity"
})
public class OrganizationAuthorizationRight extends AbstractAuthorizationRight<AuthorizationOrganization> {

	private AuthorizationOrganization entity;

	public OrganizationAuthorizationRight() {}

	public OrganizationAuthorizationRight(final AuthorizationOrganization entity) {
		this.entity = entity;
	}

	public AuthorizationOrganization getEntity() {
		return entity;
	}

	public void setEntity(AuthorizationOrganization entity) {
		this.entity = entity;
	}

}
