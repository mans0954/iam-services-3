package org.openiam.membership;

import org.openiam.base.KeyDTO;

public class MembershipRightDTO {

	private String id;
	private String rightId;
	public String getRightId() {
		return rightId;
	}
	public void setRightId(String rightId) {
		this.rightId = rightId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((rightId == null) ? 0 : rightId.hashCode());
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
		MembershipRightDTO other = (MembershipRightDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rightId == null) {
			if (other.rightId != null)
				return false;
		} else if (!rightId.equals(other.rightId))
			return false;
		return true;
	}
	
	
}
