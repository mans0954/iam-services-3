package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceAuthorizationRight", propOrder = {
})
public class ResourceAuthorizationRight extends AbstractAuthorizationRight<AuthorizationResource> {
	
	public ResourceAuthorizationRight() {}

	public ResourceAuthorizationRight(final AuthorizationResource entity) {
		super(entity);
	}
}
