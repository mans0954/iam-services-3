package org.openiam.idm.srvc.audit.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;

/**
 * @author zaporozhec
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdmAuditLogCustom", propOrder = { 
	"id",
	"logId",
	"key",
	"value",
    "timestamp"
})
@DozerDTOCorrespondence(IdmAuditLogCustomEntity.class)
public class IdmAuditLogCustom implements Serializable {
    
	private String id;
	private String logId;
	private String key;
	private String value;
    private long timestamp;

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

        IdmAuditLogCustom that = (IdmAuditLogCustom) o;

        if (timestamp != that.timestamp) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (logId != null ? !logId.equals(that.logId) : that.logId != null) return false;
		return !(value != null ? !value.equals(that.value) : that.value != null);

	}

    @Override
    public int hashCode() {
        int result = logId != null ? logId.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
	public String toString() {
		return String.format(
				"IdmAuditLogCustom [id=%s, logId=%s, key=%s, value=%s]",
				id, logId, key, value);
	}

    
}
