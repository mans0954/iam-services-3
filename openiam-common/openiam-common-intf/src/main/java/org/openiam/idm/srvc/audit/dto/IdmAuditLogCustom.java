package org.openiam.idm.srvc.audit.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;

/**
 * @author zaporozhec
 */
@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdmAuditLogCustom", propOrder = { 
	"logId",
	"key",
	"value",
    "timestamp"
})
@DozerDTOCorrespondence(IdmAuditLogCustomEntity.class)
public class IdmAuditLogCustom extends KeyDTO {
    
	@Deprecated
	private String logId;
	private String key;
	private String value;
    private long timestamp;

    @Deprecated
	public String getLogId() {
		return logId;
	}

    @Deprecated
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((logId == null) ? 0 : logId.hashCode());
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
		IdmAuditLogCustom other = (IdmAuditLogCustom) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (logId == null) {
			if (other.logId != null)
				return false;
		} else if (!logId.equals(other.logId))
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
		return "IdmAuditLogCustom [logId=" + logId + ", key=" + key
				+ ", value=" + value + ", timestamp=" + timestamp + "]";
	}

    
}
