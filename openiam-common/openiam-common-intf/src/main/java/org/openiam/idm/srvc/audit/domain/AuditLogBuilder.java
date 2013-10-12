package org.openiam.idm.srvc.audit.domain;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.AuditLogBuilderDto;

import java.io.Serializable;
import java.util.Date;

@DozerDTOCorrespondence(AuditLogBuilderDto.class)
public class AuditLogBuilder implements Serializable {
	
	private IdmAuditLogEntity entity;
	
	public AuditLogBuilder() {
		entity = new IdmAuditLogEntity();
		entity.setTimestamp(new Date());
	}

	/**
	 * Sets the user id of who triggered this event
	 * @param userId - the caller
	 * @return this
	 */
	public AuditLogBuilder setRequestorUserId(String userId) {
		entity.setUserId(userId);
		return this;
	}
	
	/**
	 * Sets the principal of who triggered this event
	 * @param principal - the caller
	 * @return this
	 */
	public AuditLogBuilder setRequestorPrincipal(String principal) {
		entity.setPrincipal(principal);
		return this;
	}

	/**
	 * Sets where this event came from
	 * @param source
	 * @return this
	 */
	public AuditLogBuilder setSource(AuditSource source) {
		entity.setSource((source!=null)?source.value():AuditSource.ESB.value());
		return this;
	}

	/**
	 * Sets the IP address of who made this call
	 * @param clientIP
	 * @return this
	 */
	public AuditLogBuilder setClientIP(String clientIP) {
		entity.setClientIP(clientIP);
		return this;
	}

	/**
	 * Sets the action that this event represents
	 * @param action
	 * @return this
	 */
	public AuditLogBuilder setAction(AuditAction action) {
		entity.setAction((action!=null)?action.value():null);
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
		entity.setResult((result!=null)?result.value():null);
		return this;
	}

	/**
	 * Sets a 'target' user - against which this operations is being performed
	 * @param userId
	 * @return this
	 */
	public AuditLogBuilder setTargetUser(final String userId) {
		entity.addTarget(userId, "USER");
		return this;
	}
	
	/**
	 * Sets a 'target' role - against which this operations is being performed
	 * @param roleId
	 * @return this
	 */
	public AuditLogBuilder setTargetRole(final String roleId) {
		entity.addTarget(roleId, "ROLE");
		return this;
	}
	
	/**
	 * Sets a 'target' group - against which this operations is being performed
	 * @param groupId
	 * @return this
	 */
	public AuditLogBuilder setTargetGroup(final String groupId) {
		entity.addTarget(groupId, "GROUP");
		return this;
	}
	
	/**
	 * Sets a 'target' resource - against which this operations is being performed
	 * @param resourceId
	 * @return this
	 */
    public AuditLogBuilder setTargetResource(final String resourceId) {
    	entity.addTarget(resourceId, "RESOURCE");
        return this;
    }

    /**
     * Sets a 'target' managed system - against which this operations is being performed
     * @param managedSysId
     * @return this
     */
    public AuditLogBuilder setTargetManagedSys(final String managedSysId) {
    	entity.addTarget(managedSysId, "MANAGED_SYS");
        return this;
    }

    /**
     * Sets the managed system for which this event applies for
     * @param managedSysId
     * @return this
     */
	public AuditLogBuilder setManagedSysId(String managedSysId) {
		entity.setManagedSysId(managedSysId);
		return this;
	}
	
	/**
	 * Sets the session ID for this event
	 * @param sessionID
	 * @return this
	 */
	public AuditLogBuilder setSessionID(final String sessionID) {
		entity.setSessionID(sessionID);
		return this;
	}
	
	/**
	 * Adds a child builder
	 * @param builder
	 * @return this
	 */
	public AuditLogBuilder addChild(final AuditLogBuilder builder) {
		if(builder != null) {
			entity.addChild(builder.getEntity());
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
		entity.addCustomRecord(key.name(), value);
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
	public IdmAuditLogEntity getEntity() {
		return entity;
	}
	
	/**
	 * DO NOT CALL THIS METHOD!  Used <b>only</b> by Dozer!!!!
	 * @param entity
	 */
	public void setEntity(final IdmAuditLogEntity entity) {
		this.entity = entity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuditLogBuilder [entity=%s]", entity);
	}


}
