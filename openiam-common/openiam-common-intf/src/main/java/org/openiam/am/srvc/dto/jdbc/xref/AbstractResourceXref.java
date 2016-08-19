package org.openiam.am.srvc.dto.jdbc.xref;

import org.openiam.am.srvc.dto.jdbc.AuthorizationResource;

public abstract class AbstractResourceXref extends AbstractAccessRightXref {

	private AuthorizationResource resource;

	public AuthorizationResource getResource() {
		return resource;
	}

	public void setResource(AuthorizationResource resource) {
		this.resource = resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		AbstractResourceXref other = (AbstractResourceXref) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	
}
