package org.openiam.idm.srvc.prov.request.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;

@Entity
@Table(name = "REQ_APPROVER")
@DozerDTOCorrespondence(RequestApprover.class)
public class RequestApproverEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REQ_APPROVER_ID", length = 32)
	private String id;
	
	@Column(name = "APPROVER_ID", length = 32)
	private String approverId;
	
	@Column(name = "APPROVER_LEVEL")
	private Integer approverLevel;
	
	@Column(name = "APPROVER_TYPE", length = 20)
	private String approverType;
	
	@Column(name = "REQUEST_ID", length = 32)
	private String requestId;
	
	@Column(name="ACTION_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
	private Date actionDate;
	
	@Column(name = "ACTION", length = 20)
	private String action;
	
	@Column(name = "CMT", length = 1000)
	private String comment;
	
	@Column(name = "STATUS", length = 20)
	private String status;
	
	@Column(name = "MNG_SYS_GROUP_ID", length = 32)
	private String mngSysGroupId;
	
	@Column(name = "MANAGED_SYS_ID", length = 32)
	private String managedSysId;
	
	public RequestApproverEntity(String approverId, Integer approverLevel,
            String approverType, String status) {
		this.approverId = approverId;
		this.approverLevel = approverLevel;
		this.approverType = approverType;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApproverId() {
		return approverId;
	}
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	public Integer getApproverLevel() {
		return approverLevel;
	}
	public void setApproverLevel(Integer approverLevel) {
		this.approverLevel = approverLevel;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result
				+ ((approverId == null) ? 0 : approverId.hashCode());
		result = prime * result
				+ ((approverLevel == null) ? 0 : approverLevel.hashCode());
		result = prime * result
				+ ((approverType == null) ? 0 : approverType.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((mngSysGroupId == null) ? 0 : mngSysGroupId.hashCode());
		result = prime * result
				+ ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestApproverEntity other = (RequestApproverEntity) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (approverId == null) {
			if (other.approverId != null)
				return false;
		} else if (!approverId.equals(other.approverId))
			return false;
		if (approverLevel == null) {
			if (other.approverLevel != null)
				return false;
		} else if (!approverLevel.equals(other.approverLevel))
			return false;
		if (approverType == null) {
			if (other.approverType != null)
				return false;
		} else if (!approverType.equals(other.approverType))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (mngSysGroupId == null) {
			if (other.mngSysGroupId != null)
				return false;
		} else if (!mngSysGroupId.equals(other.mngSysGroupId))
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RequestApproverEntity [id=" + id + ", approverId=" + approverId
				+ ", approverLevel=" + approverLevel + ", approverType="
				+ approverType + ", requestId=" + requestId + ", actionDate="
				+ actionDate + ", action=" + action + ", comment=" + comment
				+ ", status=" + status + ", mngSysGroupId=" + mngSysGroupId
				+ ", managedSysId=" + managedSysId + "]";
	}
	
	
}
