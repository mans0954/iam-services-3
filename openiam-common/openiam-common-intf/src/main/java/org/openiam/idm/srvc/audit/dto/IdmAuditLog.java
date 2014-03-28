package org.openiam.idm.srvc.audit.dto;

// Generated Nov 30, 2007 3:01:45 AM by Hibernate Tools 3.2.0.b11

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * DTO object that is used log and retrieve audit information
 * Refactoring 6.12.2012
 * @author zaporozhec 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdmAuditLog", propOrder = {
	"id",
	"principal",
	"managedSysId",
	"userId",
	"timestamp",
	"source",
	"clientIP",
	"nodeIP",
	"action",
	"result",
	"hash",
	"sessionID",
	"customRecords",
	"childLogs",
	"coorelationId",
	"targets",
	"parentLogs"
})
@DozerDTOCorrespondence(IdmAuditLogEntity.class)
public class IdmAuditLog implements Serializable {
	
    private String id;
    private String userId;
    private String principal;
    private String managedSysId;
    private Date timestamp;
    private String source;
    private String clientIP;
    private String nodeIP;
    private String action;
    private String result;
    private String hash;
    private String sessionID;
    private String coorelationId;
    private Set<IdmAuditLogCustom> customRecords;
    private Set<AuditLogTarget> targets;
    private Set<IdmAuditLog> childLogs;
    private Set<IdmAuditLog> parentLogs;

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
	
	public String getCoorelationId() {
		return coorelationId;
	}

	public void setCoorelationId(String coorelationId) {
		this.coorelationId = coorelationId;
	}
	
	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, result, source, timestamp, userId, sessionID, managedSysId, coorelationId);
	}
	
	public Set<IdmAuditLogCustom> getCustomRecords() {
		return customRecords;
	}

	public void setCustomRecords(Set<IdmAuditLogCustom> customRecords) {
		this.customRecords = customRecords;
	}

	public Set<IdmAuditLog> getChildLogs() {
		return childLogs;
	}

	public void setChildLogs(Set<IdmAuditLog> childLogs) {
		this.childLogs = childLogs;
	}
	
    public Set<AuditLogTarget> getTargets() {
		return targets;
	}

	public void setTargets(Set<AuditLogTarget> targets) {
		this.targets = targets;
	}
	
	public Set<IdmAuditLog> getParentLogs() {
		return parentLogs;
	}

	public void setParentLogs(Set<IdmAuditLog> parentLogs) {
		this.parentLogs = parentLogs;
	}

	public void addTarget(final String targetId, final String targetType) {
		if(targetId != null && targetType != null) {
			if(this.targets == null) {
				this.targets = new HashSet<AuditLogTarget>();
			}
			final AuditLogTarget target = new AuditLogTarget();
			target.setTargetId(targetId);
			target.setTargetType(targetType);
			target.setLogId(id);
			this.targets.add(target);
		}
	}

	public void addChild(final IdmAuditLog entity) {
    	if(entity != null) {
    		if(this.childLogs == null) {
    			this.childLogs = new HashSet<IdmAuditLog>();
    		}
    		this.childLogs.add(entity);
    	}
    }

    public void addParent(final IdmAuditLog event) {
        if(event != null) {
            if(this.parentLogs == null) {
                this.parentLogs = new HashSet<IdmAuditLog>();
            }
            this.parentLogs.add(event);
        }
    }

    public void addCustomRecord(final String key, final String value) {
    	if(key != null && value != null) {
    		if(customRecords == null) {
    			customRecords = new HashSet<IdmAuditLogCustom>();
    		}
    		final IdmAuditLogCustom logAttr = new IdmAuditLogCustom();
            logAttr.setKey(key);
            logAttr.setValue(value);
            logAttr.setTimestamp(new Date().getTime());
    		customRecords.add(logAttr);
    	}
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
		IdmAuditLog other = (IdmAuditLog) obj;
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
				.format("IdmAuditLog [id=%s, userId=%s, principal=%s, timestamp=%s, source=%s, clientIP=%s, nodeIP=%s, action=%s, result=%s, hash=%s]",
						id, userId, principal, timestamp, source, clientIP,
						nodeIP, action, result, hash);
	}

	
}
