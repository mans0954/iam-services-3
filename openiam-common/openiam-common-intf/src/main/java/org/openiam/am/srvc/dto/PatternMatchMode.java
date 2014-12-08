package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PatternMatchMode")
@XmlEnum
public enum PatternMatchMode {
	
	@XmlEnumValue("IGNORE")
	IGNORE(0, "IGNORE"),
	
	@XmlEnumValue("NO_PARAMS")
	NO_PARAMS(1, "NO_PARAMS"),
	
	@XmlEnumValue("SPECIFIC_PARAMS")
	SPECIFIC_PARAMS(3, "SPECIFIC_PARAMS"),
	
	@XmlEnumValue("ANY_PARAMS")
	ANY_PARAMS(2, "ANY_PARAMS");
	
	private int level;
	private String value;


	PatternMatchMode(final int level, final String val) {
		this.level = level;
		this.value = val;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String val) {
		value = val;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
