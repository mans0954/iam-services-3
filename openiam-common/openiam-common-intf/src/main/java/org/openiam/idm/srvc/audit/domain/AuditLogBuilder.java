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
	
	public AuditLogBuilder(final IdmAuditLogEntity entity) {
		this.entity = entity;
	}
	
	public AuditLogBuilder() {
		entity = new IdmAuditLogEntity();
		entity.setTimestamp(new Date());
	}

	public AuditLogBuilder setSourceUserId(String userId) {
		entity.setUserId(userId);
		return this;
	}

	public AuditLogBuilder setSource(AuditSource source) {
		entity.setSource((source!=null)?source.value():AuditSource.ESB.value());
		return this;
	}

	public AuditLogBuilder setClientIP(String clientIP) {
		entity.setClientIP(clientIP);
		return this;
	}

	public AuditLogBuilder setAction(AuditAction action) {
		entity.setAction((action!=null)?action.value():null);
		return this;
	}
	
	public AuditLogBuilder fail() {
		return setResult(AuditResult.FAILURE);
	}
	
	public AuditLogBuilder succeed() {
		return setResult(AuditResult.SUCCESS);
	}

	public AuditLogBuilder setResult(AuditResult result) {
		entity.setResult((result!=null)?result.value():null);
		return this;
	}

	public AuditLogBuilder setTargetUser(final String userId) {
		entity.setObjectType("USER");
		entity.setObjectID(userId);
		return this;
	}
	
	public AuditLogBuilder setTargetRole(final String roleId) {
		entity.setObjectType("ROLE");
		entity.setObjectID(roleId);
		return this;
	}
	
	public AuditLogBuilder setTargetGroup(final String groupId) {
		entity.setObjectType("GROUP");
		entity.setObjectID(groupId);
		return this;
	}
    public AuditLogBuilder setTargetResource(final String resourceId) {
        entity.setObjectType("RESOURCE");
        entity.setObjectID(resourceId);
        return this;
    }

	public AuditLogBuilder setSourcePrincipal(String principal) {
		entity.setPrincipal(principal);
		return this;
	}
	
	public AuditLogBuilder setManagedSysId(String managedSysId) {
		entity.setManagedSysId(managedSysId);
		return this;
	}
	
	public AuditLogBuilder setSessionID(final String sessionID) {
		entity.setSessionID(sessionID);
		return this;
	}
	
	public AuditLogBuilder addChild(final AuditLogBuilder builder) {
		if(builder != null) {
			entity.addChild(builder.getEntity());
		}
		return this;
	}
	
	public AuditLogBuilder addAttribute(final AuditAttributeName key, final String value) {
		entity.addCustomRecord(key.name(), value);
		return this;
	}

    public AuditLogBuilder setAuditDescription(final String value) {
        return addAttribute(AuditAttributeName.DESCRIPTION, value);
    }
	
	public AuditLogBuilder setFailureReason(final String value) {
		return addAttribute(AuditAttributeName.FAILURE_REASON, value);
	}
	
	public AuditLogBuilder setException(final Throwable e) {
		return addAttribute(AuditAttributeName.EXCEPTION, ExceptionUtils.getStackTrace(e));
	}
	
	public AuditLogBuilder setBaseObject(final BaseObject baseObject) {
		setClientIP(baseObject.getRequestClientIP());
		setSessionID(baseObject.getRequestorSessionID());
		setSourcePrincipal(baseObject.getRequestorLogin());
		setSourceUserId(baseObject.getRequestorUserId());
		return this;
	}
	
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
