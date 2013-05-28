package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;

@Entity
@Table(name = "APPROVER_ASSOC")
@DozerDTOCorrespondence(ApproverAssociation.class)
public class ApproverAssociationEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "APPROVER_ASSOC_ID", length = 32)
	private String id;
	
	@Column(name = "REQUEST_TYPE", length = 32)
	private String requestType;
	
	@Column(name = "ACTN", length = 32)
	private String action;
	
	@Column(name = "ASSOCIATION_OBJ_ID", length = 32)
	private String associationObjId;
	
	@Column(name = "APPROVER_USER_ID", length = 32)
	private String approverUserId;

	@Column(name = "APPROVER_ROLE_ID", length = 32)
	private String approverRoleId;
	
	@Column(name = "APPLY_DELEGATION_FILTER")
	private Integer applyDelegationFilter = new Integer(0);

	@Column(name = "ASSOCIATION_TYPE", length = 20)
	private String associationType;
	
	@Column(name = "APPROVER_LEVEL")
	private Integer approverLevel;

	@Column(name = "ON_APPROVE_NOTIFY_USER_ID", length = 32)
	private String notifyUserOnApprove;
	
	@Column(name = "ON_REJECT_NOTIFY_USER_ID", length = 32)
	private String notifyUserOnReject;
	
	@Column(name = "APPROVE_NOTIFY_USER_TYPE", length = 20)
	private String approveNotificationUserType;
	
	@Column(name = "REJECT_NOTIFY_USER_TYPE", length = 20)
	private String rejectNotificationUserType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime
				* result
				+ ((applyDelegationFilter == null) ? 0 : applyDelegationFilter
						.hashCode());
		result = prime
				* result
				+ ((approveNotificationUserType == null) ? 0
						: approveNotificationUserType.hashCode());
		result = prime * result
				+ ((approverLevel == null) ? 0 : approverLevel.hashCode());
		result = prime * result
				+ ((approverRoleId == null) ? 0 : approverRoleId.hashCode());
		result = prime * result
				+ ((approverUserId == null) ? 0 : approverUserId.hashCode());
		result = prime
				* result
				+ ((associationObjId == null) ? 0 : associationObjId.hashCode());
		result = prime * result
				+ ((associationType == null) ? 0 : associationType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((notifyUserOnApprove == null) ? 0 : notifyUserOnApprove
						.hashCode());
		result = prime
				* result
				+ ((notifyUserOnReject == null) ? 0 : notifyUserOnReject
						.hashCode());
		result = prime
				* result
				+ ((rejectNotificationUserType == null) ? 0
						: rejectNotificationUserType.hashCode());
		result = prime * result
				+ ((requestType == null) ? 0 : requestType.hashCode());
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
		ApproverAssociationEntity other = (ApproverAssociationEntity) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (applyDelegationFilter == null) {
			if (other.applyDelegationFilter != null)
				return false;
		} else if (!applyDelegationFilter.equals(other.applyDelegationFilter))
			return false;
		if (approveNotificationUserType == null) {
			if (other.approveNotificationUserType != null)
				return false;
		} else if (!approveNotificationUserType
				.equals(other.approveNotificationUserType))
			return false;
		if (approverLevel == null) {
			if (other.approverLevel != null)
				return false;
		} else if (!approverLevel.equals(other.approverLevel))
			return false;
		if (approverRoleId == null) {
			if (other.approverRoleId != null)
				return false;
		} else if (!approverRoleId.equals(other.approverRoleId))
			return false;
		if (approverUserId == null) {
			if (other.approverUserId != null)
				return false;
		} else if (!approverUserId.equals(other.approverUserId))
			return false;
		if (associationObjId == null) {
			if (other.associationObjId != null)
				return false;
		} else if (!associationObjId.equals(other.associationObjId))
			return false;
		if (associationType == null) {
			if (other.associationType != null)
				return false;
		} else if (!associationType.equals(other.associationType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (notifyUserOnApprove == null) {
			if (other.notifyUserOnApprove != null)
				return false;
		} else if (!notifyUserOnApprove.equals(other.notifyUserOnApprove))
			return false;
		if (notifyUserOnReject == null) {
			if (other.notifyUserOnReject != null)
				return false;
		} else if (!notifyUserOnReject.equals(other.notifyUserOnReject))
			return false;
		if (rejectNotificationUserType == null) {
			if (other.rejectNotificationUserType != null)
				return false;
		} else if (!rejectNotificationUserType
				.equals(other.rejectNotificationUserType))
			return false;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApproverAssociationEntity [id=" + id + ", requestType="
				+ requestType + ", action=" + action + ", associationObjId="
				+ associationObjId + ", approverUserId=" + approverUserId
				+ ", approverRoleId=" + approverRoleId
				+ ", applyDelegationFilter=" + applyDelegationFilter
				+ ", associationType=" + associationType + ", approverLevel="
				+ approverLevel + ", notifyUserOnApprove="
				+ notifyUserOnApprove + ", notifyUserOnReject="
				+ notifyUserOnReject + ", approveNotificationUserType="
				+ approveNotificationUserType + ", rejectNotificationUserType="
				+ rejectNotificationUserType + "]";
	}
	
	
}
