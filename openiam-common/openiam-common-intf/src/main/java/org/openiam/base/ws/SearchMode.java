package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SearchMode")
@XmlEnum
public enum SearchMode {

	@XmlEnumValue("AND")
	AND("AND"),

    @XmlEnumValue("OR")
	OR("OR");

    private final String type;

    SearchMode(final String type) {
    	this.type = type;
    }
}
