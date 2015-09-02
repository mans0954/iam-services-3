package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleAuthorizationRight", propOrder = {
})
public class RoleAuthorizationRight extends AbstractAuthorizationRight<AuthorizationRole> {
	
	public RoleAuthorizationRight() {}

	public RoleAuthorizationRight(final AuthorizationRole entity) {
		super(entity);
	}
}
