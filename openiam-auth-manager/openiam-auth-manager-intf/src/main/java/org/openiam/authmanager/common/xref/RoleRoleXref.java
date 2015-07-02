package org.openiam.authmanager.common.xref;

import org.openiam.authmanager.common.model.AuthorizationRole;

public class RoleRoleXref extends AbstractRoleXref {
	
	private AuthorizationRole memberRole;

	public AuthorizationRole getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(AuthorizationRole memberRole) {
		this.memberRole = memberRole;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((memberRole == null) ? 0 : memberRole.hashCode());
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
		RoleRoleXref other = (RoleRoleXref) obj;
		if (memberRole == null) {
			if (other.memberRole != null)
				return false;
		} else if (!memberRole.equals(other.memberRole))
			return false;
		return true;
	}
	

}
