package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchParam", propOrder = {
	"param",
	"matchType"
})
public class SearchParam {

	private String param;
	private MatchType matchType = MatchType.STARTS_WITH;
	
	public SearchParam() {}
	
	public SearchParam(final String param, final MatchType matchType) {
		this.param = param;
		this.matchType = matchType;
	}
	
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public MatchType getMatchType() {
		return matchType;
	}
	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}
	
	public boolean isValid() {
		return StringUtils.isNotBlank(param) && matchType != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matchType == null) ? 0 : matchType.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
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
		if (param == null) {
			if (other.param != null)
				return false;
		} else if (!param.equals(other.param))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("SearchParam [param=%s, matchType=%s]", param,
				matchType);
	}
	
	
}
