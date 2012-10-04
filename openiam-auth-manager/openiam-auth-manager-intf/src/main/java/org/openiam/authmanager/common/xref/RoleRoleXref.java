package org.openiam.authmanager.common.xref;

public class RoleRoleXref {
	
	private String roleId;
	private String memberRoleId;
	
	public String getRoleId() {
		return roleId;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public String getMemberRoleId() {
		return memberRoleId;
	}
	
	public void setMemberRoleId(String memberRoleId) {
		this.memberRoleId = memberRoleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((memberRoleId == null) ? 0 : memberRoleId.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
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
		RoleRoleXref other = (RoleRoleXref) obj;
		if (memberRoleId == null) {
			if (other.memberRoleId != null)
				return false;
		} else if (!memberRoleId.equals(other.memberRoleId))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("RoleRoleXref [roleId=%s, memberRoleId=%s]",
				roleId, memberRoleId);
	}
	
	
}
