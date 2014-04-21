package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BulkOperationObjectType")
@XmlEnum
public enum BulkOperationObjectType {

    @XmlEnumValue("GROUP")
    USER("USER", "User"),

    @XmlEnumValue("GROUP")
    GROUP("GROUP", "Group"),

    @XmlEnumValue("ROLE")
    ROLE("ROLE", "Role"),

    @XmlEnumValue("RESOURCE")
    RESOURCE("RESOURCE", "Resource");

    private String value;
    private String label;

    BulkOperationObjectType(String value, String label) {
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
