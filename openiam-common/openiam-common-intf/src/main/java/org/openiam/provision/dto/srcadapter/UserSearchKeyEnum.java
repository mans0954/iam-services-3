package org.openiam.provision.dto.srcadapter;

import javax.xml.bind.annotation.*;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlType(name = "key_name")
@XmlEnum(String.class)
public enum UserSearchKeyEnum {
    @XmlEnumValue("user_id")
    USERID(0),
    @XmlEnumValue("principal")
    PRINCIPAL(1),
    @XmlEnumValue("email")
    EMAIL(2),
    @XmlEnumValue("employee_id")
    EMPLOYEE_ID(3);


    private int value;

    UserSearchKeyEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
