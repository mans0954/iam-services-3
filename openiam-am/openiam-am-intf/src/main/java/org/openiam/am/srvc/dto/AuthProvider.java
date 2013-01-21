package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.Resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
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
    private String publicKey;
    private String privateKey;

    private Set<AuthProviderAttribute> providerAttributeSet;
    private Resource resource;

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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Set<AuthProviderAttribute> getProviderAttributeSet() {
        return providerAttributeSet;
    }

    public void setProviderAttributeSet(Set<AuthProviderAttribute> providerAttributeSet) {
        this.providerAttributeSet = providerAttributeSet;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
