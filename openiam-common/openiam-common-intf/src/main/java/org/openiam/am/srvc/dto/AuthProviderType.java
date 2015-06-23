package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderType", propOrder = {
        "description",
        "isActive",
        "hasPublicKey",
        "hasPrivateKey",
        "chainable"
})
@DozerDTOCorrespondence(AuthProviderTypeEntity.class)
public class AuthProviderType extends KeyDTO {
    private String description;
    private boolean isActive = true;
    private boolean hasPublicKey;
    private boolean hasPrivateKey;
    private boolean chainable;
    
    @XmlTransient
    private Set<AuthAttributeEntity> attributeSet;
    @XmlTransient
    private Set<AuthProviderEntity> providerSet;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<AuthAttributeEntity> getAttributeSet() {
        return attributeSet;
    }

    public void setAttributeSet(Set<AuthAttributeEntity> attributeSet) {
        this.attributeSet = attributeSet;
    }

    public Set<AuthProviderEntity> getProviderSet() {
        return providerSet;
    }

    public void setProviderSet(Set<AuthProviderEntity> providerSet) {
        this.providerSet = providerSet;
    }

    public boolean isHasPublicKey() {
		return hasPublicKey;
	}

	public void setHasPublicKey(boolean hasPublicKey) {
		this.hasPublicKey = hasPublicKey;
	}

	public boolean isHasPrivateKey() {
		return hasPrivateKey;
	}

	public void setHasPrivateKey(boolean hasPrivateKey) {
		this.hasPrivateKey = hasPrivateKey;
	}
	
	public boolean isChainable() {
		return chainable;
	}

	public void setChainable(boolean chainable) {
		this.chainable = chainable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (hasPrivateKey ? 1231 : 1237);
		result = prime * result + (hasPublicKey ? 1231 : 1237);
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (chainable ? 1231 : 1237);
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
		AuthProviderType other = (AuthProviderType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hasPrivateKey != other.hasPrivateKey)
			return false;
		if (hasPublicKey != other.hasPublicKey)
			return false;
		if (isActive != other.isActive)
			return false;
		if (chainable != other.chainable)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthProviderType [description=%s, isActive=%s, hasPublicKey=%s, hasPrivateKey=%s, toString()=%s]",
						description, isActive, hasPublicKey, hasPrivateKey,
						super.toString());
	}

	
}
