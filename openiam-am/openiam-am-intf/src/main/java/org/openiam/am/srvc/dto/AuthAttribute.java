package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthAttribute", propOrder = {
        "authAttributeId",
        "attributeName",
        "providerType",
        "description",
        "dataType",
        "isRequired",
        "defaultValue"
})
@DozerDTOCorrespondence(AuthAttributeEntity.class)
public class AuthAttribute implements Serializable {
    private String authAttributeId;
    private String attributeName;
    private String providerType;
    private String description;
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;
    private boolean isRequired = false;
    private String defaultValue;

    public String getAuthAttributeId() {
        return authAttributeId;
    }

    public void setAuthAttributeId(String authAttributeId) {
        this.authAttributeId = authAttributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

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

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
