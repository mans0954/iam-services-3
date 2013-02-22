package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderType", propOrder = {
        "providerType",
        "description",
        "isActive"
})
@DozerDTOCorrespondence(AuthProviderTypeEntity.class)
public class AuthProviderType implements Serializable{
    private String providerType;
    private String description;
    private boolean isActive = true;
    @XmlTransient
    private Set<AuthAttributeEntity> attributeSet;
    @XmlTransient
    private Set<AuthProviderEntity> providerSet;


    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
}
