package org.openiam.idm.srvc.audit.domain;

import java.util.Date;

public class AuditLogBuilder {
	
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

	/*
	public AuditLogBuilder setTimestamp(Date timestamp) {
		entity.setTimestamp(timestamp);
		return this;
	}
	*/

	public AuditLogBuilder setSource(String source) {
		entity.setSource(source);
		return this;
	}

	public AuditLogBuilder setClientIP(String clientIP) {
		entity.setClientIP(clientIP);
		return this;
	}

	public AuditLogBuilder setAction(String action) {
		entity.setAction(action);
		return this;
	}

	public AuditLogBuilder setResult(String result) {
		entity.setResult(result);
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
	
	/*
	public AuditLogBuilder setObjectID(String objectID) {
		entity.setObjectID(objectID);
		return this;
	}

	public AuditLogBuilder setObjectType(String objectType) {
		entity.setObjectType(objectType);
		return this;
	}
	*/
	
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
	
	public AuditLogBuilder addAttribute(final String key, final String value) {
		entity.addCustomRecord(key, value);
		return this;
	}
	
	public IdmAuditLogEntity getEntity() {
		return entity;
	}
}
