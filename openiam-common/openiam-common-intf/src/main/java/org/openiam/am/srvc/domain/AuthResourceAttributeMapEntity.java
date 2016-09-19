package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.constants.SsoAttributeType;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "AUTH_RESOURCE_ATTRIBUTE_MAP", uniqueConstraints = {
        @UniqueConstraint(columnNames={"PROVIDER_ID","TARGET_ATTRIBUTE_NAME"})
})
@DozerDTOCorrespondence(AuthResourceAttributeMap.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "ATTRIBUTE_MAP_ID")),
	@AttributeOverride(name = "name", column = @Column(name="TARGET_ATTRIBUTE_NAME", length=100, nullable = false))
})
public class AuthResourceAttributeMapEntity extends AbstractKeyNameEntity {
    
    @Column(name="AM_POLICY_URL", length=100, nullable = true)
    private String amPolicyUrl;

    @Column(name="ATTRIBUTE_VALUE", length=100, nullable = true)
    private String  attributeValue;

    @Enumerated(EnumType.STRING)
    @Column(name="ATTRIBUTE_TYPE", length=32, nullable = false)
    private SsoAttributeType attributeType;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)    
	private AuthProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="AM_RES_ATTRIBUTE_ID", referencedColumnName = "AM_RES_ATTRIBUTE_ID", insertable = true, updatable = true, nullable=true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)    
	private AuthResourceAMAttributeEntity amAttribute;

    public AuthProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(AuthProviderEntity provider) {
        this.provider = provider;
    }
    
    public String getAmPolicyUrl() {
        return amPolicyUrl;
    }

    public void setAmPolicyUrl(String amPolicyUrl) {
        this.amPolicyUrl = amPolicyUrl;
    }

    public AuthResourceAMAttributeEntity getAmAttribute() {
        return amAttribute;
    }

    public void setAmAttribute(AuthResourceAMAttributeEntity amAttribute) {
        this.amAttribute = amAttribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public SsoAttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(SsoAttributeType attributeType) {
        this.attributeType = attributeType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result
				+ ((amPolicyUrl == null) ? 0 : amPolicyUrl.hashCode());
		result = prime * result
				+ ((attributeType == null) ? 0 : attributeType.hashCode());
		result = prime * result
				+ ((attributeValue == null) ? 0 : attributeValue.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
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
		AuthResourceAttributeMapEntity other = (AuthResourceAttributeMapEntity) obj;
		if (amAttribute == null) {
			if (other.amAttribute != null)
				return false;
		} else if (!amAttribute.equals(other.amAttribute))
			return false;
		if (amPolicyUrl == null) {
			if (other.amPolicyUrl != null)
				return false;
		} else if (!amPolicyUrl.equals(other.amPolicyUrl))
			return false;
		if (attributeType != other.attributeType)
			return false;
		if (attributeValue == null) {
			if (other.attributeValue != null)
				return false;
		} else if (!attributeValue.equals(other.attributeValue))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}
    
    
}
