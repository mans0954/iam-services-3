package org.openiam.idm.srvc.auth.dto;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "LoginStatus")
@XmlEnum
public enum LoginStatusEnum {
    @XmlEnumValue("Active")
    ACTIVE("ACTIVE"),
    @XmlEnumValue("Inactive")
    INACTIVE("INACTIVE");

    private String value;

    LoginStatusEnum(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    public static LoginStatusEnum getFromString(final String val) {
        LoginStatusEnum retVal = null;
        for(final LoginStatusEnum e : LoginStatusEnum.values()) {
            if(StringUtils.equals(val, e.getValue())) {
                retVal = e;
                break;
            }
        }
        return retVal;
    }
}
