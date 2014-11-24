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
	"metaEntitySet",
	"resourceId",
	"resourceCoorelatedName"
})
@DozerDTOCorrespondence(URIPatternMethodEntity.class)
public class URIPatternMethod extends KeyDTO {

	private String patternId;
	private HttpMethod method;
	private Set<URIPatternMethodParameter> params;
	private Set<URIPatternMethodMeta> metaEntitySet;
	private String resourceId;
	private String resourceCoorelatedName;
	
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
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getResourceCoorelatedName() {
		return resourceCoorelatedName;
	}
	public void setResourceCoorelatedName(String resourceCoorelatedName) {
		this.resourceCoorelatedName = resourceCoorelatedName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
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
		if (method != other.method)
			return false;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMethod [patternId=" + patternId + ", method="
				+ method + ", resourceId=" + resourceId
				+ ", resourceCoorelatedName=" + resourceCoorelatedName + "]";
	}
	
	
	
}
