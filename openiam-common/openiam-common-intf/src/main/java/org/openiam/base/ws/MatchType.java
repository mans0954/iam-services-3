package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Match Type used in search
 * @author lbornov2
 *
 */
@XmlType(name = "MatchType")
@XmlEnum
public enum MatchType {

	/**
	 * Implies an EXACT match
	 */
	@XmlEnumValue("EXACT")
	EXACT("EXACT"),
	
	/**
	 * Implies a 'starts with' match
	 */
    @XmlEnumValue("STARTS_WITH")
	STARTS_WITH("STARTS_WITH"),

    @XmlEnumValue("END_WITH")
    END_WITH("END_WITH"),

    @XmlEnumValue("CONTAINS")
    CONTAINS("CONTAINS");

    private final String type;

    MatchType(final String type) {
    	this.type = type;
    }
}
