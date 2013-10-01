package org.openiam.idm.srvc.audit.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

@Entity
@Table(name = "OPENIAM_LOG")
@DozerDTOCorrespondence(IdmAuditLog.class)
public class IdmAuditLogEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "OPENIAM_LOG_ID", length = 32)
    private String id;
    
    @Column(name="USER_ID", length=32)
    private String userId;
    
    @Column(name="PRINCIPAL", length=320)
    private String principal;
    
    @Column(name="MANAGED_SYS_ID", length=32)
    private String managedSysId;
    
    @Column(name="CREATED_DATETIME")
    private Date timestamp;

    @Column(name="SOURCE", length=50)
    private String source;
    
    @Column(name="CLIENT_IP", length=50)
    private String clientIP;
    
    @Column(name="NODE_ID", length=50)
    private String nodeIP;
    
    @Column(name="LOG_ACTION", length=50)
    private String action;
    
    @Column(name="RESULT", length=50)
    private String result;
    
    @Column(name="OBJECT_ID", length=32)
    private String objectID;
    
    @Column(name="OBJECT_TYPE", length=70)
    private String objectType;
    
    @Column(name="HASH", length=100)
    private String hash;
    
    @Column(name="SESSION_ID", length=100)
    private String sessionID;
    
    @Column(name="CORRELATION_ID", length=32)
    private String coorelationId;
    
    @OneToMany(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY)
    @JoinColumn(name="OPENIAM_LOG_ID", referencedColumnName="OPENIAM_LOG_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<IdmAuditLogCustomEntity> customRecords;
    
    @ManyToMany(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY)
    @JoinTable(name = "OPENIAM_LOG_LOG_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "OPENIAM_LOG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "OPENIAM_MEMBER_LOG_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<IdmAuditLogEntity> childLogs;
    
    public void addChild(final IdmAuditLogEntity entity) {
    	if(entity != null) {
    		if(this.childLogs == null) {
    			this.childLogs = new HashSet<IdmAuditLogEntity>();
    		}
    		this.childLogs.add(entity);
    	}
    }
    
    public void addCustomRecord(final String key, final String value) {
    	if(key != null && value != null) {
    		if(customRecords == null) {
    			customRecords = new HashSet<IdmAuditLogCustomEntity>();
    		}
    		final IdmAuditLogCustomEntity entity = new IdmAuditLogCustomEntity();
    		entity.setKey(key);
    		entity.setValue(value);
    		customRecords.add(entity);
    	}
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getNodeIP() {
		return nodeIP;
	}

	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public Set<IdmAuditLogCustomEntity> getCustomRecords() {
		return customRecords;
	}

	public void setCustomRecords(Set<IdmAuditLogCustomEntity> customRecords) {
		this.customRecords = customRecords;
	}
	
	public Set<IdmAuditLogEntity> getChildLogs() {
		return childLogs;
	}

	public void setChildLogs(Set<IdmAuditLogEntity> childLogs) {
		this.childLogs = childLogs;
	}

	public String getCoorelationId() {
		return coorelationId;
	}

	public void setCoorelationId(String coorelationId) {
		this.coorelationId = coorelationId;
	}

	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, objectID, objectType, result, source, timestamp, userId, sessionID, managedSysId, coorelationId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((clientIP == null) ? 0 : clientIP.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nodeIP == null) ? 0 : nodeIP.hashCode());
		result = prime * result
				+ ((objectID == null) ? 0 : objectID.hashCode());
		result = prime * result
				+ ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((principal == null) ? 0 : principal.hashCode());
		result = prime * result + ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
		result = prime * result + ((coorelationId == null) ? 0 : coorelationId.hashCode());
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
		IdmAuditLogEntity other = (IdmAuditLogEntity) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (clientIP == null) {
			if (other.clientIP != null)
				return false;
		} else if (!clientIP.equals(other.clientIP))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nodeIP == null) {
			if (other.nodeIP != null)
				return false;
		} else if (!nodeIP.equals(other.nodeIP))
			return false;
		if (objectID == null) {
			if (other.objectID != null)
				return false;
		} else if (!objectID.equals(other.objectID))
			return false;
		if (objectType == null) {
			if (other.objectType != null)
				return false;
		} else if (!objectType.equals(other.objectType))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		
		if (sessionID == null) {
			if (other.sessionID != null)
				return false;
		} else if (!sessionID.equals(other.sessionID))
			return false;
		
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		
		if (coorelationId == null) {
			if (other.coorelationId != null)
				return false;
		} else if (!coorelationId.equals(other.coorelationId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("IdmAuditLogEntity [id=%s, userId=%s, principal=%s, timestamp=%s, source=%s, clientIP=%s, nodeIP=%s, action=%s, result=%s, objectID=%s, objectType=%s, hash=%s]",
						id, userId, principal, timestamp, source, clientIP,
						nodeIP, action, result, objectID, objectType, hash);
	}


    
}