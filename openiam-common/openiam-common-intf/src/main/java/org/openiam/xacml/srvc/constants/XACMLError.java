package org.openiam.xacml.srvc.constants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/14/15.
 */
@XmlType(name = "XACMLError")
@XmlEnum
public enum XACMLError {

    @XmlEnumValue("CAN_NOT_ADD_POLICY")
    CAN_NOT_ADD_POLICY("CAN_NOT_ADD_POLICY"),

    @XmlEnumValue("CAN_NOT_ADD_TARGET")
    CAN_NOT_ADD_TARGET("CAN_NOT_ADD_TARGET"),

    @XmlEnumValue("CAN_NOT_UPDATE_POLICY")
    CAN_NOT_UPDATE_POLICY("CAN_NOT_UPDATE_POLICY"),

    @XmlEnumValue("CAN_NOT_UPDATE_TARGET")
    CAN_NOT_UPDATE_TARGET("CAN_NOT_UPDATE_TARGET"),


    @XmlEnumValue("CAN_NOT_DELETE_POLICY")
    CAN_NOT_DELETE_POLICY("CAN_NOT_DELETE_POLICY"),

    @XmlEnumValue("CAN_NOT_DELETE_TARGET")
    CAN_NOT_DELETE_TARGET("CAN_NOT_DELETE_TARGET"),

    @XmlEnumValue("SYSTEM_ERROR")
    SYSTEM_ERROR("SYSTEM_ERROR");

    private final String value;

    XACMLError(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}
