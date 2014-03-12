package org.openiam.idm.srvc.auth.dto;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "IdentityType")
@XmlEnum
public enum IdentityTypeEnum {
    @XmlEnumValue("GROUP")
    GROUP("GROUP"),
    @XmlEnumValue("ROLE")
    ROLE("ROLE"),
    @XmlEnumValue("ORG")
    ORG("ORG");

    private String value;

    IdentityTypeEnum(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    public static IdentityTypeEnum getFromString(final String val) {
        IdentityTypeEnum retVal = null;
        for(final IdentityTypeEnum e : IdentityTypeEnum.values()) {
            if(StringUtils.equals(val, e.getValue())) {
                retVal = e;
                break;
            }
        }
        return retVal;
    }
}
