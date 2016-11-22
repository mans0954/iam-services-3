package org.openiam.idm.srvc.res.dto;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created by: Alexander Duckardt
 * Date: 1/16/14.
 */
public enum ResourceRisk {
    @XmlEnumValue("high")
    HIGH("HIGH"),
    @XmlEnumValue("medium")
    MEDIUM("MEDIUM"),
    @XmlEnumValue("low")
    LOW("LOW");

    private String value;

    ResourceRisk(String val) {
        value = val;
    }
    
    public String getValue() {
    	return value;
    }
    
    public static ResourceRisk getByValue(final String val) {
    	ResourceRisk retVal = null;
    	if(val != null) {
    		for(final ResourceRisk risk : ResourceRisk.values()) {
    			if(val.equals(risk.value)) {
    				retVal = risk;
    				break;
    			}
    		}
    	}
    	return retVal;
    }
}
