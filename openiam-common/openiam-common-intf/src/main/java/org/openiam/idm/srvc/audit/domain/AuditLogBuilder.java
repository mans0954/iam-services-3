package org.openiam.idm.srvc.audit.domain;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openiam.base.BaseObject;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.AuditLogBuilderDto;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.util.CustomJacksonMapper;

import java.io.Serializable;
import java.util.Date;

@DozerDTOCorrespondence(AuditLogBuilderDto.class)
public class AuditLogBuilder implements Serializable {
	
	private IdmAuditLog event;
	
	public AuditLogBuilder() {
        event = new IdmAuditLog();
        event.setTimestamp(new Date());
	}
    public AuditLogBuilder(IdmAuditLog auditLog) {
        event = auditLog;
    }
	/**
	 * Sets the user id of who triggered this event
	 * @param userId - the caller
	 * @return this
	 */
	public AuditLogBuilder setRequestorUserId(String userId) {
        event.setUserId(userId);
		return this;
	}
	
	/**
	 * Sets the principal of who triggered this event
	 * @param principal - the caller
	 * @return this
	 */
	public AuditLogBuilder setRequestorPrincipal(String principal) {
        event.setPrincipal(principal);
		return this;
	}

	/**
	 * Sets where this event came from
	 * @param source
	 * @return this
	 */
	public AuditLogBuilder setSource(AuditSource source) {
        event.setSource((source!=null)?source.value():AuditSource.ESB.value());
		return this;
	}
    public AuditLogBuilder setSource(String source) {
        event.setSource(source);
        return this;
    }
	/**
	 * Sets the IP address of who made this call
	 * @param clientIP
	 * @return this
	 */
	public AuditLogBuilder setClientIP(String clientIP) {
        event.setClientIP(clientIP);
		return this;
	}

    /**
     * Sry correlation source id
     * @param correlationId
     * @return
     */
    public AuditLogBuilder setCorrelationId(String correlationId) {
        event.setCoorelationId(correlationId);
        return this;
    }

	/**
	 * Sets the action that this event represents
	 * @param action
	 * @return this
	 */
	public AuditLogBuilder setAction(AuditAction action) {
        event.setAction((action!=null)?action.value():null);
		return this;
	}
	
	/**
	 * Signals that this event failed
	 * @return this
	 */
	public AuditLogBuilder fail() {
		return setResult(AuditResult.FAILURE);
	}
	
	/**
	 * Signals that this event succeeded
	 * @return this
	 */
	public AuditLogBuilder succeed() {
		return setResult(AuditResult.SUCCESS);
	}

	private AuditLogBuilder setResult(AuditResult result) {
        event.setResult((result!=null)?result.value():null);
		return this;
	}

	/**
	 * Sets a 'target' user - against which this operations is being performed
	 * @param userId
	 * @return this
	 */
	public AuditLogBuilder setTargetUser(final String userId) {
        event.addTarget(userId, "USER");
		return this;
	}
	
	/**
	 * Sets a 'target' role - against which this operations is being performed
	 * @param roleId
	 * @return this
	 */
	public AuditLogBuilder setTargetRole(final String roleId) {
        event.addTarget(roleId, "ROLE");
		return this;
	}
	
	/**
	 * Sets a 'target' group - against which this operations is being performed
	 * @param groupId
	 * @return this
	 */
	public AuditLogBuilder setTargetGroup(final String groupId) {
        event.addTarget(groupId, "GROUP");
		return this;
	}
	
	/**
	 * Sets a 'target' resource - against which this operations is being performed
	 * @param resourceId
	 * @return this
	 */
    public AuditLogBuilder setTargetResource(final String resourceId) {
        event.addTarget(resourceId, "RESOURCE");
        return this;
    }

    /**
     * Sets a 'target' managed system - against which this operations is being performed
     * @param managedSysId
     * @return this
     */
    public AuditLogBuilder setTargetManagedSys(final String managedSysId) {
        event.addTarget(managedSysId, "MANAGED_SYS");
        return this;
    }

    /**
     * Sets the managed system for which this event applies for
     * @param managedSysId
     * @return this
     */
	public AuditLogBuilder setManagedSysId(String managedSysId) {
        event.setManagedSysId(managedSysId);
		return this;
	}
	
	/**
	 * Sets the session ID for this event
	 * @param sessionID
	 * @return this
	 */
	public AuditLogBuilder setSessionID(final String sessionID) {
        event.setSessionID(sessionID);
		return this;
	}
	
	/**
	 * Add a child builder
	 * @param builder
	 * @return this
	 */
	public AuditLogBuilder addChild(final AuditLogBuilder builder) {
		if(builder != null) {
            event.addChild(builder.getEvent());
		}
		return this;
	}

    /**
     * Add a parent builder
     * @param builder
     * @return this
     */
    public AuditLogBuilder addParent(final AuditLogBuilder builder) {
        if(builder != null) {
            event.addParent(builder.getEvent());
        }
        return this;
    }
	public AuditLogBuilder addAttributeAsJson(final AuditAttributeName key, final Object o, final CustomJacksonMapper mapper) {
		if(mapper != null) {
			event.addCustomRecord(key.name(), mapper.mapToStringQuietly(o));
		}
		return this;
	}
	
	/**
	 * Adds an attribute
	 * @param key - the key
	 * @param value - the value
	 * @return this
	 */
	public AuditLogBuilder addAttribute(final AuditAttributeName key, final String value) {
        event.addCustomRecord(key.name(), value);
		return this;
	}

	/**
	 * Sets the description of this event
	 * @param value
	 * @return this
	 */
    public AuditLogBuilder setAuditDescription(final String value) {
        return addAttribute(AuditAttributeName.DESCRIPTION, value);
    }
    
    public AuditLogBuilder addWarning(final String warning) {
    	return addAttribute(AuditAttributeName.WARNING, warning);
    }
    
    public AuditLogBuilder setFailureReason(final ResponseCode code) {
    	if(code != null) {
    		setFailureReason(code.name());
    	}
    	return this;
    }
	
    /**
     * Sets a failure reason
     * @param value
     * @return this
     */
	public AuditLogBuilder setFailureReason(final String value) {
		return addAttribute(AuditAttributeName.FAILURE_REASON, value);
	}
	
	/**
	 * Sets an Exception for this event
	 * @param e
	 * @return this
	 */
	public AuditLogBuilder setException(final Throwable e) {
		return addAttribute(AuditAttributeName.EXCEPTION, ExceptionUtils.getStackTrace(e));
	}
	
	/**
	 * Sets the reason for success
	 * @param reason
	 * @return
	 */
	public AuditLogBuilder setSuccessReason(final String reason) {
		return addAttribute(AuditAttributeName.SUCCESS_REASON, reason);
	}
	
	public AuditLogBuilder setURL(final String url) {
		return addAttribute(AuditAttributeName.URL, url);
	}
	
	/**
	 * Convenience method for Web Service calls to set caller information
	 * @param baseObject
	 * @return this
	 */
	public AuditLogBuilder setBaseObject(final BaseObject baseObject) {
		setClientIP(baseObject.getRequestClientIP());
		setSessionID(baseObject.getRequestorSessionID());
		setRequestorPrincipal(baseObject.getRequestorLogin());
		setRequestorUserId(baseObject.getRequestorUserId());
		return this;
	}
	
	/**
	 * DO NOT CALL THIS METHOD!  Userd <b>only</b> by the AuditLogService to enqueue() events into JMS
	 * @return
	 */
	public IdmAuditLog getEvent() {
		return event;
	}
	
	/**
	 * DO NOT CALL THIS METHOD!  Used <b>only</b> by Dozer!!!!
	 * @param event
	 */
	public void setEvent(final IdmAuditLog event) {
		this.event = event;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
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
		AuditLogBuilder other = (AuditLogBuilder) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuditLogBuilder [entity=%s]", event);
	}


}
