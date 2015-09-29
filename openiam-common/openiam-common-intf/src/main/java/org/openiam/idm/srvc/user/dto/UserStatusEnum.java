package org.openiam.idm.srvc.user.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;


/**
 * Status' that a user can be in.
 *
 * @author suneet
 */
@XmlType(name = "UserStatus")
@XmlEnum
public enum UserStatusEnum {

    @XmlEnumValue("active")
    ACTIVE("ACTIVE"),
    @XmlEnumValue("inactive")
    INACTIVE("INACTIVE"),
    @XmlEnumValue("pending_start_date")
    PENDING_START_DATE("PENDING_START_DATE"),
    @XmlEnumValue("pending_approval")
    PENDING_APPROVAL("PENDING_APPROVAL"),
    @XmlEnumValue("approval_declined")
    APPROVAL_DECLINED("APPROVAL_DECLINED"),
    @XmlEnumValue("pending_user_validation")
    PENDING_USER_VALIDATION("PENDING_USER_VALIDATION"),
    @XmlEnumValue("pending_initial_login")
    PENDING_INITIAL_LOGIN("PENDING_INITIAL_LOGIN"),
    @XmlEnumValue("terminated")
    TERMINATED("TERMINATED"),
    @XmlEnumValue("deleted")
    DELETED("DELETED"),
    @XmlEnumValue("remove")
    REMOVE("REMOVE"),
    @XmlEnumValue("locked")
    LOCKED("LOCKED"),
    @XmlEnumValue("locked_admin")
    LOCKED_ADMIN("LOCKED_ADMIN"),
    @XmlEnumValue("disabled")
    DISABLED("DISABLED"),
    @XmlEnumValue("retired")
    RETIRED("RETIRED"),
    @XmlEnumValue("leave")
    LEAVE("LEAVE"),
    @XmlEnumValue("pending_delete")
    PENDING_DELETE("PENDING_DELETE"),
    @XmlEnumValue("pending_deactivation")
    PENDING_DEACTIVATION("PENDING_DEACTIVATION");
    private String value;

    UserStatusEnum(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    public static UserStatusEnum getFromString(final String val) {
        UserStatusEnum retVal = null;
        for (final UserStatusEnum e : UserStatusEnum.values()) {
            if (StringUtils.equals(val, e.getValue())) {
                retVal = e;
                break;
            }
        }
        return retVal;
    }
}
