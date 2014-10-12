package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderType", propOrder = {
        "isActive",
        "hasPublicKey",
        "hasPrivateKey",
        "hasPasswordPolicy",
        "usesSpringBean",
        "usesGroovyScript",
        "passwordPolicyRequired",
        "supportsJustInTimeAuthentication"
})
@DozerDTOCorrespondence(AuthProviderTypeEntity.class)
public class AuthProviderType extends KeyNameDTO {
    private boolean isActive = true;
    private boolean hasPublicKey;
    private boolean hasPrivateKey;
    private boolean hasPasswordPolicy;
    private boolean usesSpringBean;
    private boolean usesGroovyScript;
    private boolean passwordPolicyRequired;
    private boolean supportsJustInTimeAuthentication;
    
    @XmlTransient
    private Set<AuthAttributeEntity> attributeSet;
    @XmlTransient
    private Set<AuthProviderEntity> providerSet;

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

	public boolean isHasPasswordPolicy() {
		return hasPasswordPolicy;
	}

	public void setHasPasswordPolicy(boolean hasPasswordPolicy) {
		this.hasPasswordPolicy = hasPasswordPolicy;
	}

	public boolean isUsesSpringBean() {
		return usesSpringBean;
	}

	public void setUsesSpringBean(boolean usesSpringBean) {
		this.usesSpringBean = usesSpringBean;
	}

	public boolean isUsesGroovyScript() {
		return usesGroovyScript;
	}

	public void setUsesGroovyScript(boolean usesGroovyScript) {
		this.usesGroovyScript = usesGroovyScript;
	}

	public boolean isPasswordPolicyRequired() {
		return passwordPolicyRequired;
	}

	public void setPasswordPolicyRequired(boolean passwordPolicyRequired) {
		this.passwordPolicyRequired = passwordPolicyRequired;
	}
	
	public boolean isSupportsJustInTimeAuthentication() {
		return supportsJustInTimeAuthentication;
	}

	public void setSupportsJustInTimeAuthentication(
			boolean supportsJustInTimeAuthentication) {
		this.supportsJustInTimeAuthentication = supportsJustInTimeAuthentication;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (hasPasswordPolicy ? 1231 : 1237);
		result = prime * result + (hasPrivateKey ? 1231 : 1237);
		result = prime * result + (hasPublicKey ? 1231 : 1237);
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (passwordPolicyRequired ? 1231 : 1237);
		result = prime * result + (usesGroovyScript ? 1231 : 1237);
		result = prime * result + (usesSpringBean ? 1231 : 1237);
		result = prime * result + (supportsJustInTimeAuthentication ? 1231 : 1237);
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
		if (hasPasswordPolicy != other.hasPasswordPolicy)
			return false;
		if (hasPrivateKey != other.hasPrivateKey)
			return false;
		if (hasPublicKey != other.hasPublicKey)
			return false;
		if (isActive != other.isActive)
			return false;
		if (passwordPolicyRequired != other.passwordPolicyRequired)
			return false;
		if (usesGroovyScript != other.usesGroovyScript)
			return false;
		if (usesSpringBean != other.usesSpringBean)
			return false;
		if (supportsJustInTimeAuthentication != other.supportsJustInTimeAuthentication)
			return false;
		return true;
	}

	
}
