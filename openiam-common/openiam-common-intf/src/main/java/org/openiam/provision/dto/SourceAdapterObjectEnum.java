package org.openiam.provision.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlType(name = "object")
@XmlEnum(String.class)
public enum SourceAdapterObjectEnum {
    @XmlEnumValue("group")
    GROUP(0),
    @XmlEnumValue("role")
    ROLE(1),
    @XmlEnumValue("resource")
    RESOURCE(2);


    private int value;

    SourceAdapterObjectEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
