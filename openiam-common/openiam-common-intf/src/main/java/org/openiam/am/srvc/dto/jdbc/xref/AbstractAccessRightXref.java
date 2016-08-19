package org.openiam.am.srvc.dto.jdbc.xref;

import java.util.HashSet;
import java.util.Set;

import org.openiam.am.srvc.dto.jdbc.AuthorizationAccessRight;

public abstract class AbstractAccessRightXref {

	private Set<AuthorizationAccessRight> rights;

	public Set<AuthorizationAccessRight> getRights() {
		return rights;
	}

	public void setRights(Set<AuthorizationAccessRight> rights) {
		this.rights = rights;
	}

	public void addRight(final AuthorizationAccessRight right) {
		if(this.rights == null) {
			this.rights = new HashSet<AuthorizationAccessRight>();
		}
		this.rights.add(right);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAccessRightXref other = (AbstractAccessRightXref) obj;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		return true;
	}
	
	
}
