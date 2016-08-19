package org.openiam.am.srvc.dto.jdbc.xref;

import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;

public class GroupGroupXref extends AbstractGroupXref {

	private AuthorizationGroup memberGroup;

	public AuthorizationGroup getMemberGroup() {
		return memberGroup;
	}

	public void setMemberGroup(AuthorizationGroup memberGroup) {
		this.memberGroup = memberGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((memberGroup == null) ? 0 : memberGroup.hashCode());
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
		GroupGroupXref other = (GroupGroupXref) obj;
		if (memberGroup == null) {
			if (other.memberGroup != null)
				return false;
		} else if (!memberGroup.equals(other.memberGroup))
			return false;
		return true;
	}
	

}
