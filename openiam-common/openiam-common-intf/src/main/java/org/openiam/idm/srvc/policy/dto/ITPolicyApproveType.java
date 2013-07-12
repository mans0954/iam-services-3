package org.openiam.idm.srvc.policy.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ITPolicyApproveType")
@XmlEnum(String.class)
public enum ITPolicyApproveType {
    @XmlEnumValue("Once")
    ONCE("Once"),
    @XmlEnumValue("Annually")
    ANNUALLY("Annually");

    private String value;

    private ITPolicyApproveType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ITPolicyApproveType fromString(final String val) {
        for(final ITPolicyApproveType e : ITPolicyApproveType.values()) {
            if(e.getValue().equals(val)) {
                return e;
            }
        }
        return null;
    }
}
