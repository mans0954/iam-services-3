package org.openiam.am.srvc.dto.jdbc.xref;

import org.openiam.am.srvc.dto.jdbc.AuthorizationOrganization;

public abstract class AbstractOrgXref extends AbstractAccessRightXref {

	private AuthorizationOrganization organization;

	public AuthorizationOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(AuthorizationOrganization organization) {
		this.organization = organization;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
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
		AbstractOrgXref other = (AbstractOrgXref) obj;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		return true;
	}

	
}
