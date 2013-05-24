package org.openiam.idm.srvc.prov.request.dto;

// Generated Jan 9, 2009 5:33:58 PM by Hibernate Tools 3.2.2.GA

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;


/**
 * Object represents an approver for a request.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestApprover", propOrder = {
    "reqApproverId",
    "approverId",
    "approverLevel",
    "approverType",
    "requestId",
    "actionDate",
    "action",
    "comment",
    "status",
    "mngSysGroupId",
    "managedSysId"
})
public class RequestApprover implements Serializable {

	private static final long serialVersionUID = -404296971055977744L;
	private String reqApproverId;
	private String approverId;
	private Integer approverLevel;
	private String approverType;
	private String requestId;
	private Date actionDate;
	private String action;
	private String comment;
	private String status;
	
	private String mngSysGroupId;
	private String managedSysId;


	public RequestApprover() {
	}

    public RequestApprover(String approverId, Integer approverLevel,
                           String approverType, String status) {
        this.approverId = approverId;
        this.approverLevel = approverLevel;
        this.approverType = approverType;
        this.status = status;

    }


	public String getReqApproverId() {
		return reqApproverId;
	}


	public void setReqApproverId(String reqApproverId) {
		this.reqApproverId = reqApproverId;
	}


	public String getApproverId() {
		return approverId;
	}


	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}


	


	public String getApproverType() {
		return approverType;
	}


	public void setApproverType(String approverType) {
		this.approverType = approverType;
	}


	public String getRequestId() {
		return requestId;
	}


	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public Date getActionDate() {
		return actionDate;
	}


	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getMngSysGroupId() {
		return mngSysGroupId;
	}


	public void setMngSysGroupId(String mngSysGroupId) {
		this.mngSysGroupId = mngSysGroupId;
	}


	public String getManagedSysId() {
		return managedSysId;
	}


	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getApproverLevel() {
		return approverLevel;
	}


	public void setApproverLevel(Integer approverLevel) {
		this.approverLevel = approverLevel;
	}
}
