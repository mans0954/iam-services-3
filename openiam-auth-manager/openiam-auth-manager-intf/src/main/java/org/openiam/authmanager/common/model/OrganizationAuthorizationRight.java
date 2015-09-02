package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationAuthorizationRight", propOrder = {
})
public class OrganizationAuthorizationRight extends AbstractAuthorizationRight<AuthorizationOrganization> {
	public OrganizationAuthorizationRight() {}

	public OrganizationAuthorizationRight(final AuthorizationOrganization entity) {
		super(entity);
	}
}
