package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIParamXSSRuleEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIParamXSSRule", propOrder = {
	"patternId",
	"paramName",
	"ignoreXSS"
})
@DozerDTOCorrespondence(URIParamXSSRuleEntity.class)
public class URIParamXSSRule extends KeyDTO {
	
	public URIParamXSSRule() {}

	private String patternId;
	private String paramName;
	private boolean ignoreXSS = true;
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public boolean isIgnoreXSS() {
		return ignoreXSS;
	}
	public void setIgnoreXSS(boolean ignoreXSS) {
		this.ignoreXSS = ignoreXSS;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (ignoreXSS ? 1231 : 1237);
		result = prime * result
				+ ((paramName == null) ? 0 : paramName.hashCode());
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIParamXSSRule other = (URIParamXSSRule) obj;
		if (ignoreXSS != other.ignoreXSS)
			return false;
		if (paramName == null) {
			if (other.paramName != null)
				return false;
		} else if (!paramName.equals(other.paramName))
			return false;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		return true;
	}
	
	
}
