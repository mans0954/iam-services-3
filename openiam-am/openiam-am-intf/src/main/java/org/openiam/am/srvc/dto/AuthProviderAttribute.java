package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthProviderAttribute", propOrder = {
        "providerAttributeId",
        "providerId",
        "attributeId",
        "attributeName",
        "value",
        "dataType"
})
@DozerDTOCorrespondence(AuthProviderAttributeEntity.class)
public class AuthProviderAttribute implements Serializable {
    private String providerAttributeId;
    private String providerId;
    private String attributeId;
    private String attributeName;
    private String value;
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;

    public String getProviderAttributeId() {
        return providerAttributeId;
    }

    public void setProviderAttributeId(String providerAttributeId) {
        this.providerAttributeId = providerAttributeId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }
}
