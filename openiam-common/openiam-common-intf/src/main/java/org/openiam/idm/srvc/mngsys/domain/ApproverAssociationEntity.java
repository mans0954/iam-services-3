package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;

@Entity
@Table(name = "APPROVER_ASSOC")
@DozerDTOCorrespondence(ApproverAssociation.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApproverAssociationEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "APPROVER_ASSOC_ID", length = 32)
	private String id;
	
	@Column(name = "REQUEST_TYPE", length = 32)
	private String requestType;
	
	@Column(name = "APPLY_DELEGATION_FILTER")
	@Type(type = "yes_no")
	private boolean applyDelegationFilter;

	@Column(name = "ASSOCIATION_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private AssociationType associationType;
	
	@Column(name = "ASSOCIATION_ENTITY_ID", length = 32)
	private String associationEntityId;
	
	@Column(name = "APPROVER_LEVEL")
	private Integer approverLevel;

	@Column(name = "ON_APPROVE_ENTITY_ID", length = 32)
	private String onApproveEntityId;
	
	@Column(name = "ON_REJECT_ENTITY_ID", length = 32)
	private String onRejectEntityId;
	
	@Column(name = "ON_APPROVE_ENTITY_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private AssociationType onApproveEntityType;
	
	@Column(name = "ON_REJECT_ENTITY_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private AssociationType onRejectEntityType;
	
	@Column(name = "APPROVER_ENTITY_ID", length = 32)
	private String approverEntityId;
	
	@Column(name = "APPROVER_ENTITY_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private AssociationType approverEntityType;

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

	public boolean isApplyDelegationFilter() {
		return applyDelegationFilter;
	}

	public void setApplyDelegationFilter(boolean applyDelegationFilter) {
		this.applyDelegationFilter = applyDelegationFilter;
	}

	public String getAssociationEntityId() {
		return associationEntityId;
	}

	public void setAssociationEntityId(String associationEntityId) {
		this.associationEntityId = associationEntityId;
	}

	public Integer getApproverLevel() {
		return approverLevel;
	}

	public void setApproverLevel(Integer approverLevel) {
		this.approverLevel = approverLevel;
	}

	public String getOnApproveEntityId() {
		return onApproveEntityId;
	}

	public void setOnApproveEntityId(String onApproveEntityId) {
		this.onApproveEntityId = onApproveEntityId;
	}

	public String getOnRejectEntityId() {
		return onRejectEntityId;
	}

	public void setOnRejectEntityId(String onRejectEntityId) {
		this.onRejectEntityId = onRejectEntityId;
	}

	public AssociationType getAssociationType() {
		return associationType;
	}

	public void setAssociationType(AssociationType associationType) {
		this.associationType = associationType;
	}

	public AssociationType getOnApproveEntityType() {
		return onApproveEntityType;
	}

	public void setOnApproveEntityType(AssociationType onApproveEntityType) {
		this.onApproveEntityType = onApproveEntityType;
	}

	public AssociationType getOnRejectEntityType() {
		return onRejectEntityType;
	}

	public void setOnRejectEntityType(AssociationType onRejectEntityType) {
		this.onRejectEntityType = onRejectEntityType;
	}
	
	

	public String getApproverEntityId() {
		return approverEntityId;
	}

	public void setApproverEntityId(String approverEntityId) {
		this.approverEntityId = approverEntityId;
	}

	public AssociationType getApproverEntityType() {
		return approverEntityType;
	}

	public void setApproverEntityType(AssociationType approverEntityType) {
		this.approverEntityType = approverEntityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (applyDelegationFilter ? 1231 : 1237);
		result = prime
				* result
				+ ((approverEntityId == null) ? 0 : approverEntityId.hashCode());
		result = prime
				* result
				+ ((approverEntityType == null) ? 0 : approverEntityType
						.hashCode());
		result = prime * result
				+ ((approverLevel == null) ? 0 : approverLevel.hashCode());
		result = prime
				* result
				+ ((associationEntityId == null) ? 0 : associationEntityId
						.hashCode());
		result = prime * result
				+ ((associationType == null) ? 0 : associationType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((onApproveEntityId == null) ? 0 : onApproveEntityId
						.hashCode());
		result = prime
				* result
				+ ((onApproveEntityType == null) ? 0 : onApproveEntityType
						.hashCode());
		result = prime
				* result
				+ ((onRejectEntityId == null) ? 0 : onRejectEntityId.hashCode());
		result = prime
				* result
				+ ((onRejectEntityType == null) ? 0 : onRejectEntityType
						.hashCode());
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
		if (applyDelegationFilter != other.applyDelegationFilter)
			return false;
		if (approverEntityId == null) {
			if (other.approverEntityId != null)
				return false;
		} else if (!approverEntityId.equals(other.approverEntityId))
			return false;
		if (approverEntityType != other.approverEntityType)
			return false;
		if (approverLevel == null) {
			if (other.approverLevel != null)
				return false;
		} else if (!approverLevel.equals(other.approverLevel))
			return false;
		if (associationEntityId == null) {
			if (other.associationEntityId != null)
				return false;
		} else if (!associationEntityId.equals(other.associationEntityId))
			return false;
		if (associationType != other.associationType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (onApproveEntityId == null) {
			if (other.onApproveEntityId != null)
				return false;
		} else if (!onApproveEntityId.equals(other.onApproveEntityId))
			return false;
		if (onApproveEntityType != other.onApproveEntityType)
			return false;
		if (onRejectEntityId == null) {
			if (other.onRejectEntityId != null)
				return false;
		} else if (!onRejectEntityId.equals(other.onRejectEntityId))
			return false;
		if (onRejectEntityType != other.onRejectEntityType)
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
				+ requestType + ", applyDelegationFilter="
				+ applyDelegationFilter + ", associationType="
				+ associationType + ", associationEntityId="
				+ associationEntityId + ", approverLevel=" + approverLevel
				+ ", onApproveEntityId=" + onApproveEntityId
				+ ", onRejectEntityId=" + onRejectEntityId
				+ ", onApproveEntityType=" + onApproveEntityType
				+ ", onRejectEntityType=" + onRejectEntityType
				+ ", approverEntityId=" + approverEntityId
				+ ", approverEntityType=" + approverEntityType + "]";
	}

	
	
}
