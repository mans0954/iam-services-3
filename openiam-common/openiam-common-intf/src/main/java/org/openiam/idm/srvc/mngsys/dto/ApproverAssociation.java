package org.openiam.idm.srvc.mngsys.dto;

import java.io.Serializable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproverAssociation", propOrder = {
	"id",
	"requestType",
	"applyDelegationFilter",
	"associationType",
	"associationEntityId",
	"approverLevel",
	"onApproveEntityId",
	"onRejectEntityId",
	"onApproveEntityType",
	"onRejectEntityType",
	"approverEntityId",
	"approverEntityType"
})
@DozerDTOCorrespondence(ApproverAssociationEntity.class)
public class ApproverAssociation implements Serializable {
	private String id;
	private String requestType;
	private boolean applyDelegationFilter;
	
	@Enumerated(EnumType.STRING)
	private AssociationType associationType;
	private String associationEntityId;
	private Integer approverLevel;
	private String onApproveEntityId;
	private String onRejectEntityId;
	
	@Enumerated(EnumType.STRING)
	private AssociationType onApproveEntityType;
	
	@Enumerated(EnumType.STRING)
	private AssociationType onRejectEntityType;
	
	private String approverEntityId;
	
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
		ApproverAssociation other = (ApproverAssociation) obj;
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
		return "ApproverAssociation [id=" + id + ", requestType=" + requestType
				+ ", applyDelegationFilter=" + applyDelegationFilter
				+ ", associationType=" + associationType
				+ ", associationEntityId=" + associationEntityId
				+ ", approverLevel=" + approverLevel + ", onApproveEntityId="
				+ onApproveEntityId + ", onRejectEntityId=" + onRejectEntityId
				+ ", onApproveEntityType=" + onApproveEntityType
				+ ", onRejectEntityType=" + onRejectEntityType
				+ ", approverEntityId=" + approverEntityId
				+ ", approverEntityType=" + approverEntityType + "]";
	}
	
	
	
}
