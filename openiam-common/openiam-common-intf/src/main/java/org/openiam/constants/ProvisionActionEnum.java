package org.openiam.constants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvisionActionEnum")
@XmlEnum
public enum ProvisionActionEnum {

    @XmlEnumValue("SAVE")
    SAVE("SAVE"),
    @XmlEnumValue("ACTIVATE")
    ACTIVATE("ACTIVATE"),
    @XmlEnumValue("DEACTIVATE")
    DEACTIVATE("DEACTIVATE"),
    @XmlEnumValue("ENABLE")
    ENABLE("ENABLE"),
    @XmlEnumValue("DISABLE")
    DISABLE("DISABLE"),
    @XmlEnumValue("DELETE")
    DELETE("DELETE"),
    @XmlEnumValue("RESET_SECURITY_QUESTIONS")
    RESET_SECURITY_QUESTIONS("RESET_SECURITY_QUESTIONS"),
    @XmlEnumValue("RESET_ACCOUNT")
    RESET_ACCOUNT("RESET_ACCOUNT"),
    @XmlEnumValue("SET_PASSWORD")
    SET_PASSWORD("SET_PASSWORD"),
    @XmlEnumValue("RESET_PASSWORD")
    RESET_PASSWORD("RESET_PASSWORD"),
    @XmlEnumValue("RESYNC_PASSWORD")
    RESYNC_PASSWORD("RESYNC_PASSWORD");

    private String value;

    ProvisionActionEnum(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

}
