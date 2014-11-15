package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternParameterEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternParameter", propOrder = {
	"patternId"
})
@DozerDTOCorrespondence(URIPatternParameterEntity.class)
public class URIPatternParameter extends AbstractParameter {

	private String patternId;

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		URIPatternParameter other = (URIPatternParameter) obj;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternParameter [patternId=" + patternId + 
				", values=" + values + ", name=" + name + ", id="
				+ id + ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}
	
	
}
