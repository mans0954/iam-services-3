package org.openiam.am.srvc.dto.jdbc.xref;

import org.openiam.am.srvc.dto.jdbc.AuthorizationUser;

public class ResourceUserXref extends AbstractResourceXref {
	
	private AuthorizationUser user;

	public AuthorizationUser getUser() {
		return user;
	}

	public void setUser(AuthorizationUser user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		ResourceUserXref other = (ResourceUserXref) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	

}
