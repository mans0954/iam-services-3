package org.openiam.idm.srvc.audit.dto;

// Generated Nov 30, 2007 3:01:45 AM by Hibernate Tools 3.2.0.b11

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openiam.base.BaseObject;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.*;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.util.CustomJacksonMapper;

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
	"correlationId",
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
    private String result = AuditResult.SUCCESS.value();
    private String hash;
    private String sessionID;
    private String correlationId;
    private Set<IdmAuditLogCustom> customRecords;
    private Set<AuditLogTarget> targets;
    private Set<IdmAuditLog> childLogs;
    private Set<IdmAuditLog> parentLogs;

    public IdmAuditLog() {
        setTimestamp(new Date());
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
	
	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, result, source, timestamp, userId, sessionID, managedSysId, correlationId);
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

	public void addTarget(final String targetId, final String targetType, final String principal) {
		if(targetId != null && targetType != null) {
			if(this.targets == null) {
				this.targets = new HashSet<>();
			}
			final AuditLogTarget target = new AuditLogTarget();
			target.setTargetId(targetId);
			target.setTargetType(targetType);
            target.setObjectPrincipal(principal);
			target.setLogId(id);
			this.targets.add(target);
		}
	}

	public void addChild(final IdmAuditLog entity) {
    	if(entity != null) {
    		if(this.childLogs == null) {
    			this.childLogs = new HashSet<IdmAuditLog>();
    		}
            if(entity.getResult() == null) {
                entity.setResult(this.getResult());
            }
    		this.childLogs.add(entity);
    	}
    }

    public void addParent(final IdmAuditLog event) {
        if(event != null) {
            if(this.parentLogs == null) {
                this.parentLogs = new HashSet<>();
            }
            this.parentLogs.add(event);
        }
    }

    public void addCustomRecord(final String key, final String value) {
    	if(key != null && value != null) {
    		if(customRecords == null) {
    			customRecords = new HashSet<>();
    		}
    		final IdmAuditLogCustom logAttr = new IdmAuditLogCustom();
            logAttr.setKey(key);
            logAttr.setValue(value);
            logAttr.setTimestamp(new Date().getTime());
    		customRecords.add(logAttr);
    	}
    }

    public void addAttributeAsJson(final AuditAttributeName key, final Object o, final CustomJacksonMapper mapper) {
        if(mapper != null) {
            addCustomRecord(key.name(), mapper.mapToStringQuietly(o));
        }
    }

    /**
     * Adds an attribute
     * @param key - the key
     * @param value - the value
     * @return this
     */
    public void addAttribute(final AuditAttributeName key, final String value) {
        addCustomRecord(key.name(), value);
    }


    /**
     * Sets the description of this event
     * @param value
     * @return this
     */
    public void setAuditDescription(final String value) {
        addAttribute(AuditAttributeName.DESCRIPTION, value);
    }

    public void addWarning(final String warning) {
        addAttribute(AuditAttributeName.WARNING, warning);
    }

    public void setFailureReason(final ResponseCode code) {
        if(code != null) {
            setFailureReason(code.name());
        }
    }

    /**
     * Sets an Exception for this event
     * @param e
     * @return this
     */
    public void setException(final Throwable e) {
        addAttribute(AuditAttributeName.EXCEPTION, ExceptionUtils.getStackTrace(e));
    }

    /**
     * Sets the reason for success
     * @param reason
     * @return
     */
    public void setSuccessReason(final String reason) {
        addAttribute(AuditAttributeName.SUCCESS_REASON, reason);
    }

    public void setURL(final String url) {
        addAttribute(AuditAttributeName.URL, url);
    }

    /**
     * Sets the user id of who triggered this event
     * @param userId - the caller
     * @return this
     */
    public void setRequestorUserId(String userId) {
        setUserId(userId);
    }

    private void setResult(AuditResult result) {
        setResult((result != null) ? result.value() : null);
    }

    /**
     * Signals that this event failed
     * @return this
     */
    public void fail() {
        setResult(AuditResult.FAILURE);
    }

    /**
     * Signals that this event succeeded
     * @return this
     */
    public void succeed() {
        setResult(AuditResult.SUCCESS);
    }

    /**
     * Sets a 'target' user - against which this operations is being performed
     * @param userId
     * @return this
     */
    public void setTargetUser(final String userId, final String userPrincipal) {
        addTarget(userId, AuditTarget.USER.value(), userPrincipal);
    }

    /**
     * Sets a 'target' role - against which this operations is being performed
     * @param roleId
     * @return this
     */
    public void setTargetRole(final String roleId,final String rolePrincipal) {
        addTarget(roleId, AuditTarget.ROLE.value(),  rolePrincipal);
    }
    /**
     * Sets a 'target' policy - against which this operations is being performed
     * @param policyId
     * @return this
     */
    public void setTargetPolicy(final String policyId,final String policyPrincipal) {
        addTarget(policyId, AuditTarget.POLICY.value(),  policyPrincipal);
    }
    /**
     * Sets a 'target' group - against which this operations is being performed
     * @param groupId
     * @return this
     */
    public void setTargetGroup(final String groupId, final String groupPrincipal) {
        addTarget(groupId, AuditTarget.GROUP.value(), groupPrincipal);
    }

    /**
     * Sets a 'target' role attribute - against which this operations is being performed
     * @param attrId
     * @param attrName
     * @return this
     */
    public void setTargetRoleAttribute(final String attrId, final String attrName) {
        addTarget(attrId, AuditTarget.ROLE_ATTRIBUTE.value(), attrName);
    }

    /**
     * Sets a 'target' group attribute - against which this operations is being performed
     * @param attrId
     * @param attrName
     * @return this
     */
    public void setTargetGroupAttribute(final String attrId, final String attrName) {
        addTarget(attrId, AuditTarget.GROUP_ATTRIBUTE.value(), attrName);
    }
    /**
     * Sets a 'target' resource - against which this operations is being performed
     * @param resourceId
     * @return this
     */
    public void setTargetResource(final String resourceId, final String resourcePrincipal) {
        addTarget(resourceId, AuditTarget.RESOURCE.value(), resourcePrincipal);
    }

    /**
     * Sets a 'target' managed system - against which this operations is being performed
     * @param managedSysId
     * @return this
     */
    public void setTargetManagedSys(final String managedSysId, final String managedSysPrincipal) {
        addTarget(managedSysId, AuditTarget.MANAGED_SYS.value(), managedSysPrincipal);
    }
    /**
     * Sets a 'target' org - against which this operations is being performed
     * @param orgId
     * @return this
     */
    public void setTargetOrg(final String orgId,final String orgPrincipal) {
        addTarget(orgId, AuditTarget.ORG.value(),  orgPrincipal);
    }
    /**
     * Sets a 'target' task - against which this operations is being performed
     * @param taskId
     * @return this
     */
    public void setTargetTask(final String taskId, final String taskPrincipal) {
        addTarget(taskId, AuditTarget.TASK.value(),  taskPrincipal);
    }
    /**
     * Sets the principal of who triggered this event
     * @param principal - the caller
     * @return this
     */
    public void setRequestorPrincipal(String principal) {
        setPrincipal(principal);
    }
    /**
     * Convenience method for Web Service calls to set caller information
     * @param baseObject
     * @return this
     */
    public void setBaseObject(final BaseObject baseObject) {
        setClientIP(baseObject.getRequestClientIP());
        setSessionID(baseObject.getRequestorSessionID());
        setRequestorPrincipal(baseObject.getRequestorLogin());
        setRequestorUserId(baseObject.getRequestorUserId());
    }

    public void setFailureReason(final String value) {
        addAttribute(AuditAttributeName.FAILURE_REASON, value);
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
		result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
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
		
		if (correlationId == null) {
			if (other.correlationId != null)
				return false;
		} else if (!correlationId.equals(other.correlationId))
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
