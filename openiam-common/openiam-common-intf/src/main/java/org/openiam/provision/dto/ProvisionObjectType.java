package org.openiam.provision.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvisionObjectType")
@XmlEnum
public enum ProvisionObjectType {
    @XmlEnumValue("ROLE")
    ROLE,
    @XmlEnumValue("USER")
    USER,
    @XmlEnumValue("GROUP")
    GROUP;
}
