package org.openiam.provision.dto.srcadapter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(name = "operation")
@XmlEnum(String.class)
public enum SourceAdapterOperationEnum {
    @XmlEnumValue("no_change")
    NO_CHANGE(0),
    @XmlEnumValue("add")
    ADD(1),
    @XmlEnumValue("modify")
    MODIFY(2),
    @XmlEnumValue("delete")
    DELETE(3),
    @XmlEnumValue("disable")
    DISABLE(4),
    @XmlEnumValue("enable")
    ENABLE(5),
    @XmlEnumValue("change_password")
    CHANGE_PASSWORD(6),
    @XmlEnumValue("reset_password")
    RESET_PASSWORD(7);


    private int value;

    SourceAdapterOperationEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
