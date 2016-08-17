package org.openiam.constants;

import javax.xml.bind.annotation.*;

@XmlType(name = "ProvisionActionTypeEnum")
@XmlEnum
public enum ProvisionActionTypeEnum {

    @XmlEnumValue("PRE")
    PRE("PRE"),
    @XmlEnumValue("POST")
    POST("POST");

    private String value;

    ProvisionActionTypeEnum(String val) {
        value = val;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String val) {
        value = val;
    }

}
