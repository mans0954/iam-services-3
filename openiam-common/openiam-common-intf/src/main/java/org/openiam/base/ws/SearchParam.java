package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * Class to wrap a search parameter, and the matching type to use
 * @author lbornov2
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchParam", propOrder = {
	"value",
	"matchType"
})
public class SearchParam {

	/**
	 * The value to search for
	 */
	private String value;
	
	/**
	 * The match type to use when searching
	 */
	private MatchType matchType = MatchType.STARTS_WITH;
	
	public SearchParam() {}
	
	public SearchParam(final String value, final MatchType matchType) {
		this.value = value;
		this.matchType = matchType;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public MatchType getMatchType() {
		return matchType;
	}
	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}
	
	public boolean isValid() {
		return StringUtils.isNotBlank(value) && matchType != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matchType == null) ? 0 : matchType.hashCode());
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
		SearchParam other = (SearchParam) obj;
		if (matchType != other.matchType)
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
		return String.format("SearchParam [value=%s, matchType=%s]", value,
				matchType);
	}
	
	
}
