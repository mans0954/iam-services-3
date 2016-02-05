package org.openiam.idm.srvc.mngsys.bean;


import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapDataTypeOptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 *  A flyweight version of AttributeMap
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeMapBean", propOrder = {
        "id",
        "operation",
        "objectType",
        "attributeName",
        "policyType",
        "attributePolicyId",
        "defaultAttributePolicyId",
        "dataType",
        "defaultValue",
        "status",
})
public class AttributeMapBean {

    private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
    private String id;
    private String objectType;
    private String attributeName;
    private String policyType;
    private String attributePolicyId;
    private String defaultAttributePolicyId;
    private PolicyMapDataTypeOptions dataType;
    private String defaultValue;
    private String status;

    public AttributeMapBean() {}

    public AttributeMapBean(AttributeMap attributeMap) {
        this.id = attributeMap.getId();
        this.objectType = attributeMap.getMapForObjectType();
        this.attributeName = attributeMap.getName();
        if (attributeMap.getReconResAttribute() != null) {
            if (attributeMap.getReconResAttribute().getAttributePolicy() != null) {
                this.policyType = "POLICY";
                this.attributePolicyId = attributeMap.getReconResAttribute().getAttributePolicy().getId();
                this.defaultAttributePolicyId = null;
            } else if (attributeMap.getReconResAttribute().getDefaultAttributePolicy() != null) {
                this.policyType = "DEFAULT_IDM";
                this.defaultAttributePolicyId = attributeMap.getReconResAttribute().getDefaultAttributePolicy().getId();
                this.attributePolicyId = null;
            }
        }
        this.dataType = attributeMap.getDataType();
        this.defaultValue = attributeMap.getDefaultValue();
        this.status = attributeMap.getStatus();
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getAttributePolicyId() {
        return attributePolicyId;
    }

    public void setAttributePolicyId(String attributePolicyId) {
        this.attributePolicyId = attributePolicyId;
    }

    public String getDefaultAttributePolicyId() {
        return defaultAttributePolicyId;
    }

    public void setDefaultAttributePolicyId(String defaultAttributePolicyId) {
        this.defaultAttributePolicyId = defaultAttributePolicyId;
    }

    public PolicyMapDataTypeOptions getDataType() {
        return dataType;
    }

    public void setDataType(PolicyMapDataTypeOptions dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
