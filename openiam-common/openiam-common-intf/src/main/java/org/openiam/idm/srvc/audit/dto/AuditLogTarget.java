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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogTarget that = (AuditLogTarget) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (logId != null ? !logId.equals(that.logId) : that.logId != null) return false;
        if (objectPrincipal != null ? !objectPrincipal.equals(that.objectPrincipal) : that.objectPrincipal != null)
            return false;
        if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;
        if (targetType != null ? !targetType.equals(that.targetType) : that.targetType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (logId != null ? logId.hashCode() : 0);
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
