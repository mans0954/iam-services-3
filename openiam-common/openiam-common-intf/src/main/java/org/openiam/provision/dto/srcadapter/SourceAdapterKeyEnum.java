package org.openiam.provision.dto.srcadapter;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlType(name = "key_name")
@XmlEnum(String.class)
public enum SourceAdapterKeyEnum {
    @XmlEnumValue("user_id")
    USERID(0),
    @XmlEnumValue("principal")
    PRINCIPAL(1),
    @XmlEnumValue("email")
    EMAIL(2),
    @XmlEnumValue("employee_id")
    EMPLOYEE_ID(3);


    private int value;

    SourceAdapterKeyEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
