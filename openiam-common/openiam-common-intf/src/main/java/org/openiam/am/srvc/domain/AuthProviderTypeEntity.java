package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.base.domain.AbstractKeyNameEntity;
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
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "PROVIDER_TYPE")),
	@AttributeOverride(name = "name", column = @Column(name="NAME", length = 50, nullable = true))
})
public class AuthProviderTypeEntity extends AbstractKeyNameEntity {
    
    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;
    
    @Column(name="HAS_PUBLIC_KEY")
    @Type(type = "yes_no")
    private boolean hasPublicKey;
    
    @Column(name="IS_PASSWORD_POLICY_REQUIRED")
    @Type(type = "yes_no")
    private boolean passwordPolicyRequired;
    
    @Column(name="HAS_PRIVATE_KEY")
    @Type(type = "yes_no")
    private boolean hasPrivateKey;
    
    @Column(name="HAS_PASSWORD_POLICY")
    @Type(type = "yes_no")
    private boolean hasPasswordPolicy;
    
    @Column(name="SUPPORTS_JUST_IN_TIME_AUTH")
    @Type(type = "yes_no")
    private boolean supportsJustInTimeAuthentication;
    
    @Column(name="HAS_SPRING_BEAN")
    @Type(type = "yes_no")
    private boolean usesSpringBean;
    
    @Column(name="HAS_GROOVY_SCRIPT")
    @Type(type = "yes_no")
    private boolean usesGroovyScript;
    
    @Column(name="SUPPORTS_SMS_OTP")
    @Type(type = "yes_no")
    private boolean supportsSMSOTP;
    
    @Column(name="SUPPORTS_TOTP")
    @Type(type = "yes_no")
    private boolean supportsTOTP;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<AuthAttributeEntity> attributeSet;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
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
	
	public boolean isSupportsSMSOTP() {
		return supportsSMSOTP;
	}

	public void setSupportsSMSOTP(boolean supportsSMSOTP) {
		this.supportsSMSOTP = supportsSMSOTP;
	}

	public boolean isSupportsTOTP() {
		return supportsTOTP;
	}

	public void setSupportsTOTP(boolean supportsTOTP) {
		this.supportsTOTP = supportsTOTP;
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
		result = prime * result + (supportsSMSOTP ? 1231 : 1237);
		result = prime * result + (supportsTOTP ? 1231 : 1237);
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
		if (supportsSMSOTP != other.supportsSMSOTP)
			return false;
		if (supportsTOTP != other.supportsTOTP)
			return false;
		return true;
	}
	
	
}
