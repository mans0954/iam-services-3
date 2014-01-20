package org.openiam.am.srvc.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.pk.AuthLevelGroupingURIPatternXrefIdEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGroupingURIPatternXrefIdEntity", propOrder = {
        "groupingId",
        "patternId"
})
@DozerDTOCorrespondence(AuthLevelGroupingURIPatternXrefIdEntity.class)
public class AuthLevelGroupingURIPatternXrefId implements Serializable {

	private String groupingId;
	private String patternId;
	
	public AuthLevelGroupingURIPatternXrefId() {
		
	}
	
	public String getGroupingId() {
		return groupingId;
	}
	public void setGroupingId(String groupingId) {
		this.groupingId = groupingId;
	}
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((groupingId == null) ? 0 : groupingId.hashCode());
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
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
		AuthLevelGroupingURIPatternXrefId other = (AuthLevelGroupingURIPatternXrefId) obj;
		if (groupingId == null) {
			if (other.groupingId != null)
				return false;
		} else if (!groupingId.equals(other.groupingId))
			return false;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("AuthLevelGroupingURIPatternXrefId [groupingId=%s, patternId=%s]",
						groupingId, patternId);
	}
	
	
}
