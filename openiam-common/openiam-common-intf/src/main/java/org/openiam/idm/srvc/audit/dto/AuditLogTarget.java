package org.openiam.idm.srvc.audit.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;

@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogTarget", propOrder = { 
	"logId",
	"targetId",
	"targetType",
    "objectPrincipal"
})
@DozerDTOCorrespondence(AuditLogTargetEntity.class)
public class AuditLogTarget extends KeyDTO {

	private String logId;
	private String targetId;
    private String objectPrincipal;

	private String targetType;
	
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

    public String getObjectPrincipal() {
        return objectPrincipal;
    }

    public void setObjectPrincipal(String objectPrincipal) {
        this.objectPrincipal = objectPrincipal;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((logId == null) ? 0 : logId.hashCode());
		result = prime * result
				+ ((objectPrincipal == null) ? 0 : objectPrincipal.hashCode());
		result = prime * result
				+ ((targetId == null) ? 0 : targetId.hashCode());
		result = prime * result
				+ ((targetType == null) ? 0 : targetType.hashCode());
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
		AuditLogTarget other = (AuditLogTarget) obj;
		if (logId == null) {
			if (other.logId != null)
				return false;
		} else if (!logId.equals(other.logId))
			return false;
		if (objectPrincipal == null) {
			if (other.objectPrincipal != null)
				return false;
		} else if (!objectPrincipal.equals(other.objectPrincipal))
			return false;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		if (targetType == null) {
			if (other.targetType != null)
				return false;
		} else if (!targetType.equals(other.targetType))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AuditLogTarget [logId=" + logId + ", targetId=" + targetId
				+ ", objectPrincipal=" + objectPrincipal + ", targetType="
				+ targetType + "]";
	}

    
}
