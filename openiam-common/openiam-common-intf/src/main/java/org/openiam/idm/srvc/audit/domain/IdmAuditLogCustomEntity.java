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
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "OPENIAM_LOG_ATTRIBUTE")
@DozerDTOCorrespondence(IdmAuditLogCustom.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
public class IdmAuditLogCustomEntity implements Serializable {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "OPENIAM_LOG_ATTRIBUTE_ID")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="OPENIAM_LOG_ID", referencedColumnName = "OPENIAM_LOG_ID", insertable = true, updatable = false)
    private IdmAuditLogEntity log;
    
    @Column(name = "NAME", length = 100)
    private String key;
    
    @Column(name = "VALUE")
    private String value;

    @Column(name="CREATED_TIMESTAMP")
    private long timestamp;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdmAuditLogCustomEntity that = (IdmAuditLogCustomEntity) o;

        if (timestamp != that.timestamp) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (log != null ? !log.equals(that.log) : that.log != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = log != null ? log.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
	public String toString() {
		return String.format(
				"IdmAuditLogCustomEntity [id=%s, log=%s, key=%s, value=%s]",
				id, log, key, value);
	}

	
}
