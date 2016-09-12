package org.openiam.am.srvc.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "AUTH_PROVIDER")
@DozerDTOCorrespondence(AuthProvider.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthProviderEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "PROVIDER_ID", length = 32, nullable = false)
    private String providerId;
    @Column(name = "PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;
    @Column(name = "MANAGED_SYS_ID", length = 32, nullable = false)
    private String managedSysId;
    
    /*
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    */
    
    @Column(name="IS_CHAINED")
    @Type(type = "yes_no")
    private boolean chained=false;
    
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;
    @Column(name="SIGN_REQUEST")
    @Type(type = "yes_no")
    private boolean isSignRequest=false;
    @Column(name = "PUBLIC_KEY", nullable = true)
    @Lob
    private byte[] publicKey=null;
    @Column(name = "PRIVATE_KEY", nullable = true)
    @Lob
    private byte[] privateKey=null;

    @Column(name="SUPPORTS_CERT_AUTH")
    @Type(type = "yes_no")
    private boolean supportsCertAuth;

    @Column(name="CERT_AUTH_REGEX",length=19)
    private String certRegex;

    @Column(name="CERT_AUTH_REGEX_SCRIPT",length=19)
    private String certGroovyScript;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private AuthProviderTypeEntity type;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ManagedSysEntity managedSys;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="NEXT_PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private AuthProviderEntity nextAuthProvider;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ResourceEntity resource;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "provider")
    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<AuthProviderAttributeEntity> providerAttributeSet;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "provider")
    @MapKey(name = "targetAttributeName")
    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<String, AuthResourceAttributeMapEntity> resourceAttributeMap=new HashMap<String, AuthResourceAttributeMapEntity>(0);

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    /*
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public ManagedSysEntity getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(ManagedSysEntity managedSys) {
        this.managedSys = managedSys;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }

    public Set<AuthProviderAttributeEntity> getProviderAttributeSet() {
        return providerAttributeSet;
    }

    public void setProviderAttributeSet(Set<AuthProviderAttributeEntity> providerAttributeSet) {
        this.providerAttributeSet = providerAttributeSet;
    }

    public Map<String, AuthResourceAttributeMapEntity> getResourceAttributeMap() {
        return resourceAttributeMap;
    }

    public void setResourceAttributeMap(Map<String, AuthResourceAttributeMapEntity> resourceAttributeMap) {
        this.resourceAttributeMap = resourceAttributeMap;
    }

	public boolean isChained() {
		return chained;
	}

	public void setChained(boolean chained) {
		this.chained = chained;
	}

	public AuthProviderEntity getNextAuthProvider() {
		return nextAuthProvider;
	}

	public void setNextAuthProvider(AuthProviderEntity nextAuthProvider) {
		this.nextAuthProvider = nextAuthProvider;
	}

    public boolean isSupportsCertAuth() {
        return supportsCertAuth;
    }

    public void setSupportsCertAuth(boolean supportsCertAuth) {
        this.supportsCertAuth = supportsCertAuth;
    }

    public String getCertRegex() {
        return certRegex;
    }

    public void setCertRegex(String certRegex) {
        this.certRegex = certRegex;
    }

    public String getCertGroovyScript() {
        return certGroovyScript;
    }

    public void setCertGroovyScript(String certGroovyScript) {
        this.certGroovyScript = certGroovyScript;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (chained ? 1231 : 1237);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isSignRequest ? 1231 : 1237);
		result = prime * result
				+ ((managedSys == null) ? 0 : managedSys.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((nextAuthProvider == null) ? 0 : nextAuthProvider.hashCode());
		result = prime * result + Arrays.hashCode(privateKey);
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		result = prime * result
				+ ((providerType == null) ? 0 : providerType.hashCode());
		result = prime * result + Arrays.hashCode(publicKey);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AuthProviderEntity other = (AuthProviderEntity) obj;
		if (chained != other.chained)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isSignRequest != other.isSignRequest)
			return false;
		if (managedSys == null) {
			if (other.managedSys != null)
				return false;
		} else if (!managedSys.equals(other.managedSys))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nextAuthProvider == null) {
			if (other.nextAuthProvider != null)
				return false;
		} else if (!nextAuthProvider.equals(other.nextAuthProvider))
			return false;
		if (!Arrays.equals(privateKey, other.privateKey))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		if (providerType == null) {
			if (other.providerType != null)
				return false;
		} else if (!providerType.equals(other.providerType))
			return false;
		if (!Arrays.equals(publicKey, other.publicKey))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
    
    
}
