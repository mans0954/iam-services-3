package org.openiam.idm.srvc.mngsys.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AssociationType")
@XmlEnum
public enum AssociationType {
	
	@XmlEnumValue("user")
    USER("USER", true, true),
    @XmlEnumValue("supervisor")
    SUPERVISOR("SUPERVISOR", true, true),
    @XmlEnumValue("role")
    ROLE("ROLE", true, true),
    @XmlEnumValue("group")
    GROUP("GROUP", true, true),
    @XmlEnumValue("target_user")
    TARGET_USER("TARGET_USER", false, true),
    @XmlEnumValue("resource")
    RESOURCE("RESOURCE", false, false);
    

	private boolean isApprover;
	private String value;
	private boolean isNotifiable;
	
	AssociationType(final String value, final boolean isApprover, final boolean isNotifiable) {
		this.value = value;
		this.isApprover = isApprover;
		this.isNotifiable = isNotifiable;
	}
	
	public String getValue() {
        return value;
    }
    
    public boolean getIsApprover() {
    	return isApprover;
    }
    
    public boolean getIsNotifiable() {
    	return isNotifiable;
    }
    
    public static AssociationType getByValue(final String v) {
    	AssociationType retVal = null;
    	for(final AssociationType type : AssociationType.values()) {
    		if(type.value.equals(v)) {
    			retVal = type;
    			break;
    		}
    	}
    	return retVal;
    }
}
