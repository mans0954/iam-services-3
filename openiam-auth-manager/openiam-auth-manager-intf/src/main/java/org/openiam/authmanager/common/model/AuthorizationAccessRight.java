package org.openiam.authmanager.common.model;

import org.openiam.idm.srvc.access.domain.AccessRightEntity;

public class AuthorizationAccessRight {

	private String name;
	private String id;
	private int bitIdx = 0;
	
	public AuthorizationAccessRight() {}
	
	public AuthorizationAccessRight(final AccessRightEntity entity, final int bitIdx) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.bitIdx = bitIdx;
	}
	
	public String getId() {
		return id;
	}
	public AuthorizationAccessRight setId(String id) {
		this.id = id;
		return this;
	}
	public int getBitIdx() {
		return bitIdx;
	}
	public AuthorizationAccessRight setBitIdx(int bitIdx) {
		this.bitIdx = bitIdx;
		return this;
	}

	public String getName() {
		return name;
	}

	public AuthorizationAccessRight setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bitIdx;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
