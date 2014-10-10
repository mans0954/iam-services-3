package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "AUTH_ATTRIBUTE", uniqueConstraints = {
        @UniqueConstraint(columnNames={"ATTRIBUTE_NAME", "PROVIDER_TYPE"})
})
@DozerDTOCorrespondence(AuthAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "AUTH_ATTRIBUTE_ID")),
	@AttributeOverride(name = "name", column = @Column(name="ATTRIBUTE_NAME", length = 100, nullable = false))
})
public class AuthAttributeEntity extends AbstractKeyNameEntity {
	
    @Column(name="DESCRIPTION", length = 255, nullable = true)
    private String description;
    
    @Column(name="DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;
    
    @Column(name="REQUIRED")
    @Type(type = "yes_no")
    private boolean isRequired = false;
    
    @Column(name="DEFAULT_VALUE", length = 4096)
    private String defaultValue;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = true, updatable = false)
    private AuthProviderTypeEntity type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public AuthProviderTypeEntity getType() {
        return type;
    }

    public void setType(AuthProviderTypeEntity type) {
        this.type = type;
    }

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isRequired ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AuthAttributeEntity other = (AuthAttributeEntity) obj;
		if (dataType != other.dataType)
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isRequired != other.isRequired)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
    
    
}
