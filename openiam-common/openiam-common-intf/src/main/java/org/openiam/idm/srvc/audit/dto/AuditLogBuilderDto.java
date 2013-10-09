package org.openiam.idm.srvc.audit.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

@DozerDTOCorrespondence(AuditLogBuilder.class)
public class AuditLogBuilderDto implements Serializable {

	private IdmAuditLog entity;
	
	public AuditLogBuilderDto(final IdmAuditLog entity) {
		this.entity = entity;
	}
	
	public AuditLogBuilderDto() {
		entity = new IdmAuditLog();
		entity.setTimestamp(new Date());
	}
	
	public AuditLogBuilderDto setRequestorUserId(String userId) {
		entity.setUserId(userId);
		return this;
	}
	
	public AuditLogBuilderDto setRequestorPrincipal(String principal) {
		entity.setPrincipal(principal);
		return this;
	}

	public AuditLogBuilderDto setSource(AuditSource source) {
		entity.setSource(source.value());
		return this;
	}

	public AuditLogBuilderDto setClientIP(String clientIP) {
		entity.setClientIP(clientIP);
		return this;
	}

	public AuditLogBuilderDto setAction(AuditAction action) {
		entity.setAction((action!=null)?action.value():null);
		return this;
	}

	public AuditLogBuilderDto setResult(AuditResult result) {
		entity.setResult((result!=null)?result.value():null);
		return this;
	}

	public AuditLogBuilderDto setTargetUser(final String userId) {
		entity.addTarget(userId, "USER");
		return this;
	}
	
	public AuditLogBuilderDto setTargetRole(final String roleId) {
		entity.addTarget(roleId, "ROLE");
		return this;
	}
	
	public AuditLogBuilderDto setTargetGroup(final String groupId) {
		entity.addTarget(groupId, "GROUP");
		return this;
	}
	public AuditLogBuilderDto setTargetResource(final String resourceId) {
    	entity.addTarget(resourceId, "RESOURCE");
        return this;
    }
	
	public AuditLogBuilderDto setManagedSysId(String managedSysId) {
		entity.setManagedSysId(managedSysId);
		return this;
	}
	
	public AuditLogBuilderDto setSessionID(final String sessionID) {
		entity.setSessionID(sessionID);
		return this;
	}
	
	public AuditLogBuilderDto addChild(final AuditLogBuilderDto builder) {
		if(builder != null) {
			entity.addChild(builder.getEntity());
		}
		return this;
	}
	
	public AuditLogBuilderDto fail() {
		return setResult(AuditResult.FAILURE);
	}
	
	public AuditLogBuilderDto succeed() {
		return setResult(AuditResult.SUCCESS);
	}
	
	public AuditLogBuilderDto setException(final Throwable e) {
		return addAttribute(AuditAttributeName.EXCEPTION, ExceptionUtils.getStackTrace(e));
	}
	
	public AuditLogBuilderDto setFailureReason(final String reason) {
		return addAttribute(AuditAttributeName.FAILURE_REASON, reason);
	}
	
	public AuditLogBuilderDto addAttribute(final AuditAttributeName key, final String value) {
		entity.addCustomRecord(key.name(), value);
		return this;
	}
	
	public IdmAuditLog getEntity() {
		return entity;
	}
	
	/**
	 * DO NOT CALL THIS METHOD!  Used <b>only</b> by Dozer!!!!
	 * @param entity
	 */
	public void setEntity(final IdmAuditLog entity) {
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
		AuditLogBuilderDto other = (AuditLogBuilderDto) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuditLogBuilderDto [entity=%s]", entity);
	}



}
