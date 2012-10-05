package org.openiam.authmanager.common.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Lev Bornovalov
 * This class serves two purposes:
 * 1) Each Cached Entity should correspond to 1 unique bit
 * 2) All Entities have a String ID.  Using a bitSet as the hashCode key eliminates
 *    this possibility (since the bit is unique for each entity instance)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthorizationEntity", propOrder = {
        "id"
})
public abstract class AbstractAuthorizationEntity {

	private String id;
	
	@XmlTransient
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
		AbstractAuthorizationEntity other = (AbstractAuthorizationEntity) obj;
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
