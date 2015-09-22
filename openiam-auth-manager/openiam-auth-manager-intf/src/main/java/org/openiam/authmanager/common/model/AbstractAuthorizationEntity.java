package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.domain.KeyEntity;

/**
 * @author Lev Bornovalov
 * This class serves two purposes:
 * 1) Each Cached Entity should correspond to 1 unique bit
 * 2) All Entities have a String ID.  Using a bitSet as the hashCode key eliminates
 *    this possibility (since the bit is unique for each entity instance)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthorizationEntity", propOrder = {
        "id",
        "name",
        "description",
        "status",
        "managedSysId"
})
public abstract class AbstractAuthorizationEntity {
	
	public AbstractAuthorizationEntity() {}
	
	public AbstractAuthorizationEntity(final KeyEntity entity) {
		this.id = entity.getId();
	}
	
	public AbstractAuthorizationEntity(final AbstractAuthorizationEntity entity) {
		this.id = entity.id;
		this.name = entity.name;
		this.description = entity.description;
		this.status = entity.status;
		this.managedSysId = entity.managedSysId;
	}

	private String name;
	private String id;
    private String description;
    private String status;
    private String managedSysId;

	@XmlTransient
	private int bitSetIdx = -1;
	
	public String getId() {
		return id;
	}
	
	public AbstractAuthorizationEntity setId(final String id) {
		this.id = id;
		return this;
	}

	public int getBitSetIdx() {
		return bitSetIdx;
	}

	public AbstractAuthorizationEntity setBitSetIdx(int bitSetIdx) {
		this.bitSetIdx = bitSetIdx;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public AbstractAuthorizationEntity setName(String name) {
		this.name = name;
		return this;
	}
	
	protected void makeCopy(final AbstractAuthorizationEntity entity) {
		entity.setId(id);
		entity.setName(name);
		entity.setBitSetIdx(bitSetIdx);
	}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }
    
    /**
     * Formula:  nr + r + b = B
     * n = number of rights
     * r = bit of entity
     * b = bit of right. If no right, b == 0
     * B = output bit
     * @param right
     * @param entity
     * @param numOfRights
     * @return
     */
	protected int getBitIndex(final AuthorizationAccessRight right, final AbstractAuthorizationEntity entity, final int numOfRights) {
		final int n = numOfRights;
		final int r = entity.getBitSetIdx();
		final int b = (right != null) ? right.getBitIdx() : 0;
		return (n * r) + r + b;
	}

	public abstract AbstractAuthorizationEntity shallowCopy();
	
	/**
	 * Reverse engineers the algorithm for calculating a bitset, and return the bit for the 'right'
	 * @param bit - bit from the internal bitset of the Collection
	 * @param entity - Entity you're looking up
	 * @param numOfRights - number of Authorization Rights
	 * @return
	 */
	public static int getRightBit(final int bit, final AbstractAuthorizationEntity entity, final int numOfRights) {
		return bit - (entity.getBitSetIdx() * numOfRights) - entity.getBitSetIdx();
	}
	
	/**
	 * Reverse engineers the algorithm for calculating a bitset, and return the bit for the 'entity'
	 * Returns null if this bit represents a right, but not an entity.  This null is returned purposefully, and
	 * other parts of the code depend on a null value to make certain assumptions.  DO NOT de-nullify this method!
	 * @param bit - bit from the internal bitset of the Collection
	 * @param numOfRights - number of Authorization Rights
	 * @return
	 */
	public static Integer getEntityBit(final int bit, final int numOfRights) {
		/* 
		 * right bit is 0, since you're not looking at rights
		 * if mod is not 0, then it's a right big, return null in this case 
		 */
		return (bit % (numOfRights + 1) == 0) ? (bit / (numOfRights + 1)) : null;
	}
}
