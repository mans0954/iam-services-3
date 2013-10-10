package org.openiam.idm.srvc.auth.dto;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "LoginStatus")
@XmlEnum
public enum LoginStatusEnum {
    @XmlEnumValue("pending_initial_login")
    PENDING_INITIAL_LOGIN("PENDING_INITIAL_LOGIN"),
    @XmlEnumValue("Active")
    ACTIVE("ACTIVE"),
    @XmlEnumValue("Inactive")
    INACTIVE("INACTIVE"),
    @XmlEnumValue("Pending create")
    PENDING_CREATE("PENDING_CREATE"),
    @XmlEnumValue("Pending update")
    PENDING_UPDATE("PENDING_UPDATE"),
    @XmlEnumValue("Fail create")
    FAIL_CREATE("FAIL_CREATE"),
    @XmlEnumValue("Fail update")
    FAIL_UPDATE("FAIL_UPDATE");

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
