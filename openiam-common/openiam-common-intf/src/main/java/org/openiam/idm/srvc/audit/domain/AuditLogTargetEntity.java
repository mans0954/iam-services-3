package org.openiam.idm.srvc.audit.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Parent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name="OPENIAM_LOG_TARGET")
@DozerDTOCorrespondence(AuditLogTarget.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
@AttributeOverride(name = "id", column = @Column(name = "OPENIAM_LOG_TARGET_ID"))
@Document(indexName = ESIndexName.AUDIT_LOG, type= ESIndexType.AUDIT_LOG_TARGETS)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogTargetEntity extends KeyEntity {

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="OPENIAM_LOG_ID", referencedColumnName = "OPENIAM_LOG_ID", insertable = true, updatable = false)
    @Deprecated
    @JsonIgnore
    private IdmAuditLogEntity log;
    
    @Parent(type=ESIndexType.AUDIT_LOG)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Transient
    private String logId;
    
    @Column(name = "TARGET_ID", length = 32)
    private String targetId;
    
    @Column(name = "TARGET_TYPE",length=70)
    private String targetType;

    @Column(name = "OBJECT_PRINCIPAL",length=70)
    private String objectPrincipal;

    @Deprecated
	public IdmAuditLogEntity getLog() {
		return log;
	}

    @Deprecated
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

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((log == null) ? 0 : log.hashCode());
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
		AuditLogTargetEntity other = (AuditLogTargetEntity) obj;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
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
		return "AuditLogTargetEntity [log=" + log + ", logId=" + logId
				+ ", targetId=" + targetId + ", targetType=" + targetType
				+ ", objectPrincipal=" + objectPrincipal + "]";
	}

	
}
