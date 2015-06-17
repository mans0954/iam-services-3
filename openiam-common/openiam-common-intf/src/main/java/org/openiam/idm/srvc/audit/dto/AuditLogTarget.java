package org.openiam.idm.srvc.audit.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogTarget", propOrder = { 
	"id",
	"logId",
	"targetId",
	"targetType",
    "objectPrincipal"
})
@DozerDTOCorrespondence(AuditLogTargetEntity.class)
public class AuditLogTarget implements Serializable {

	private String id;
	private String logId;
	private String targetId;
    private String objectPrincipal;

	private String targetType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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

    // WARNING!  We can't match this object by ID. This object can be equals with different IDs !!!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogTarget logTarget = (AuditLogTarget) o;

        if (logId != null ? !logId.equals(logTarget.logId) : logTarget.logId != null) return false;
        if (objectPrincipal != null ? !objectPrincipal.equals(logTarget.objectPrincipal) : logTarget.objectPrincipal != null)
            return false;
        if (targetId != null ? !targetId.equals(logTarget.targetId) : logTarget.targetId != null) return false;
		return !(targetType != null ? !targetType.equals(logTarget.targetType) : logTarget.targetType != null);

	}

    // WARNING!  We can't match this object by ID. This object can be equals with different IDs !!!
    @Override
    public int hashCode() {
        int result = logId != null ? logId.hashCode() : 0;
        result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
        result = 31 * result + (objectPrincipal != null ? objectPrincipal.hashCode() : 0);
        result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String.format(
				"AuditLogTarget [id=%s, objectPrincipal=%s, logId=%s, targetId=%s, targetType=%s]",
				id, objectPrincipal, logId, targetId, targetType);
	}
	
	
}
