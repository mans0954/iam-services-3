package org.openiam.am.srvc.dto.jdbc.xref;

import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;

public abstract class AbstractGroupXref extends AbstractAccessRightXref {

	private AuthorizationGroup group;

	public AuthorizationGroup getGroup() {
		return group;
	}

	public void setGroup(AuthorizationGroup group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		AbstractGroupXref other = (AbstractGroupXref) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}
}
