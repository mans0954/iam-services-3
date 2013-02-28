package org.openiam.idm.srvc.audit.dto;

// Generated Nov 30, 2007 3:01:45 AM by Hibernate Tools 3.2.0.b11

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * DTO object that is used log and retrieve audit information
 * Refactoring 6.12.2012
 * @author zaporozhec 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdmAuditLog", propOrder = { "logId", "objectTypeId",
        "actionId", "actionStatus", "reason", "reasonDetail", "actionDatetime",
        "objectName", "resourceName", "userId", "domainId", "principal",
        "host", "clientId", "reqUrl", "requestId", "sessionId",
        "attributesChanges", "objectId", "linkedLogId", "linkSequence",
        "logHash", "srcSystemId", "targetSystemId", "nodeIP", "customRecords" })
@DozerDTOCorrespondence(IdmAuditLogEntity.class)
public class IdmAuditLog implements java.io.Serializable {

    private String logId;
    private String objectTypeId;
    private String objectId;
    private String actionId;
    private String actionStatus;
    private String reason;
    private String reasonDetail;
    @XmlSchemaType(name = "dateTime")
    private Date actionDatetime;
    private String objectName;
    private String resourceName;
    private String userId;
    private String domainId;
    private String principal;
    /* IP or host name of the client machine */
    private String host;
    /* IP or host name of the node which sent the request to the IAM server */
    private String nodeIP;

    private String clientId;
    private String reqUrl;
    private String attributesChanges;

    private String linkedLogId;
    private Integer linkSequence = 0;
    private String logHash;
    private String requestId;
    private String sessionId;

    private String srcSystemId;
    private String targetSystemId;

    private List<IdmAuditLogCustom> customRecords = new ArrayList<IdmAuditLogCustom>(
            0);

    // private Set<CustomIdmAuditLog> customAttributes = new Has

    public IdmAuditLog() {
    }

    public IdmAuditLog(String objectTypeId, String actionId,
            String actionStatus, String reason, String domainId, String userId,
            String principal, String linkedLogId, String clientId) {
        this.objectTypeId = objectTypeId;
        this.actionId = actionId;
        this.actionStatus = actionStatus;
        this.reason = reason;
        this.domainId = domainId;
        this.userId = userId;
        this.principal = principal;
        this.linkedLogId = linkedLogId;
        this.clientId = clientId;
        this.actionDatetime = new Date(System.currentTimeMillis());
    }

    public IdmAuditLogCustom getCustomRecord(int dislpayOrder,
            CustomIdmAuditLogType type) {
        for (IdmAuditLogCustom ialc : this.customRecords) {
            if (dislpayOrder == ialc.getDispayOrder()
                    && type.equals(ialc.getType())) {
                return ialc;
            }
        }
        return null;
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
        updateCustomRecord(attrName, attrValue, 1, CustomIdmAuditLogType.ATTRIB);

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

    public void updateCustomRecord(String name, String value, int displayOrder,
            CustomIdmAuditLogType type) {
        if (type == null)
            return;
        boolean isExist = false;
        for (IdmAuditLogCustom ialcEntity : customRecords) {
            if (type.equals(ialcEntity.getType())
                    && displayOrder == ialcEntity.getDispayOrder()) {
                isExist = true;
                ialcEntity.setCustomValue(value);
                ialcEntity.setCustomName(name);
                break;
            }
        }
        if (!isExist) {
            IdmAuditLogCustom ialcEntity = new IdmAuditLogCustom();
            ialcEntity.setType(type);
            ialcEntity.setCustomName(name);
            ialcEntity.setCustomValue(value);
            ialcEntity.setDispayOrder(displayOrder);
            ialcEntity.setLogId(this.logId);
            customRecords.add(ialcEntity);
        }
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

    public String getAttributesChanges() {
        return this.attributesChanges;
    }

    public void setAttributesChanges(String attributesChanges) {
        this.attributesChanges = attributesChanges;
    }

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

    public List<IdmAuditLogCustom> getCustomRecords() {
        return customRecords;
    }

    public void setCustomRecords(List<IdmAuditLogCustom> customRecords) {
        this.customRecords = customRecords;
    }

}
