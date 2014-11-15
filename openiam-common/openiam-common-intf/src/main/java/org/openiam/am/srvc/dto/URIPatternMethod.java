package org.openiam.am.srvc.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternMethodEntity;
import org.openiam.am.srvc.domain.URIPatternMethodMetaEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.springframework.http.HttpMethod;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMethod", propOrder = {
	"patternId",
	"method",
	"params",
	"metaEntitySet"
})
@DozerDTOCorrespondence(URIPatternMethodEntity.class)
public class URIPatternMethod extends KeyDTO {

	private String patternId;
	private HttpMethod method;
	private Set<URIPatternMethodParameter> params;
	private Set<URIPatternMethodMeta> metaEntitySet;
	
	public Set<URIPatternMethodMeta> getMetaEntitySet() {
		return metaEntitySet;
	}
	public void setMetaEntitySet(Set<URIPatternMethodMeta> metaEntitySet) {
		this.metaEntitySet = metaEntitySet;
	}
	public Set<URIPatternMethodParameter> getParams() {
		return params;
	}
	public void setParams(Set<URIPatternMethodParameter> parameters) {
		this.params = parameters;
	}
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
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
		URIPatternMethod other = (URIPatternMethod) obj;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		if (method != other.method)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMethod [patternId=" + patternId + ", method="
				+ method + "]";
	}
	
	
}
