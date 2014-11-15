package org.openiam.am.srvc.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternMethodParameterEntity;
import org.openiam.am.srvc.domain.URIPatternParameterEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMethodParameter", propOrder = {
	"patternMethodId"
})
@DozerDTOCorrespondence(URIPatternMethodParameterEntity.class)
public class URIPatternMethodParameter extends AbstractParameter {

	private String patternMethodId;

	public String getPatternMethodId() {
		return patternMethodId;
	}

	public void setPatternMethodId(String patternMethodId) {
		this.patternMethodId = patternMethodId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patternMethodId == null) ? 0 : patternMethodId.hashCode());
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
		URIPatternMethodParameter other = (URIPatternMethodParameter) obj;
		if (patternMethodId == null) {
			if (other.patternMethodId != null)
				return false;
		} else if (!patternMethodId.equals(other.patternMethodId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMethodParameter [patternMethodId=" + patternMethodId
				+ ", values=" + values
				+ ", name=" + name + ", id=" + id + ", objectState="
				+ objectState + ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
