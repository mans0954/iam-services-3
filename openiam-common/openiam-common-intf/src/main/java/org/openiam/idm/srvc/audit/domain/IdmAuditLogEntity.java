package org.openiam.idm.srvc.audit.domain;

// Generated Nov 30, 2007 3:01:45 AM by Hibernate Tools 3.2.0.b11

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

/**
 * DTO object that is used log and retrieve audit information
 * Refactoring 6.12.2012
 * @author zaporozhec 
 */
@Entity
@Table(name = "IDM_AUDIT_LOG")
@DozerDTOCorrespondence(IdmAuditLog.class)
public class IdmAuditLogEntity implements java.io.Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "LOG_ID", length = 32)
    private String logId;

    @Column(name = "OBJECT_TYPE_ID", length = 20)
    private String objectTypeId;

    @Column(name = "OBJECT_ID", length = 32)
    private String objectId;

    @Column(name = "ACTION_ID", length = 50)
    private String actionId;

    @Column(name = "ACTION_STATUS", length = 32)
    private String actionStatus;

    @Column(name = "REASON", length = 1000)
    private String reason;

    @Column(name = "REASON_DETAIL")
    private String reasonDetail;

    @Column(name = "ACTION_DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDatetime;

    @Column(name = "OBJECT_NAME", length = 255)
    private String objectName;

    @Column(name = "RESOURCE_NAME", length = 255)
    private String resourceName;

    @Column(name = "USER_ID", length = 32)
    private String userId;

    @Column(name = "SERVICE_ID", length = 20)
    private String domainId;

    @Column(name = "LOGIN_ID", length = 320)
    private String principal;

    @Column(name = "HOST", length = 100)
    /* IP or host name of the client machine */
    private String host;

    @Column(name = "NODE_IP", length = 60)
    /* IP or host name of the node which sent the request to the IAM server */
    private String nodeIP;

    @Column(name = "CLIENT_ID", length = 20)
    private String clientId;

    @Column(name = "REQ_URL", length = 255)
    private String reqUrl;

    @Column(name = "LINKED_LOG_ID", length = 40)
    private String linkedLogId;

    @Column(name = "LINK_SEQUENCE")
    private Integer linkSequence = 0;

    @Column(name = "LOG_HASH", length = 80)
    private String logHash;

    @Column(name = "REQUEST_ID", length = 40)
    private String requestId;

    @Column(name = "SESSION_ID", length = 40)
    private String sessionId;

    @Column(name = "SRC_SYSTEM_ID", length = 32)
    private String srcSystemId;

    @Column(name = "TARGET_SYSTEM_ID", length = 40)
    private String targetSystemId;

    @Column(name = "LOG_ID", length = 32)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOG_ID", insertable = false, updatable = false)
    private List<IdmAuditLogCustomEntity> customRecords = new ArrayList<IdmAuditLogCustomEntity>(
            0);

    /**
     * @return the customRecords
     */
    public List<IdmAuditLogCustomEntity> getCustomRecords() {
        return customRecords;
    }

    /**
     * @param customRecords the customRecords to set
     */
    public void setCustomRecords(List<IdmAuditLogCustomEntity> customRecords) {
        this.customRecords = customRecords;
    }

    /**
     * @param linkSequence the linkSequence to set
     */
    public void setLinkSequence(Integer linkSequence) {
        this.linkSequence = linkSequence;
    }

    public IdmAuditLogEntity() {
    }

    public IdmAuditLogEntity(String objectTypeId, String actionId,
            String actionStatus, String reason, String domainId, String userId,
            String principal, String linkedLogId, String clientId) {

        this.objectTypeId = objectTypeId;
        this.actionId = actionId;
        this.actionStatus = actionStatus;
        this.reason = reason;
        this.domainId = domainId;
        this.userId = userId;
        this.principal = principal;
        this.actionDatetime = new java.util.Date(System.currentTimeMillis());
        this.userId = userId;
        this.clientId = clientId;
    }

    /**
     * Populates the attributes that used when starting or ending synchronization
     */
    public void setSynchAttributes(String objectTypeId, String objectId,
            String actionId, String userId, String sessionId) {
        this.objectTypeId = objectTypeId;
        this.objectId = objectId;
        this.actionId = actionId;
        this.actionDatetime = new Date(System.currentTimeMillis());
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public void setSynchUserAttributes(String objectTypeId, String objectId,
            String actionId, String actionStatus, String userId,
            String principal, String requestId, String reason,
            String sessionId, String attrName, String attrValue) {

        this.objectTypeId = objectTypeId;
        this.objectId = objectId;
        this.actionId = actionId;
        this.actionDatetime = new Date(System.currentTimeMillis());
        this.actionStatus = actionStatus;
        this.userId = userId;
        this.sessionId = sessionId;
        this.principal = principal;
        this.requestId = requestId;
        this.reason = reason;
        this.updateCustomRecord(attrName, attrValue, 1,
                CustomIdmAuditLogType.ATTRIB);

    }

    public void updateCustomRecord(String name, String value, int displayOrder,
            CustomIdmAuditLogType type) {
        if (type == null)
            return;
        boolean isExist = false;
        for (IdmAuditLogCustomEntity ialcEntity : customRecords) {
            if (type.equals(ialcEntity.getType())
                    && displayOrder == ialcEntity.getDispayOrder()) {
                isExist = true;
                ialcEntity.setCustomValue(value);
                ialcEntity.setCustomName(name);
                break;
            }
        }
        if (!isExist) {
            IdmAuditLogCustomEntity ialcEntity = new IdmAuditLogCustomEntity();
            ialcEntity.setType(type);
            ialcEntity.setCustomName(name);
            ialcEntity.setCustomValue(value);
            ialcEntity.setDispayOrder(displayOrder);
            ialcEntity.setLogId(this.logId);
            customRecords.add(ialcEntity);
        }
    }

    public void updateSynchAttributes(String actionStatus, String reason,
            String reasonDetail) {
        this.actionStatus = actionStatus;
        this.reason = reason;
        this.reasonDetail = reasonDetail;

    }

    public String getLinkedLogId() {
        return linkedLogId;
    }

    public void setLinkedLogId(String linkedLogId) {
        this.linkedLogId = linkedLogId;
    }

    public int getLinkSequence() {
        return linkSequence;
    }

    public void setLinkSequence(int linkSequence) {
        this.linkSequence = linkSequence;
    }

    public String getLogHash() {
        return logHash;
    }

    public void setLogHash(String logHash) {
        this.logHash = logHash;
    }

    public String getLogId() {
        return this.logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getObjectTypeId() {
        return this.objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getActionId() {
        return this.actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionStatus() {
        return this.actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonDetail() {
        return this.reasonDetail;
    }

    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
    }

    public Date getActionDatetime() {
        return this.actionDatetime;
    }

    public void setActionDatetime(Date actionDatetime) {
        this.actionDatetime = actionDatetime;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getReqUrl() {
        return this.reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    // public String getAttributesChanges() {
    // return this.attributesChanges;
    // }
    //
    // public void setAttributesChanges(String attributesChanges) {
    // this.attributesChanges = attributesChanges;
    // }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSrcSystemId() {
        return srcSystemId;
    }

    public void setSrcSystemId(String srcSystemId) {
        this.srcSystemId = srcSystemId;
    }

    public String getTargetSystemId() {
        return targetSystemId;
    }

    public void setTargetSystemId(String targetSystemId) {
        this.targetSystemId = targetSystemId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

}
