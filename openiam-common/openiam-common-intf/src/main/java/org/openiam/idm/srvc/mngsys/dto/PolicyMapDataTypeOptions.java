package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PolicyMapDataTypeOptions")
@XmlEnum
public enum PolicyMapDataTypeOptions {
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("long")
    LONG("long"),
    @XmlEnumValue("timestamp")
    TIMESTAMP("timestamp"),
    @XmlEnumValue("memberOf")
    MEMBER_OF("memberOf"),
    @XmlEnumValue("directReports")
    DIRECT_REPORTS("directReports"),
    @XmlEnumValue("byteArray")
    BYTE_ARRAY("byteArray");

    private String value;

    public String getValue() {
        return value;
    }

    private PolicyMapDataTypeOptions(String value) {
        this.value = value;
    }

    public static PolicyMapDataTypeOptions fromString(final String val) {
        for(final PolicyMapDataTypeOptions e : PolicyMapDataTypeOptions.values()) {
            if(e.getValue().equals(val)) {
                return e;
            }
        }
        return null;
    }
}
