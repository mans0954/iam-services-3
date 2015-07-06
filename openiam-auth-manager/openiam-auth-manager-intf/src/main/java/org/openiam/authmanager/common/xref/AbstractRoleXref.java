package org.openiam.authmanager.common.xref;

import org.openiam.authmanager.common.model.AuthorizationRole;

public abstract class AbstractRoleXref extends AbstractAccessRightXref {

	private AuthorizationRole role;

	public AuthorizationRole getRole() {
		return role;
	}

	public void setRole(AuthorizationRole role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		AbstractRoleXref other = (AbstractRoleXref) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}


}
