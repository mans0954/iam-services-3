package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternRuleValue", propOrder = {
	"key",
	"value",
	"propagate"
})
public class URIPatternRuleValue implements Serializable {

	private String key;
	private String value;
	private boolean propagate = true;
	
	private URIPatternRuleValue() {}
	
	public URIPatternRuleValue(final String key, final String value, final boolean propagate) {
		this();
		this.key = key;
		this.value = value;
		this.propagate = propagate;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean isPropagate() {
		return propagate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + (propagate ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternRuleValue other = (URIPatternRuleValue) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (propagate != other.propagate)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"URIPatternRuleValue [key=%s, value=%s, propagate=%s]", key,
				value, propagate);
	}

	
}
