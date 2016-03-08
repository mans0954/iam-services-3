package org.openiam.idm.srvc.audit.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This is no longer stored in the database - it is stored in ElasticSearch.
 * 
 * This is NO LONGER a Hibernate Entity, and has Hibernate annotations ONLY for
 * backwards compatability and migration purposes.
 * 
 * This shoudl ONLY be stored in ElasticSearch, and NEVER the database
 * 
 * @author zaporozhec
 */
@Entity
@Table(name = "OPENIAM_LOG_ATTRIBUTE")
@DozerDTOCorrespondence(IdmAuditLogCustom.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
@AttributeOverride(name = "id", column = @Column(name = "OPENIAM_LOG_ATTRIBUTE_ID"))
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmAuditLogCustomEntity extends KeyEntity {
    
    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="OPENIAM_LOG_ID", referencedColumnName = "OPENIAM_LOG_ID", insertable = true, updatable = false)
    @Deprecated
    @JsonIgnore
    private IdmAuditLogEntity log;
    
    @Column(name = "NAME", length = 100)
    private String key;

    @Lob
    @Column(name = "VALUE")
    private String value;

    @Column(name="CREATED_TIMESTAMP")
    @Deprecated
    @JsonIgnore
    private long timestamp;

    @Deprecated
	public IdmAuditLogEntity getLog() {
		return log;
	}

    @Deprecated
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

	@Deprecated
    public long getTimestamp() {
        return timestamp;
    }

	@Deprecated
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		IdmAuditLogCustomEntity other = (IdmAuditLogCustomEntity) obj;
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
		if (timestamp != other.timestamp)
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
		return "IdmAuditLogCustomEntity [log=" + log + ", key=" + key
				+ ", value=" + value + ", timestamp=" + timestamp + "]";
	}

	
    
}
