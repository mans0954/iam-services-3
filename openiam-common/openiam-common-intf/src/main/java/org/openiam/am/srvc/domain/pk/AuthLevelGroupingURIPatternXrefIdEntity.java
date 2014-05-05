package org.openiam.am.srvc.domain.pk;

import java.io.Serializable;

import javax.persistence.Column;

import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXrefId;
import org.openiam.dozer.DozerDTOCorrespondence;

@DozerDTOCorrespondence(AuthLevelGroupingURIPatternXrefId.class)
public class AuthLevelGroupingURIPatternXrefIdEntity implements Serializable {

	@Column(name="AUTH_LEVEL_GROUPING_ID", length = 32, nullable = false)
    private String groupingId;
	
    @Column(name="URI_PATTERN_ID", length = 32, nullable = false)
    private String patternId;

    public AuthLevelGroupingURIPatternXrefIdEntity() {
    	
    }
    
    public AuthLevelGroupingURIPatternXrefIdEntity(final String groupingId, final String patternId) {
    	this.groupingId = groupingId;
    	this.patternId = patternId;
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
		AuthLevelGroupingURIPatternXrefIdEntity other = (AuthLevelGroupingURIPatternXrefIdEntity) obj;
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
				.format("AuthLevelGroupingURIPatternXrefIdEntity [groupingId=%s, patternId=%s]",
						groupingId, patternId);
	}
    
    
}
