package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMatchMode", propOrder = {
        "matchMode"
})
public abstract class AbstractMatchMode extends KeyDTO {

	protected PatternMatchMode matchMode;
	
	public AbstractMatchMode() {}
	
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
		AbstractMatchMode other = (AbstractMatchMode) obj;
		if (matchMode != other.matchMode)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractMatchMode [matchMode=" + matchMode + ", id=" + id
				+ ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}

	
}
