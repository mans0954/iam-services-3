package org.openiam.idm.srvc.audit.domain;

import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditSource;

import java.io.Serializable;
import java.util.Date;

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

	/*
	public AuditLogBuilder setTimestamp(Date timestamp) {
		entity.setTimestamp(timestamp);
		return this;
	}
	*/

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
	
	public AuditLogBuilder addAttribute(final AuditAttributeName key, final String value) {
		entity.addCustomRecord(key.name(), value);
		return this;
	}
	
	public IdmAuditLogEntity getEntity() {
		return entity;
	}


    @Override
    public String toString() {
        return String.format("%s{entity=%s}", this.getClass().getSimpleName(), entity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogBuilder builder = (AuditLogBuilder) o;

        if (entity != null ? !entity.equals(builder.entity) : builder.entity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return entity != null ? entity.hashCode() : 0;
    }
}
