package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproverAssociation", propOrder = {
        "approverAssocId",
        "requestType",
        "action",
        "associationObjId",
        "approverUserId",
        "approverName",
        "associationType",
        "approverLevel",
        "selected",
        "notifyUserOnApprove",
        "notifyUserOnReject",
        "approveNotificationUserType",
        "rejectNotificationUserType",
        "approverRoleId",
        "applyDelegationFilter"
})
public class ApproverAssociation implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6664731921635130368L;
    private String approverAssocId;
    private String requestType;
    private String action;
    private String associationObjId;
    private String approverUserId;

    private String approverRoleId;
    private Integer applyDelegationFilter = new Integer(0);

    private String approverName;
    private String associationType;
    private Integer approverLevel;


    /* Users to notify based on approval or rejection */
    private String notifyUserOnApprove;
    private String notifyUserOnReject;

    /* type of user that we are sending a notification to - User, Supervisor, Target User */
    private String approveNotificationUserType;
    private String rejectNotificationUserType;


    protected Boolean selected = new Boolean(false);


    public ApproverAssociation() {
    }


    public String getApproverAssocId() {
        return approverAssocId;
    }


    public void setApproverAssocId(String approverAssocId) {
        this.approverAssocId = approverAssocId;
    }


    public String getRequestType() {
        return requestType;
    }


    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    public String getAssociationObjId() {
        return associationObjId;
    }


    public void setAssociationObjId(String associationObjId) {
        this.associationObjId = associationObjId;
    }


    public String getApproverUserId() {
        return approverUserId;
    }


    public void setApproverUserId(String approverUserId) {
        this.approverUserId = approverUserId;
    }


    public String getAssociationType() {
        return associationType;
    }


    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }


    public Integer getApproverLevel() {
        return approverLevel;
    }


    public void setApproverLevel(Integer approverLevel) {
        this.approverLevel = approverLevel;
    }


    public Boolean getSelected() {
        return selected;
    }


    public void setSelected(Boolean selected) {
        this.selected = selected;
    }


    public String getApproverName() {
        return approverName;
    }


    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getNotifyUserOnApprove() {
        return notifyUserOnApprove;
    }

    public void setNotifyUserOnApprove(String notifyUserOnApprove) {
        this.notifyUserOnApprove = notifyUserOnApprove;
    }

    public String getNotifyUserOnReject() {
        return notifyUserOnReject;
    }

    public void setNotifyUserOnReject(String notifyUserOnReject) {
        this.notifyUserOnReject = notifyUserOnReject;
    }

    public String getApproveNotificationUserType() {
        return approveNotificationUserType;
    }

    public void setApproveNotificationUserType(String approveNotificationUserType) {
        this.approveNotificationUserType = approveNotificationUserType;
    }

    public String getRejectNotificationUserType() {
        return rejectNotificationUserType;
    }

    public void setRejectNotificationUserType(String rejectNotificationUserType) {
        this.rejectNotificationUserType = rejectNotificationUserType;
    }

    public String getApproverRoleId() {
        return approverRoleId;
    }

    public void setApproverRoleId(String approverRoleId) {
        this.approverRoleId = approverRoleId;
    }

    public Integer getApplyDelegationFilter() {
        return applyDelegationFilter;
    }

    public void setApplyDelegationFilter(Integer applyDelegationFilter) {
        this.applyDelegationFilter = applyDelegationFilter;
    }
}
