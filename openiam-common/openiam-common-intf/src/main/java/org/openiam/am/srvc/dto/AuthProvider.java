package org.openiam.am.srvc.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.Resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProvider", propOrder = {
        "providerType",
        "managedSysId",
        "resourceId",
        "description",
        "isSignRequest",
        "publicKey",
        "privateKey",
        "attributes",
        "resource",
        "resourceAttributeMap",
        "policyId",
        "defaultProvider",
        "springBeanName",
        "groovyScriptURL"
})
@DozerDTOCorrespondence(AuthProviderEntity.class)
public class AuthProvider extends KeyNameDTO {
    private String providerType;
    private String managedSysId;
    private String resourceId;
    private String description;
    private boolean isSignRequest=false;
    private byte[] publicKey;
    private byte[] privateKey;
    private String policyId;
    private boolean defaultProvider;
    private String groovyScriptURL;
    private String springBeanName;

    private Set<AuthProviderAttribute> attributes;
    private Map<String, AuthResourceAttributeMap> resourceAttributeMap=new HashMap<String, AuthResourceAttributeMap>(0);
    private Resource resource;
    @XmlTransient
    private Map<String, AuthProviderAttribute> attributeMap=null;
    
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public Set<AuthProviderAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AuthProviderAttribute> attributes) {
        this.attributes = attributes;
        this.generateAttributeMap();
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Map<String, AuthResourceAttributeMap> getResourceAttributeMap() {
        return resourceAttributeMap;
    }

    public void setResourceAttributeMap(Map<String, AuthResourceAttributeMap> resourceAttributeMap) {
        this.resourceAttributeMap = resourceAttributeMap;
    }

    /**
    *  Returns provider attributes as Map. Key of the map is attribute name.
     *  if you want to change attribute value, add new or remove attribute, <b>don't use this map. use  setProviderAttributeSet() property to change attribute instead.</b>
    * */
    public Map<String, AuthProviderAttribute> getAttributeMap() {
        if(CollectionUtils.isNotEmpty(attributes)) {
            if(attributeMap==null){
                generateAttributeMap();
            }
        }
        return attributeMap;
    }

    public void setAttributeMap(Map<String, AuthProviderAttribute> attributeMap) {
        this.attributeMap = attributeMap;
    }

    private  void generateAttributeMap(){
    	attributeMap = new HashMap<String, AuthProviderAttribute>();
        for (AuthProviderAttribute attr: this.attributes){
        	attributeMap.put(attr.getAttributeName(), attr);
        }
    }

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (defaultProvider ? 1231 : 1237);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((groovyScriptURL == null) ? 0 : groovyScriptURL.hashCode());
		result = prime * result + (isSignRequest ? 1231 : 1237);
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((policyId == null) ? 0 : policyId.hashCode());
		result = prime * result + Arrays.hashCode(privateKey);
		result = prime * result
				+ ((providerType == null) ? 0 : providerType.hashCode());
		result = prime * result + Arrays.hashCode(publicKey);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((springBeanName == null) ? 0 : springBeanName.hashCode());
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
		AuthProvider other = (AuthProvider) obj;
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
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (policyId == null) {
			if (other.policyId != null)
				return false;
		} else if (!policyId.equals(other.policyId))
			return false;
		if (!Arrays.equals(privateKey, other.privateKey))
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
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (springBeanName == null) {
			if (other.springBeanName != null)
				return false;
		} else if (!springBeanName.equals(other.springBeanName))
			return false;
		return true;
	}

	
}
