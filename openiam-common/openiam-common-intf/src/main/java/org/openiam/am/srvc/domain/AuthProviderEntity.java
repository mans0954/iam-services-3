package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "AUTH_PROVIDER")
@DozerDTOCorrespondence(AuthProvider.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "PROVIDER_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 50, nullable = false))
})
public class AuthProviderEntity extends AbstractKeyNameEntity {

    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;
    
    @Column(name = "SPRING_BEAN_NAME", length = 100, nullable = true)
    private String springBeanName;
    
    @Column(name = "GROOVY_SCRIPT_URL", length = 100, nullable = true)
    private String groovyScriptURL;
    
    @Column(name="SUPPORTS_JUST_IN_TIME_AUTH")
    @Type(type = "yes_no")
    private boolean supportsJustInTimeAuthentication;
    
    @Column(name="SIGN_REQUEST")
    @Type(type = "yes_no")
    private boolean isSignRequest=false;
    
    @Column(name="IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean defaultProvider;
    
    @Column(name="SUPPORTS_SMS_OTP")
    @Type(type = "yes_no")
    private boolean supportsSMSOTP;
    
    @Column(name="SMS_OTP_GROOVY_SCRIPT", length=150)
    private String smsOTPGroovyScript;
    
    @Column(name="SUPPORTS_TOTP")
    @Type(type = "yes_no")
    private boolean supportsTOTP;
    
    @Column(name = "PUBLIC_KEY", nullable = true)
    @Lob
    private byte[] publicKey=null;
    
    @Column(name = "PRIVATE_KEY", nullable = true)
    @Lob
    private byte[] privateKey=null;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = true, updatable = false)
    private AuthProviderTypeEntity type;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=false)
    private ManagedSysEntity managedSystem;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
    private ResourceEntity resource;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "provider", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<AuthProviderAttributeEntity> attributes;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "authProvider")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ContentProviderEntity> contentProviders;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "authProvider")
    @Fetch(FetchMode.SUBSELECT)
    private Set<URIPatternEntity> uriPatterns;

	@OneToMany(fetch = FetchType.LAZY,cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "client")
	@Fetch(FetchMode.SUBSELECT)
	private Set<OAuthTokenEntity> oAuthTokens;

	@OneToMany(fetch = FetchType.LAZY,cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "client")
	@Fetch(FetchMode.SUBSELECT)
	private Set<OAuthCodeEntity> oAuthCodes;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="client", orphanRemoval=true)
	@Fetch(FetchMode.SUBSELECT)
	private Set<OAuthUserClientXrefEntity> authorizedUsers = new HashSet<OAuthUserClientXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "provider")
    @MapKey(name = "name")
    private Map<String, AuthResourceAttributeMapEntity> resourceAttributeMap=new HashMap<String, AuthResourceAttributeMapEntity>(0);
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = true, nullable=true)
    private PolicyEntity passwordPolicy;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AUTH_POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = true, nullable=true)
    private PolicyEntity authenticationPolicy;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSignRequest() {
        return isSignRequest;
    }

    public void setSignRequest(boolean signRequest) {
        isSignRequest = signRequest;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public AuthProviderTypeEntity getType() {
        return type;
    }

    public void setType(AuthProviderTypeEntity type) {
        this.type = type;
    }

    public ManagedSysEntity getManagedSystem() {
		return managedSystem;
	}

	public void setManagedSystem(ManagedSysEntity managedSystem) {
		this.managedSystem = managedSystem;
	}

	public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }

    public Set<AuthProviderAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AuthProviderAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public Map<String, AuthResourceAttributeMapEntity> getResourceAttributeMap() {
        return resourceAttributeMap;
    }

    public void setResourceAttributeMap(Map<String, AuthResourceAttributeMapEntity> resourceAttributeMap) {
        this.resourceAttributeMap = resourceAttributeMap;
    }

	public PolicyEntity getPasswordPolicy() {
		return passwordPolicy;
	}

	public void setPasswordPolicy(PolicyEntity passwordPolicy) {
		this.passwordPolicy = passwordPolicy;
	}
	
	

	public boolean isDefaultProvider() {
		return defaultProvider;
	}

	public void setDefaultProvider(boolean defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

	public String getGroovyScriptURL() {
		return groovyScriptURL;
	}

	public void setGroovyScriptURL(String groovyScriptURL) {
		this.groovyScriptURL = groovyScriptURL;
	}

	public String getSpringBeanName() {
		return springBeanName;
	}

	public void setSpringBeanName(String springBeanName) {
		this.springBeanName = springBeanName;
	}

	public Set<ContentProviderEntity> getContentProviders() {
		return contentProviders;
	}

	public void setContentProviders(Set<ContentProviderEntity> contentProviders) {
		this.contentProviders = contentProviders;
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

	public String getSmsOTPGroovyScript() {
		return smsOTPGroovyScript;
	}

	public void setSmsOTPGroovyScript(String smsOTPGroovyScript) {
		this.smsOTPGroovyScript = smsOTPGroovyScript;
	}

	public Set<URIPatternEntity> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(Set<URIPatternEntity> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}
	
	public boolean isSupportsTOTP() {
		return supportsTOTP;
	}

	public void setSupportsTOTP(boolean supportsTOTP) {
		this.supportsTOTP = supportsTOTP;
	}

	public PolicyEntity getAuthenticationPolicy() {
		return authenticationPolicy;
	}

	public void setAuthenticationPolicy(PolicyEntity authenticationPolicy) {
		this.authenticationPolicy = authenticationPolicy;
	}

	public Set<OAuthTokenEntity> getoAuthTokens() {
		return oAuthTokens;
	}

	public void setoAuthTokens(Set<OAuthTokenEntity> oAuthTokens) {
		this.oAuthTokens = oAuthTokens;
	}

	public Set<OAuthUserClientXrefEntity> getAuthorizedUsers() {
		return authorizedUsers;
	}

	public void setAuthorizedUsers(Set<OAuthUserClientXrefEntity> authorizedUsers) {
		this.authorizedUsers = authorizedUsers;
	}

	public Set<OAuthCodeEntity> getoAuthCodes() {
		return oAuthCodes;
	}

	public void setoAuthCodes(Set<OAuthCodeEntity> oAuthCodes) {
		this.oAuthCodes = oAuthCodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (supportsJustInTimeAuthentication ? 1231 : 1237);
		result = prime * result + (defaultProvider ? 1231 : 1237);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((groovyScriptURL == null) ? 0 : groovyScriptURL.hashCode());
		result = prime * result + (isSignRequest ? 1231 : 1237);
		result = prime * result
				+ ((managedSystem == null) ? 0 : managedSystem.hashCode());
		result = prime * result + ((passwordPolicy == null) ? 0 : passwordPolicy.hashCode());
		result = prime * result + ((authenticationPolicy == null) ? 0 : authenticationPolicy.hashCode());
		result = prime * result + Arrays.hashCode(privateKey);
		result = prime * result + Arrays.hashCode(publicKey);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((springBeanName == null) ? 0 : springBeanName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (supportsSMSOTP ? 1231 : 1237);
		result = prime * result + (supportsTOTP ? 1231 : 1237);
		result = prime * result + ((smsOTPGroovyScript == null) ? 0 : smsOTPGroovyScript.hashCode());
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
		AuthProviderEntity other = (AuthProviderEntity) obj;
		if (defaultProvider != other.defaultProvider)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (groovyScriptURL == null) {
			if (other.groovyScriptURL != null)
				return false;
		} else if (!groovyScriptURL.equals(other.groovyScriptURL))
			return false;
		if (isSignRequest != other.isSignRequest)
			return false;
		if (managedSystem == null) {
			if (other.managedSystem != null)
				return false;
		} else if (!managedSystem.equals(other.managedSystem))
			return false;
		
		if (passwordPolicy == null) {
			if (other.passwordPolicy != null)
				return false;
		} else if (!passwordPolicy.equals(other.passwordPolicy))
			return false;
		
		if (authenticationPolicy == null) {
			if (other.authenticationPolicy != null)
				return false;
		} else if (!authenticationPolicy.equals(other.authenticationPolicy))
			return false;
		
		if (!Arrays.equals(privateKey, other.privateKey))
			return false;
		if (!Arrays.equals(publicKey, other.publicKey))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (springBeanName == null) {
			if (other.springBeanName != null)
				return false;
		} else if (!springBeanName.equals(other.springBeanName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (supportsJustInTimeAuthentication != other.supportsJustInTimeAuthentication)
			return false;
		if (supportsSMSOTP != other.supportsSMSOTP)
			return false;
		if (supportsTOTP != other.supportsTOTP)
			return false;
		if (smsOTPGroovyScript == null) {
			if (other.smsOTPGroovyScript != null)
				return false;
		} else if (!smsOTPGroovyScript.equals(other.smsOTPGroovyScript))
			return false;
		return true;
	}
	
	
}
