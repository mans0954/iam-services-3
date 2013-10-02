package org.openiam.idm.srvc.audit.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "IDM_AUDIT_LOG_CUSTOM")
@DozerDTOCorrespondence(IdmAuditLogCustom.class)
public class IdmAuditLogCustomEntity implements Serializable {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "OPENIAM_LOG_ATTRIBUTE_ID")
    private String id;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "OPENIAM_LOG_ID", referencedColumnName = "OPENIAM_LOG_ID", insertable = true, updatable = false)
    private IdmAuditLogEntity log;
    
    @Column(name = "NAME", length = 100)
    private String key;
    
    @Column(name = "VALUE")
    private String value;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		IdmAuditLogCustomEntity other = (IdmAuditLogCustomEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"IdmAuditLogCustomEntity [id=%s, log=%s, key=%s, value=%s]",
				id, log, key, value);
	}

	
}
