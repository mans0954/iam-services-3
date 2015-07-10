package org.openiam.membership;

import java.util.HashSet;
import java.util.Set;

import org.openiam.base.KeyDTO;

public class MembershipDTO {

	private String id;
	private String entityId;
	private String memberEntityId;
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getMemberEntityId() {
		return memberEntityId;
	}
	public void setMemberEntityId(String memberEntityId) {
		this.memberEntityId = memberEntityId;
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
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((memberEntityId == null) ? 0 : memberEntityId.hashCode());
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
		MembershipDTO other = (MembershipDTO) obj;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (memberEntityId == null) {
			if (other.memberEntityId != null)
				return false;
		} else if (!memberEntityId.equals(other.memberEntityId))
			return false;
		return true;
	}
	
	
}
