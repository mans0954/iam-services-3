package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.Resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
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
        "resource"
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
}
