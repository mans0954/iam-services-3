package org.openiam.authmanager.common.xref;

import org.openiam.authmanager.common.model.AuthorizationResource;

public class ResourceResourceXref extends AbstractResourceXref {

	private AuthorizationResource memberResource;

	public AuthorizationResource getMemberResource() {
		return memberResource;
	}

	public void setMemberResource(AuthorizationResource memberResource) {
		this.memberResource = memberResource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((memberResource == null) ? 0 : memberResource.hashCode());
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
		ResourceResourceXref other = (ResourceResourceXref) obj;
		if (memberResource == null) {
			if (other.memberResource != null)
				return false;
		} else if (!memberResource.equals(other.memberResource))
			return false;
		return true;
	}


}
