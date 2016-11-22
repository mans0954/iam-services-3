package org.openiam.idm.srvc.audit.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.BaseObject;
import org.openiam.base.domain.KeyEntity;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.NestedCollectionType;
import org.openiam.elasticsearch.annotation.SimpleElasticSearchJSONMapping;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author lbornova
 *
 *  This class is <b>NOT</b> meant to be stored in Hibernate
 *	The only reason for the Hibernate annotations is to support
 *  migrating customers who are still on 3.X of the product
 *  
 *  Customers using V4+ of the product should <b>never</b>
 *  store log records in the databse.
 */
@Entity
@Table(name = "OPENIAM_LOG")
@DozerDTOCorrespondence(IdmAuditLog.class)
@Cache(usage=CacheConcurrencyStrategy.NONE)
@Document(indexName = ESIndexName.AUDIT_LOG, type= ESIndexType.AUDIT_LOG)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AttributeOverride(name = "id", column = @Column(name = "OPENIAM_LOG_ID"))
@SimpleElasticSearchJSONMapping
public class IdmAuditLogEntity extends KeyEntity {
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="USER_ID", length=32)
    private String userId;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="PRINCIPAL", length=320)
    private String principal;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="MANAGED_SYS_ID", length=32)
    private String managedSysId;
    
	@Field(type = FieldType.Date, index = FieldIndex.not_analyzed, store= true)
    @Column(name="CREATED_DATETIME")
    private Date timestamp;

	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="SOURCE", length=50)
    private String source;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="CLIENT_IP", length=50)
    private String clientIP;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="NODE_ID", length=50)
    private String nodeIP;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="LOG_ACTION", length=50)
    private String action;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="RESULT", length=50)
    private String result;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="HASH", length=100)
    private String hash;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="SESSION_ID", length=100)
    private String sessionID;
    
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Column(name="CORRELATION_ID", length=32)
    private String correlationId;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	@Transient
	private String authProviderId;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	@Transient
	private String contentProviderId;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	@Transient
	private String uriPatternId;
	
	@Transient
	@Field(type = FieldType.Nested)
	private Map<String, String> attributes;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    //@Field(type = FieldType.Nested, index = FieldIndex.not_analyzed, store= true)
    //@NestedFieldType(IdmAuditLogCustomEntity.class)
    @Deprecated
    @JsonIgnore
    private Set<IdmAuditLogCustomEntity> customRecords = new LinkedHashSet<IdmAuditLogCustomEntity>();
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "log")
    @Field(type = FieldType.Nested, index = FieldIndex.not_analyzed, store= true, includeInParent=true)
    @NestedCollectionType(AuditLogTargetEntity.class)
    private Set<AuditLogTargetEntity> targets = new LinkedHashSet<AuditLogTargetEntity>();
    
    /*
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "OPENIAM_LOG_LOG_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "OPENIAM_MEMBER_LOG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "OPENIAM_LOG_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<IdmAuditLogEntity> parentLogs = new HashSet<IdmAuditLogEntity>();
    */
    
    /**
     * This is the reference to the parent
     * 
     * "null" EXPLICITLY means "no parent", becuase of
     * Spring Data ElasticSearch's limitation of not being able to do an
     * "Exists" query (or it's possible, that due to their sh*tty documentation,
     * I wasn't able to find a way to do it).
     * 
     * @Lev Bornovalov
     */
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @Transient
    private String parentId = "null";

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "OPENIAM_LOG_LOG_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "OPENIAM_LOG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "OPENIAM_MEMBER_LOG_ID")})
    @Fetch(FetchMode.SUBSELECT)
    //@Field(type = FieldType.Nested, index = FieldIndex.not_analyzed, store= true)
    @NestedCollectionType(IdmAuditLogEntity.class)
    private Set<IdmAuditLogEntity> childLogs = new HashSet<IdmAuditLogEntity>();
    
    public void addChild(final IdmAuditLogEntity entity) {
    	if(entity != null) {
            if(entity.getResult() == null) {
                entity.setResult(this.getResult());
            }
    		this.childLogs.add(entity);
    	}
    }

    /*
    public void addParent(final IdmAuditLogEntity entity) {
        if(entity != null) {
            this.parentLogs.add(entity);
        }
    }
    */
    
    /**
     * Deprecated.  Just call <code>put</code>
     * Just use put()
     * @param key
     * @param value
     */
    @Deprecated
    public void addCustomRecord(final String key, final String value) {
    	put(key, value);
    }
    
    /**
     * Deprecated.  Just call <code>put</code>
     * Just use put()
     * @param key
     * @param value
     */
    @Deprecated
    public void addCustomRecord(IdmAuditLogCustomEntity logCustomEntity) {
    	if(logCustomEntity != null) {
    		put(logCustomEntity.getKey(), logCustomEntity.getValue());
    	}
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

	@Deprecated
	public Set<IdmAuditLogCustomEntity> getCustomRecords() {
		return customRecords;
	}

	@Deprecated
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
	
	/*
	public Set<IdmAuditLogEntity> getParentLogs() {
		return parentLogs;
	}

	public void setParentLogs(Set<IdmAuditLogEntity> parentLogs) {
		this.parentLogs = parentLogs;
	}
	*/

	public void addTarget(final String targetId, final String targetType, final String principal) {
		if(targetId != null && targetType != null) {
			if(this.targets == null) {
				this.targets = new HashSet<>();
			}
			final AuditLogTargetEntity target = new AuditLogTargetEntity();
			target.setTargetId(targetId);
			target.setTargetType(targetType);
            target.setObjectPrincipal(principal);
			//target.setLog(this);
			this.targets.add(target);
		}
	}
	
	//@JsonAnyGetter
    public Map<String, String> getAttributes() {
        return attributes;
    }

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String concat() {
		return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", action, clientIP, principal, nodeIP, result, source, timestamp, userId, sessionID, managedSysId, correlationId);
	}
	
	public void addAttributeAsJson(final AuditAttributeName key, final Object o, final CustomJacksonMapper mapper) {
        if(mapper != null) {
        	put(key.name(), mapper.mapToStringQuietly(o));
        }
    }

    /**
     * use put()
     * 
     * Adds an attribute
     * @param key - the key
     * @param value - the value
     * @return this
     */
	@Deprecated
    public void addAttribute(final AuditAttributeName key, final String value) {
    	put(key.name(), value);
    }
    
    public void put(final String key, final String value) {
    	if(key != null && value != null) {
    		if(this.attributes == null) {
    			this.attributes = new HashMap<>();
    		}
    		this.attributes.put(key, value);
    	}
    }
    
    public String get(final String key) {
    	return (key != null && attributes != null) ? attributes.get(key) : null;
    }

    public void setTaskOwner(final String value) {
    	addAttribute(AuditAttributeName.TASK_OWNER, value);
    }
    
    public void setTaskClass(final Class<?> clazz) {
    	addAttribute(AuditAttributeName.TASK_CLASS, clazz.getCanonicalName());
    }
    
    public void setTargetClass(final Class<?> clazz) {
    	if(clazz != null) {
    		put(AuditAttributeName.TARGET_CLASS.name(), clazz.toString());
    	}
    }
    
    public void setTaskDescription(final String value) {
    	addAttribute(AuditAttributeName.TASK_DESCSRIPTION, value);
    }
    
    public void setTaskName(final String value) {
    	addAttribute(AuditAttributeName.TASK_NAME, value);
    }

    /**
     * Sets the description of this event
     * @param value
     * @return this
     */
    public void setAuditDescription(final String value) {
        addAttribute(AuditAttributeName.DESCRIPTION, value);
    }
    
    public void setActivitiTaskName(final String value) {
    	addAttribute(AuditAttributeName.ACTIVITI_TASK_NAME, value);
    }
    
    public void setEventName(final String value) {
    	addAttribute(AuditAttributeName.EVENT_NAME, value);
    }

    public void addWarning(final String warning) {
        addAttribute(AuditAttributeName.WARNING, warning);
    }

    @JsonIgnore
    public void setFailureReason(final ResponseCode code) {
        if(code != null) {
            setFailureReason(code.name());
        }
    }
    
    /**
     * Sorting by timestamp - DESC
     * @return
     */
    public Collection<IdmAuditLogEntity> getChildLogsSorted() {
        if(childLogs != null) {
            List<IdmAuditLogEntity> sortedItems = new ArrayList<IdmAuditLogEntity>(childLogs);
            Collections.sort(sortedItems, new Comparator<IdmAuditLogEntity>(){
                @Override
                public int compare(IdmAuditLogEntity o1, IdmAuditLogEntity o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            } );
            return sortedItems;
        }
        return childLogs;
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
    
    public void setGroovyScript(final String value) {
    	addAttribute(AuditAttributeName.GROOVY_SCRIPT, value);
    }

    /**
     * Sets the user id of who triggered this event
     * @param userId - the caller
     * @return this
     */
    public void setRequestorUserId(String userId) {
        setUserId(userId);
    }

    /**
     * Signals that this event failed
     * @return this
     */
    public void fail() {
        setResult(AuditResult.FAILURE.value());
    }

    /**
     * Signals that this event succeeded
     * @return this
     */
    public void succeed() {
        setResult(AuditResult.SUCCESS.value());
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

    public void setBaseRequest(BaseServiceRequest baseRequest){
        setClientIP(baseRequest.getRequestClientIP());
        setSessionID(baseRequest.getRequestorSessionID());
        setRequestorPrincipal(baseRequest.getRequestorLogin());
        setRequestorUserId(baseRequest.getRequesterId());
    }

    public void setFailureReason(final String value) {
        addAttribute(AuditAttributeName.FAILURE_REASON, value);
    }

    
	

	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public String getContentProviderId() {
		return contentProviderId;
	}

	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}

	public String getUriPatternId() {
		return uriPatternId;
	}

	public void setUriPatternId(String uriPatternId) {
		this.uriPatternId = uriPatternId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((clientIP == null) ? 0 : clientIP.hashCode());
		result = prime * result
				+ ((correlationId == null) ? 0 : correlationId.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((nodeIP == null) ? 0 : nodeIP.hashCode());
		result = prime * result
				+ ((principal == null) ? 0 : principal.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result
				+ ((sessionID == null) ? 0 : sessionID.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((authProviderId == null) ? 0 : authProviderId.hashCode());
		result = prime * result + ((contentProviderId == null) ? 0 : contentProviderId.hashCode());
		result = prime * result + ((uriPatternId == null) ? 0 : uriPatternId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdmAuditLogEntity other = (IdmAuditLogEntity) obj;
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
		if (correlationId == null) {
			if (other.correlationId != null)
				return false;
		} else if (!correlationId.equals(other.correlationId))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (nodeIP == null) {
			if (other.nodeIP != null)
				return false;
		} else if (!nodeIP.equals(other.nodeIP))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (sessionID == null) {
			if (other.sessionID != null)
				return false;
		} else if (!sessionID.equals(other.sessionID))
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
		
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		
		if (uriPatternId == null) {
			if (other.uriPatternId != null)
				return false;
		} else if (!uriPatternId.equals(other.uriPatternId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IdmAuditLogEntity [userId=" + userId + ", principal="
				+ principal + ", managedSysId=" + managedSysId + ", timestamp="
				+ timestamp + ", source=" + source + ", clientIP=" + clientIP
				+ ", nodeIP=" + nodeIP + ", action=" + action + ", result="
				+ result + ", hash=" + hash + ", sessionID=" + sessionID
				+ ", correlationId=" + correlationId + "]";
	}

   
}