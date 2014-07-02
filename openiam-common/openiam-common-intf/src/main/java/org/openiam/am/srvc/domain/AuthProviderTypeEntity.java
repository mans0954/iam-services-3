package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "AUTH_PROVIDER_TYPE")
@DozerDTOCorrespondence(AuthProviderType.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthProviderTypeEntity implements Serializable {
    @Id
    @Column(name="PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;
    @Column(name="DESCRIPTION", length = 50, nullable = true)
    private String description;
    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<AuthAttributeEntity> attributeSet;
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<AuthProviderEntity> providerSet;

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuthProviderTypeEntity that = (AuthProviderTypeEntity) o;

        if (isActive != that.isActive) {
            return false;
        }
        if (!description.equals(that.description)) {
            return false;
        }
        if (!providerType.equals(that.providerType)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = providerType.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (isActive ? 1 : 0);
        return result;
    }
}
