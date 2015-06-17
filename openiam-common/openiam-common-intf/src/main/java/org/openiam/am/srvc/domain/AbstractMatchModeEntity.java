package org.openiam.am.srvc.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.base.domain.KeyEntity;

@MappedSuperclass
public abstract class AbstractMatchModeEntity extends KeyEntity {

    @Column(name="PARAM_MATCH_MODE", length = 100)
    @Enumerated(EnumType.STRING)
    private PatternMatchMode matchMode;
    
    public AbstractMatchModeEntity() {}

	public PatternMatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(PatternMatchMode matchMode) {
		this.matchMode = matchMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((matchMode == null) ? 0 : matchMode.hashCode());
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
		AbstractMatchModeEntity other = (AbstractMatchModeEntity) obj;
		return matchMode == other.matchMode;
	}

	@Override
	public String toString() {
		return "AbstractMatchModeEntity [matchMode=" + matchMode + ", id=" + id
				+ "]";
	}

	
}
