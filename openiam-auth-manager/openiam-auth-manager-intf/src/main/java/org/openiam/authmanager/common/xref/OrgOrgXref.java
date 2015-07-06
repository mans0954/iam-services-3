package org.openiam.authmanager.common.xref;

import org.openiam.authmanager.common.model.AuthorizationOrganization;

public class OrgOrgXref extends AbstractOrgXref {

	private AuthorizationOrganization memberOrganization;

	public AuthorizationOrganization getMemberOrganization() {
		return memberOrganization;
	}

	public void setMemberOrganization(AuthorizationOrganization memberOrganization) {
		this.memberOrganization = memberOrganization;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((memberOrganization == null) ? 0 : memberOrganization
						.hashCode());
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
		OrgOrgXref other = (OrgOrgXref) obj;
		if (memberOrganization == null) {
			if (other.memberOrganization != null)
				return false;
		} else if (!memberOrganization.equals(other.memberOrganization))
			return false;
		return true;
	}


}
