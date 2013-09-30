package org.openiam.idm.srvc.audit.dto;

// Generated Nov 30, 2007 3:01:45 AM by Hibernate Tools 3.2.0.b11

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
	"loginId",
	"userId",
	"timestamp",
	"source",
	"clientIP",
	"nodeIP",
	"action",
	"result",
	"objectID",
	"objectType",
	"hash",
	"sessionID",
	"customRecords",
	"childLogs"
})
@DozerDTOCorrespondence(IdmAuditLogEntity.class)
public class IdmAuditLog implements Serializable {
	
    private String id;
    private String userId;
    private String loginId;
    private Date timestamp;
    private String source;
    private String clientIP;
    private String nodeIP;
    private String action;
    private String result;
    private String objectID;
    private String objectType;
    private String hash;
    private String sessionID;
    private Set<IdmAuditLogCustom> customRecords;
    private Set<IdmAuditLog> childLogs;

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

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, loginId, nodeIP, objectID, objectType, result, source, timestamp, userId, sessionID);
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
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
		result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
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
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		
		if (sessionID == null) {
			if (other.sessionID != null)
				return false;
		} else if (!sessionID.equals(other.sessionID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("IdmAuditLog [id=%s, userId=%s, loginId=%s, timestamp=%s, source=%s, clientIP=%s, nodeIP=%s, action=%s, result=%s, objectID=%s, objectType=%s, hash=%s]",
						id, userId, loginId, timestamp, source, clientIP,
						nodeIP, action, result, objectID, objectType, hash);
	}

	
}
