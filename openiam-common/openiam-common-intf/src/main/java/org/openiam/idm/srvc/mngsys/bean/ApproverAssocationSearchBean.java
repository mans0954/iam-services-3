package org.openiam.idm.srvc.mngsys.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractSearchBean;
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
public class ApproverAssocationSearchBean extends AbstractSearchBean<ApproverAssociation, String> {
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

	
}
