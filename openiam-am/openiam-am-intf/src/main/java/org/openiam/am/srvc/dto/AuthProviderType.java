package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

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
}
