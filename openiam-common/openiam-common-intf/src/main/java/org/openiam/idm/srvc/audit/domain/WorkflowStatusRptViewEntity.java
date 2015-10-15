package org.openiam.idm.srvc.audit.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by anton on 11.10.15.
 */

@Entity
@Table(name = "WORKFLOW_STATUS_RPT_VIEW")
@Cache(usage= CacheConcurrencyStrategy.NONE)
public class WorkflowStatusRptViewEntity implements Serializable {

    @Id
    @Column(name = "OPENIAM_LOG_ID", length = 100)
    private String logId;

    @Column(name = "REQUESTER_ID", length = 100)
    private String requesterId;

    @Column(name = "REQUESTER_PRINCIPAL", length = 100)
    private String requesterPrincipal;

    @Column(name = "REQUEST_CREATE_DATE", length = 100)
    private String requestCreateDate;

    @Column(name = "REQUESTER_FIRST_NAME", length = 100)
    private String requesterFirstName;

    @Column(name = "REQUESTER_LAST_NAME", length = 100)
    private String requesterLastName;

    @Column(name = "LOG_ACTION", length = 100)
    private String logAction;

    @Column(name = "ASSOCIATION_TYPE", length = 100)
    private String associationType;

    @Column(name = "ASSOSIATION_ID", length = 100)
    private String associationId;

    @Column(name = "RESOURCE_TYPE_ID", length = 100)
    private String resourceTypeId;

    @Column(name = "ASSOCIATION_NAME", length = 100)
    private String associationName;

    @Column(name = "MEMBER_ASSOSIATION_TYPE", length = 100)
    private String MemberAssociationType;

    @Column(name = "MEMBER_ASSOSIATION_ID", length = 100)
    private String MemberAssociationId;

    @Column(name = "EMPLOYEE_FIRST_NAME", length = 100)
    private String employeeFirstName;

    @Column(name = "EMPLOYEE_LAST_NAME", length = 100)
    private String employeeLastName;

    @Column(name = "APPROVER_ID", length = 100)
    private String approverId;

    @Column(name = "APPROVER_PRINCIPAL", length = 100)
    private String approverPrincipal;

    @Column(name = "APPROVER_FIRST_NAME", length = 100)
    private String approverFirstName;

    @Column(name = "APPROVER_LAST_NAME", length = 100)
    private String approverLastName;

    @Column(name = "APPROVAL_DATE", length = 100)
    private Date approvalDate;

    @Column(name = "IS_APPROVED", length = 100)
    private String isApproved;

    @Column(name = "COMMENT", length = 100)
    private String comment;

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterPrincipal() {
        return requesterPrincipal;
    }

    public void setRequesterPrincipal(String requesterPrincipal) {
        this.requesterPrincipal = requesterPrincipal;
    }

    public String getRequestCreateDate() {
        return requestCreateDate;
    }

    public void setRequestCreateDate(String requestCreateDate) {
        this.requestCreateDate = requestCreateDate;
    }

    public String getRequesterFirstName() {
        return requesterFirstName;
    }

    public void setRequesterFirstName(String requesterFirstName) {
        this.requesterFirstName = requesterFirstName;
    }

    public String getRequesterLastName() {
        return requesterLastName;
    }

    public void setRequesterLastName(String requesterLastName) {
        this.requesterLastName = requesterLastName;
    }

    public String getLogAction() {
        return logAction;
    }

    public void setLogAction(String logAction) {
        this.logAction = logAction;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    public String getAssociationId() {
        return associationId;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getAssociationName() {
        return associationName;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
    }

    public String getMemberAssociationType() {
        return MemberAssociationType;
    }

    public void setMemberAssociationType(String memberAssociationType) {
        MemberAssociationType = memberAssociationType;
    }

    public String getMemberAssociationId() {
        return MemberAssociationId;
    }

    public void setMemberAssociationId(String memberAssociationId) {
        MemberAssociationId = memberAssociationId;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproverPrincipal() {
        return approverPrincipal;
    }

    public void setApproverPrincipal(String approverPrincipal) {
        this.approverPrincipal = approverPrincipal;
    }

    public String getApproverFirstName() {
        return approverFirstName;
    }

    public void setApproverFirstName(String approverFirstName) {
        this.approverFirstName = approverFirstName;
    }

    public String getApproverLastName() {
        return approverLastName;
    }

    public void setApproverLastName(String approverLastName) {
        this.approverLastName = approverLastName;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkflowStatusRptViewEntity that = (WorkflowStatusRptViewEntity) o;

        if (logId != null ? !logId.equals(that.logId) : that.logId != null) return false;
        if (requesterId != null ? !requesterId.equals(that.requesterId) : that.requesterId != null) return false;
        if (requesterPrincipal != null ? !requesterPrincipal.equals(that.requesterPrincipal) : that.requesterPrincipal != null)
            return false;
        if (requestCreateDate != null ? !requestCreateDate.equals(that.requestCreateDate) : that.requestCreateDate != null)
            return false;
        if (requesterFirstName != null ? !requesterFirstName.equals(that.requesterFirstName) : that.requesterFirstName != null)
            return false;
        if (requesterLastName != null ? !requesterLastName.equals(that.requesterLastName) : that.requesterLastName != null)
            return false;
        if (logAction != null ? !logAction.equals(that.logAction) : that.logAction != null) return false;
        if (associationType != null ? !associationType.equals(that.associationType) : that.associationType != null)
            return false;
        if (associationId != null ? !associationId.equals(that.associationId) : that.associationId != null)
            return false;
        if (resourceTypeId != null ? !resourceTypeId.equals(that.resourceTypeId) : that.resourceTypeId != null)
            return false;
        if (associationName != null ? !associationName.equals(that.associationName) : that.associationName != null)
            return false;
        if (MemberAssociationType != null ? !MemberAssociationType.equals(that.MemberAssociationType) : that.MemberAssociationType != null)
            return false;
        if (MemberAssociationId != null ? !MemberAssociationId.equals(that.MemberAssociationId) : that.MemberAssociationId != null)
            return false;
        if (employeeFirstName != null ? !employeeFirstName.equals(that.employeeFirstName) : that.employeeFirstName != null)
            return false;
        if (employeeLastName != null ? !employeeLastName.equals(that.employeeLastName) : that.employeeLastName != null)
            return false;
        if (approverId != null ? !approverId.equals(that.approverId) : that.approverId != null) return false;
        if (approverPrincipal != null ? !approverPrincipal.equals(that.approverPrincipal) : that.approverPrincipal != null)
            return false;
        if (approverFirstName != null ? !approverFirstName.equals(that.approverFirstName) : that.approverFirstName != null)
            return false;
        if (approverLastName != null ? !approverLastName.equals(that.approverLastName) : that.approverLastName != null)
            return false;
        if (approvalDate != null ? !approvalDate.equals(that.approvalDate) : that.approvalDate != null) return false;
        if (isApproved != null ? !isApproved.equals(that.isApproved) : that.isApproved != null) return false;
        return !(comment != null ? !comment.equals(that.comment) : that.comment != null);

    }

    @Override
    public int hashCode() {
        int result = requesterId != null ? requesterId.hashCode() : 0;
        result = 31 * result + (logId != null ? logId.hashCode() : 0);
        result = 31 * result + (requesterPrincipal != null ? requesterPrincipal.hashCode() : 0);
        result = 31 * result + (requestCreateDate != null ? requestCreateDate.hashCode() : 0);
        result = 31 * result + (requesterFirstName != null ? requesterFirstName.hashCode() : 0);
        result = 31 * result + (requesterLastName != null ? requesterLastName.hashCode() : 0);
        result = 31 * result + (logAction != null ? logAction.hashCode() : 0);
        result = 31 * result + (associationType != null ? associationType.hashCode() : 0);
        result = 31 * result + (associationId != null ? associationId.hashCode() : 0);
        result = 31 * result + (resourceTypeId != null ? resourceTypeId.hashCode() : 0);
        result = 31 * result + (associationName != null ? associationName.hashCode() : 0);
        result = 31 * result + (MemberAssociationType != null ? MemberAssociationType.hashCode() : 0);
        result = 31 * result + (MemberAssociationId != null ? MemberAssociationId.hashCode() : 0);
        result = 31 * result + (employeeFirstName != null ? employeeFirstName.hashCode() : 0);
        result = 31 * result + (employeeLastName != null ? employeeLastName.hashCode() : 0);
        result = 31 * result + (approverId != null ? approverId.hashCode() : 0);
        result = 31 * result + (approverPrincipal != null ? approverPrincipal.hashCode() : 0);
        result = 31 * result + (approverFirstName != null ? approverFirstName.hashCode() : 0);
        result = 31 * result + (approverLastName != null ? approverLastName.hashCode() : 0);
        result = 31 * result + (approvalDate != null ? approvalDate.hashCode() : 0);
        result = 31 * result + (isApproved != null ? isApproved.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WorkflowStatusRptViewEntity{" +
                "requesterId='" + requesterId + '\'' +
                ", logId='" + logId + '\'' +
                ", requesterPrincipal='" + requesterPrincipal + '\'' +
                ", requestCreateDate='" + requestCreateDate + '\'' +
                ", requesterFirstName='" + requesterFirstName + '\'' +
                ", requesterLastName='" + requesterLastName + '\'' +
                ", logAction='" + logAction + '\'' +
                ", associationType='" + associationType + '\'' +
                ", associationId='" + associationId + '\'' +
                ", resourceTypeId='" + resourceTypeId + '\'' +
                ", associationName='" + associationName + '\'' +
                ", MemberAssociationType='" + MemberAssociationType + '\'' +
                ", MemberAssociationId='" + MemberAssociationId + '\'' +
                ", employeeFirstName='" + employeeFirstName + '\'' +
                ", employeeLastName='" + employeeLastName + '\'' +
                ", approverId='" + approverId + '\'' +
                ", approverPrincipal='" + approverPrincipal + '\'' +
                ", approverFirstName='" + approverFirstName + '\'' +
                ", approverLastName='" + approverLastName + '\'' +
                ", approvalDate=" + approvalDate +
                ", isApproved='" + isApproved + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
