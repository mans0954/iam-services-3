package org.openiam.idm.srvc.audit.domain;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private String correlationId;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    private Set<IdmAuditLogCustomEntity> customRecords = new LinkedHashSet<IdmAuditLogCustomEntity>();
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    private Set<AuditLogTargetEntity> targets = new LinkedHashSet<AuditLogTargetEntity>();
    
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
            if(entity.getResult() == null) {
                entity.setResult(this.getResult());
            }
    		this.childLogs.add(entity);
    	}
    }

    public void addParent(final IdmAuditLogEntity entity) {
        if(entity != null) {
            this.parentLogs.add(entity);
        }
    }
    public void addCustomRecord(IdmAuditLogCustomEntity logCustomEntity) {
    	if(logCustomEntity != null) {
    		if(customRecords == null) {
    			customRecords = new LinkedHashSet<>();
    		}
            logCustomEntity.setLog(this);
  		    customRecords.add(logCustomEntity);
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

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
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

	public void addTarget(final String targetId, final String targetType, final String principal) {
		if(targetId != null && targetType != null) {
			if(this.targets == null) {
				this.targets = new HashSet<>();
			}
			final AuditLogTargetEntity target = new AuditLogTargetEntity();
			target.setTargetId(targetId);
			target.setTargetType(targetType);
            target.setObjectPrincipal(principal);
			target.setLog(this);
			this.targets.add(target);
		}
	}


	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, result, source, timestamp, userId, sessionID, managedSysId, correlationId);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdmAuditLogEntity that = (IdmAuditLogEntity) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (clientIP != null ? !clientIP.equals(that.clientIP) : that.clientIP != null) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (nodeIP != null ? !nodeIP.equals(that.nodeIP) : that.nodeIP != null) return false;
        if (principal != null ? !principal.equals(that.principal) : that.principal != null) return false;
        if (result != null ? !result.equals(that.result) : that.result != null) return false;
        if (sessionID != null ? !sessionID.equals(that.sessionID) : that.sessionID != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
		return !(userId != null ? !userId.equals(that.userId) : that.userId != null);

	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((clientIP == null) ? 0 : clientIP.hashCode());
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((nodeIP == null) ? 0 : nodeIP.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        result = prime * result + ((managedSysId == null) ? 0 : managedSysId.hashCode());
        result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
        result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());

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