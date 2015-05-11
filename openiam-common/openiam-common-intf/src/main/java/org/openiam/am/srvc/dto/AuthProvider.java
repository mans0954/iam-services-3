package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthProviderEntity;
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
        "providerId",
        "providerType",
        "managedSysId",
        "resourceId",
        "name",
        "description",
        "isSignRequest",
        "publicKey",
        "privateKey",
        "providerAttributeSet",
        "resource",
        "resourceAttributeMap"
})
@DozerDTOCorrespondence(AuthProviderEntity.class)
public class AuthProvider implements Serializable {
    private String providerId;
    private String providerType;
    private String managedSysId;
    private String resourceId;
    private String name;
    private String description;
    private boolean isSignRequest=false;
    private byte[] publicKey;
    private byte[] privateKey;

    private Set<AuthProviderAttribute> providerAttributeSet;
    private Map<String, AuthResourceAttributeMap> resourceAttributeMap=new HashMap<String, AuthResourceAttributeMap>(0);
    private Resource resource;
    @XmlTransient
    private Map<String, AuthProviderAttribute> providerAttributeMap=null;

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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

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

    public Set<AuthProviderAttribute> getProviderAttributeSet() {
        return providerAttributeSet;
    }

    public void setProviderAttributeSet(Set<AuthProviderAttribute> providerAttributeSet) {
        this.providerAttributeSet = providerAttributeSet;
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
    public Map<String, AuthProviderAttribute> getProviderAttributeMap() {
        if(this.providerAttributeSet!=null && !this.providerAttributeSet.isEmpty()){
            if(providerAttributeMap==null){
                generateAttributeMap();
            }
        }
        return providerAttributeMap;
    }

    public void setProviderAttributeMap(Map<String, AuthProviderAttribute> providerAttributeMap) {
        this.providerAttributeMap = providerAttributeMap;
    }

    private  void generateAttributeMap(){
        providerAttributeMap = new HashMap<String, AuthProviderAttribute>();
        for (AuthProviderAttribute attr: this.providerAttributeSet){
            providerAttributeMap.put(attr.getAttributeName(), attr);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthProvider that = (AuthProvider) o;

        if (isSignRequest != that.isSignRequest) return false;
        if (providerId != null ? !providerId.equals(that.providerId) : that.providerId != null) return false;
        if (providerType != null ? !providerType.equals(that.providerType) : that.providerType != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!Arrays.equals(publicKey, that.publicKey)) return false;
        if (!Arrays.equals(privateKey, that.privateKey)) return false;
        if (providerAttributeSet != null ? !providerAttributeSet.equals(that.providerAttributeSet) : that.providerAttributeSet != null)
            return false;
        if (resourceAttributeMap != null ? !resourceAttributeMap.equals(that.resourceAttributeMap) : that.resourceAttributeMap != null)
            return false;
        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;
        return !(providerAttributeMap != null ? !providerAttributeMap.equals(that.providerAttributeMap) : that.providerAttributeMap != null);

    }

    @Override
    public int hashCode() {
        int result = providerId != null ? providerId.hashCode() : 0;
        result = 31 * result + (providerType != null ? providerType.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (isSignRequest ? 1 : 0);
        result = 31 * result + (publicKey != null ? Arrays.hashCode(publicKey) : 0);
        result = 31 * result + (privateKey != null ? Arrays.hashCode(privateKey) : 0);
        result = 31 * result + (providerAttributeSet != null ? providerAttributeSet.hashCode() : 0);
        result = 31 * result + (resourceAttributeMap != null ? resourceAttributeMap.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (providerAttributeMap != null ? providerAttributeMap.hashCode() : 0);
        return result;
    }
}
