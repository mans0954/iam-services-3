package org.openiam.idm.srvc.audit.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuditLogTargetEntity other = (AuditLogTargetEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
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
		return String
				.format("AuditLogTargetEntity [id=%s, log=%s, targetId=%s, targetType=%s]",
						id, log, targetId, targetType);
	}
	
    
}
