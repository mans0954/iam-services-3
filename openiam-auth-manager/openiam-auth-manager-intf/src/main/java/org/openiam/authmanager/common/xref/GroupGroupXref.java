package org.openiam.authmanager.common.xref;

public class GroupGroupXref {

	private String groupId;
	private String memberGroupId;
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getMemberGroupId() {
		return memberGroupId;
	}
	
	public void setMemberGroupId(String memberGroupId) {
		this.memberGroupId = memberGroupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result
				+ ((memberGroupId == null) ? 0 : memberGroupId.hashCode());
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
		GroupGroupXref other = (GroupGroupXref) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (memberGroupId == null) {
			if (other.memberGroupId != null)
				return false;
		} else if (!memberGroupId.equals(other.memberGroupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("GroupGroupXref [groupId=%s, memberGroupId=%s]",
				groupId, memberGroupId);
	}
	
	
}
