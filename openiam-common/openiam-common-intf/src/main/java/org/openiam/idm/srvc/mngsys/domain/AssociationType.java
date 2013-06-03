package org.openiam.idm.srvc.mngsys.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AssociationType")
@XmlEnum
public enum AssociationType {
	
	@XmlEnumValue("user")
    USER("USER"),
    @XmlEnumValue("supervisor")
    SUPERVISOR("SUPERVISOR"),
    @XmlEnumValue("role")
    ROLE("ROLE"),
    @XmlEnumValue("group")
    GROUP("GROUP"),
    @XmlEnumValue("target_user")
    TARGET_USER("TARGET_USER");
    

	private String value;
	
	AssociationType(final String value) {
		this.value = value;
	}
	
	public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }
}
