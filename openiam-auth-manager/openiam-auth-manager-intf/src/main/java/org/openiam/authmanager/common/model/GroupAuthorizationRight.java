package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupAuthorizationRight", propOrder = {
})
public class GroupAuthorizationRight extends AbstractAuthorizationRight<AuthorizationGroup> {
	
	public GroupAuthorizationRight() {}

	public GroupAuthorizationRight(final AuthorizationGroup entity) {
		super(entity);
	}
}
