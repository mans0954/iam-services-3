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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleAuthorizationRight other = (RoleAuthorizationRight) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	
}
