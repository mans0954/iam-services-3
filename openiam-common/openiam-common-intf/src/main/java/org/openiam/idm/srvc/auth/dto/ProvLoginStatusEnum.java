package org.openiam.idm.srvc.auth.dto;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvLoginStatus")
@XmlEnum
public enum ProvLoginStatusEnum {

    @XmlEnumValue("CREATED")
    CREATED("CREATED"),
    @XmlEnumValue("CREATE FAILED")
    FAIL_CREATE("FAIL_CREATE"),
    @XmlEnumValue("PENDING CREATE")
    PENDING_CREATE("PENDING_CREATE"),

    @XmlEnumValue("UPDATED")
    UPDATED("UPDATED"),
    @XmlEnumValue("UPDATE FAILED")
    FAIL_UPDATE("FAIL_UPDATE"),
    @XmlEnumValue("PENDING UPDATE")
    PENDING_UPDATE("PENDING_UPDATE"),

    @XmlEnumValue("DISABLED")
    DISABLED("DISABLED"),
    @XmlEnumValue("DISABLE FAILED")
    FAIL_DISABLE("FAIL_DISABLE"),
    @XmlEnumValue("PENDING DISABLE")
    PENDING_DISABLE("PENDING_DISABLE"),

    @XmlEnumValue("ENABLED")
    ENABLED("ENABLED"),
    @XmlEnumValue("ENABLE FAILED")
    FAIL_ENABLE("FAIL_ENABLE"),
    @XmlEnumValue("PENDING ENABLE")
    PENDING_ENABLE("PENDING_ENABLE"),

    @XmlEnumValue("DELETED")
    DELETED("DELETED"),
    @XmlEnumValue("DELETE FAILED")
    FAIL_DELETE("FAIL_DELETE"),
    @XmlEnumValue("PENDING DELETE")
    PENDING_DELETE("PENDING_DELETE");

    private String value;

    ProvLoginStatusEnum(String val) {
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
