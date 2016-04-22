package org.openiam.idm.srvc.mngsys.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproverAssocationSearchBean", propOrder = {
        "associationType",
        "associationEntityId",
        "requestType",
        "onApproveEntityId",
        "onApproveEntityType",
        "onRejectEntityId",
        "onRejectEntityType",
        "approverEntityId",
        "approverEntityType",
        "approverLevel"
})
public class ApproverAssocationSearchBean extends AbstractSearchBean<ApproverAssociation, String> implements SearchBean {
	private AssociationType associationType;
	private String associationEntityId;
	private String requestType;
	private String onApproveEntityId;
	private AssociationType onApproveEntityType;
	private String onRejectEntityId;
	private AssociationType onRejectEntityType;
	private String approverEntityId;
	private AssociationType approverEntityType;
	private String approverLevel;
	public AssociationType getAssociationType() {
		return associationType;
	}
	public void setAssociationType(AssociationType associationType) {
		this.associationType = associationType;
	}
	public String getAssociationEntityId() {
		return associationEntityId;
	}
	public void setAssociationEntityId(String associationEntityId) {
		this.associationEntityId = associationEntityId;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getOnApproveEntityId() {
		return onApproveEntityId;
	}
	public void setOnApproveEntityId(String onApproveEntityId) {
		this.onApproveEntityId = onApproveEntityId;
	}
	public AssociationType getOnApproveEntityType() {
		return onApproveEntityType;
	}
	public void setOnApproveEntityType(AssociationType onApproveEntityType) {
		this.onApproveEntityType = onApproveEntityType;
	}
	public String getOnRejectEntityId() {
		return onRejectEntityId;
	}
	public void setOnRejectEntityId(String onRejectEntityId) {
		this.onRejectEntityId = onRejectEntityId;
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
	public String getApproverLevel() {
		return approverLevel;
	}
	public void setApproverLevel(String approverLevel) {
		this.approverLevel = approverLevel;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApproverAssocationSearchBean other = (ApproverAssocationSearchBean) obj;
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


	
}
