package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BulkOperationEnum")
@XmlEnum
public enum BulkOperationEnum {
    @XmlEnumValue("ACTIVATE_USER")
    ACTIVATE_USER("ACTIVATE_USER", "Activate"),
    @XmlEnumValue("DEACTIVATE_USER")
    DEACTIVATE_USER("DEACTIVATE_USER", "DeActivate"),
    @XmlEnumValue("DELETE_USER")
    DELETE_USER("DELETE_USER", "Delete"),
    @XmlEnumValue("ENABLE_USER")
    ENABLE_USER("ENABLE_USER", "Enable"),
    @XmlEnumValue("DISABLE_USER")
    DISABLE_USER("DISABLE_USER", "Disable"),
    @XmlEnumValue("ADD_ENTITLEMENT")
    ADD_ENTITLEMENT("ADD_ENTITLEMENT", "Add"),
    @XmlEnumValue("DELETE_ENTITLEMENT")
    DELETE_ENTITLEMENT("DELETE_ENTITLEMENT", "Delete");

    private String value;
    private String label;

    BulkOperationEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
