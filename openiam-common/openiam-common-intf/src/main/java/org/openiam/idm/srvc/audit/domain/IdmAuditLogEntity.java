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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

@Entity
@Table(name = "OPENIAM_LOG")
@DozerDTOCorrespondence(IdmAuditLog.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
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
    
    @Column(name="HASH", length=100)
    private String hash;
    
    @Column(name="SESSION_ID", length=100)
    private String sessionID;
    
    @Column(name="CORRELATION_ID", length=32)
    private String coorelationId;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    private Set<IdmAuditLogCustomEntity> customRecords = new HashSet<IdmAuditLogCustomEntity>();
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    private Set<AuditLogTargetEntity> targets;
    
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "OPENIAM_LOG_LOG_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "OPENIAM_MEMBER_LOG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "OPENIAM_LOG_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<IdmAuditLogEntity> parentLogs = new HashSet<IdmAuditLogEntity>();

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "OPENIAM_LOG_LOG_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "OPENIAM_LOG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "OPENIAM_MEMBER_LOG_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<IdmAuditLogEntity> childLogs = new HashSet<IdmAuditLogEntity>();
    
    public void addChild(final IdmAuditLogEntity entity) {
    	if(entity != null) {
    		this.childLogs.add(entity);
    	}
    }

    public void addParent(final IdmAuditLogEntity entity) {
        if(entity != null) {
            this.parentLogs.add(entity);
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
    		entity.setLog(this);
            entity.setTimestamp(new Date().getTime());
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

	public Set<AuditLogTargetEntity> getTargets() {
		return targets;
	}

	public void setTargets(Set<AuditLogTargetEntity> targets) {
		this.targets = targets;
	}
	
	public Set<IdmAuditLogEntity> getParentLogs() {
		return parentLogs;
	}

	public void setParentLogs(Set<IdmAuditLogEntity> parentLogs) {
		this.parentLogs = parentLogs;
	}

	public void addTarget(final String targetId, final String targetType) {
		if(targetId != null && targetType != null) {
			if(this.targets == null) {
				this.targets = new HashSet<AuditLogTargetEntity>();
			}
			final AuditLogTargetEntity target = new AuditLogTargetEntity();
			target.setTargetId(targetId);
			target.setTargetType(targetType);
			target.setLog(this);
			this.targets.add(target);
		}
	}


	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, result, source, timestamp, userId, sessionID, managedSysId, coorelationId);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdmAuditLogEntity that = (IdmAuditLogEntity) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (clientIP != null ? !clientIP.equals(that.clientIP) : that.clientIP != null) return false;
        if (coorelationId != null ? !coorelationId.equals(that.coorelationId) : that.coorelationId != null)
            return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (nodeIP != null ? !nodeIP.equals(that.nodeIP) : that.nodeIP != null) return false;
        if (principal != null ? !principal.equals(that.principal) : that.principal != null) return false;
        if (sessionID != null ? !sessionID.equals(that.sessionID) : that.sessionID != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (principal != null ? principal.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (clientIP != null ? clientIP.hashCode() : 0);
        result = 31 * result + (nodeIP != null ? nodeIP.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (sessionID != null ? sessionID.hashCode() : 0);
        result = 31 * result + (coorelationId != null ? coorelationId.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("IdmAuditLogEntity [id=%s, userId=%s, principal=%s, timestamp=%s, source=%s, clientIP=%s, nodeIP=%s, action=%s, result=%s, hash=%s]",
						id, userId, principal, timestamp, source, clientIP,
						nodeIP, action, result, hash);
	}


    
}