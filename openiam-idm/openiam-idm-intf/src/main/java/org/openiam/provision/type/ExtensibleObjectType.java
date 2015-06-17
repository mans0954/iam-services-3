package org.openiam.provision.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ProvisionObjectType")
@XmlEnum
public enum ExtensibleObjectType {
    @XmlEnumValue("ROLE")
    ROLE,
    @XmlEnumValue("USER")
    USER,
    @XmlEnumValue("ADDRESS")
    ADDRESS,
    @XmlEnumValue("EMAIL")
    EMAIL,
    @XmlEnumValue("PHONE")
    PHONE,
    @XmlEnumValue("GROUP")
    GROUP
}
