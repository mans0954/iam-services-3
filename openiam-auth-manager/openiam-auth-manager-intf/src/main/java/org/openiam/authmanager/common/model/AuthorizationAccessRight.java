package org.openiam.authmanager.common.model;

import org.openiam.idm.srvc.access.domain.AccessRightEntity;

public class AuthorizationAccessRight {

	private String id;
	private int bitIdx = 0;
	
	public AuthorizationAccessRight() {}
	
	public AuthorizationAccessRight(final AccessRightEntity entity, final int bitIdx) {
		this.id = entity.getId();
		this.bitIdx = bitIdx;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getBitIdx() {
		return bitIdx;
	}
	public void setBitIdx(int bitIdx) {
		this.bitIdx = bitIdx;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bitIdx;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AuthorizationAccessRight other = (AuthorizationAccessRight) obj;
		if (bitIdx != other.bitIdx)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
