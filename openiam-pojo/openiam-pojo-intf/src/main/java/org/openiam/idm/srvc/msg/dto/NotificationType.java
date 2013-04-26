package org.openiam.idm.srvc.msg.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * Status' that a user can be in.
 *
 * @author suneet
 */
@XmlType(name = "NotificationType")
@XmlEnum
public enum NotificationType {

    
    @XmlEnumValue("system")
    SYSTEM("SYSTEM"),
    @XmlEnumValue("configurable")
    CONFIGURABLE("CONFIGURABLE");;
    private String value;

    NotificationType(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }


}
