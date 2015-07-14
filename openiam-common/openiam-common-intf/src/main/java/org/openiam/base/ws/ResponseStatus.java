package org.openiam.base.ws;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * ResponseStatus provides valid values that an operation in a service can return.
 *
 * @author suneet
 */
@XmlType(name = "ResponseStatus")
@XmlEnum
public enum ResponseStatus {

    @XmlEnumValue("success")
    SUCCESS("success"),
    @XmlEnumValue("failure")
    FAILURE("failure");
    private final String value;

    ResponseStatus(String val) {
        value = val;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
