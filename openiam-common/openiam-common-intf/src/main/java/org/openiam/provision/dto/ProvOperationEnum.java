package org.openiam.provision.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvOperationEnum")
@XmlEnum
public enum ProvOperationEnum {
    @XmlEnumValue("nochange")
    NO_CHANGE(0),
    @XmlEnumValue("create")
    CREATE(1),
    @XmlEnumValue("update")
    UPDATE(2),
    @XmlEnumValue("delete")
    DELETE(3),
    @XmlEnumValue("disable")
    DISABLE(4);

    private int value;

    ProvOperationEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }

}
