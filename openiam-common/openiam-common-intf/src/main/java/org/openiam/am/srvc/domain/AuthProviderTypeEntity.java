package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "AUTH_PROVIDER_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(AuthProviderType.class)
@AttributeOverride(name = "id", column = @Column(name = "PROVIDER_TYPE"))
public class AuthProviderTypeEntity extends KeyEntity {
	
    @Column(name="DESCRIPTION", length = 50, nullable = true)
    private String description;
    
    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;
    
    @Column(name="HAS_PUBLIC_KEY")
    @Type(type = "yes_no")
    private boolean hasPublicKey;
    
    @Column(name="HAS_PRIVATE_KEY")
    @Type(type = "yes_no")
    private boolean hasPrivateKey;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<AuthAttributeEntity> attributeSet;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (hasPrivateKey ? 1231 : 1237);
		result = prime * result + (hasPublicKey ? 1231 : 1237);
		result = prime * result + (isActive ? 1231 : 1237);
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
		AuthProviderTypeEntity other = (AuthProviderTypeEntity) obj;
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
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthProviderTypeEntity [description=%s, isActive=%s, hasPublicKey=%s, hasPrivateKey=%s, toString()=%s]",
						description, isActive, hasPublicKey, hasPrivateKey,
						super.toString());
	}

	
}
