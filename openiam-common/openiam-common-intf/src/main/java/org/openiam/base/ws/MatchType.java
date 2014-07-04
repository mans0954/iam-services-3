package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "MatchType")
@XmlEnum
public enum MatchType {

	@XmlEnumValue("EXACT")
	EXACT("EXACT"),

    @XmlEnumValue("STARTS_WITH")
	STARTS_WITH("STARTS_WITH");

    private final String type;

    MatchType(final String type) {
    	this.type = type;
    }
}
