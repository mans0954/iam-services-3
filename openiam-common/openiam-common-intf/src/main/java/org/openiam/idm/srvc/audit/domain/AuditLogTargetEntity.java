package org.openiam.idm.srvc.audit.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;

@Entity
@Table(name="OPENIAM_LOG_TARGET")
@DozerDTOCorrespondence(AuditLogTarget.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
public class AuditLogTargetEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "OPENIAM_LOG_TARGET_ID")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="OPENIAM_LOG_ID", referencedColumnName = "OPENIAM_LOG_ID", insertable = true, updatable = false)
    private IdmAuditLogEntity log;
    
    @Column(name = "TARGET_ID", length = 32)
    private String targetId;
    
    @Column(name = "TARGET_TYPE",length=70)
    private String targetType;

    @Column(name = "OBJECT_PRINCIPAL",length=70)
    private String objectPrincipal;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IdmAuditLogEntity getLog() {
		return log;
	}

	public void setLog(IdmAuditLogEntity log) {
		this.log = log;
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

        AuditLogTargetEntity that = (AuditLogTargetEntity) o;

        if (log != null ? !log.equals(that.log) : that.log != null) return false;
        if (objectPrincipal != null ? !objectPrincipal.equals(that.objectPrincipal) : that.objectPrincipal != null)
            return false;
        if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;
        if (targetType != null ? !targetType.equals(that.targetType) : that.targetType != null) return false;

        return true;
    }
    // WARNING!  We can't match this object by ID. This object can be equals with different IDs !!!
    @Override
    public int hashCode() {
        int result = log != null ? log.hashCode() : 0;
        result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
        result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
        result = 31 * result + (objectPrincipal != null ? objectPrincipal.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("AuditLogTargetEntity [id=%s, objectPrincipal=%s, log=%s, targetId=%s, targetType=%s]",
                        id, objectPrincipal, log, targetId, targetType);
	}
	
    
}
