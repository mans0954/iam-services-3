package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "AUTH_PROVIDER_ATTRIBUTE")
@DozerDTOCorrespondence(AuthProviderAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "PROVIDER_ATTRIBUTE_ID"))
public class AuthProviderAttributeEntity extends KeyEntity {

    @Column(name="VALUE", length = 4096, nullable = false)
    private String value;
    
    @Column(name="DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = false)
    private AuthProviderEntity provider;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AUTH_ATTRIBUTE_ID", referencedColumnName = "AUTH_ATTRIBUTE_ID", insertable = true, updatable = true)
    private AuthAttributeEntity attribute;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuthProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(AuthProviderEntity provider) {
        this.provider = provider;
    }

    public AuthAttributeEntity getAttribute() {
        return attribute;
    }

    public void setAttribute(AuthAttributeEntity attribute) {
        this.attribute = attribute;
    }

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		AuthProviderAttributeEntity other = (AuthProviderAttributeEntity) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (dataType != other.dataType)
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
    
    
}
