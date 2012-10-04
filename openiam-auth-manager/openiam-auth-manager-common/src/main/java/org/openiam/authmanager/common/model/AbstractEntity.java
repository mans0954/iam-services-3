package org.openiam.authmanager.common.model;

import java.io.Serializable;

/**
 * @author Lev Bornovalov
 * This class serves two purposes:
 * 1) Each Cached Entity should correspond to 1 unique bit
 * 2) All Entities have a String ID.  Using a bitSet as the hashCode key eliminates
 *    this possibility (since the bit is unique for each entity instance)
 */
public abstract class AbstractEntity {

	private String id;
	private int bitSetIdx = -1;
	
	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}

	public int getBitSetIdx() {
		return bitSetIdx;
	}

	public void setBitSetIdx(int bitSetIdx) {
		this.bitSetIdx = bitSetIdx;
	}
	
	public abstract void compile();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		AbstractEntity other = (AbstractEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AbstractEntity [id=%s, bitSetIdx=%s]", id,
				bitSetIdx);
	}

	
}
