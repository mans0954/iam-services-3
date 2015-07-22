package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproverAssocationSearchBean", propOrder = {
        "id",
        "associationType",
        "associationEntityId",
		"approverEntityId",
		"approverEntityType",
		"onApproveEntityId",
		"onApproveEntityType",
		"onRejectEntityId",
		"onRejectEntityType"
})
public class ApproverAssocationSearchBean extends AbstractSearchBean<ApproverAssociation, String> {
	private String id;
	private AssociationType associationType;
	private String associationEntityId;

	private String approverEntityId;
	private AssociationType approverEntityType;
	private String onApproveEntityId;
	private AssociationType onApproveEntityType;
	private String onRejectEntityId;
	private AssociationType onRejectEntityType;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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

	@Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(associationType != null ? associationType : "")
                .append(id != null ? id : "")
                .append(associationEntityId != null ? associationEntityId : "")
				.append(approverEntityId != null ? approverEntityId : "")
				.append(approverEntityType != null ? approverEntityType : "")
				.append(onApproveEntityId != null ? onApproveEntityId : "")
				.append(onApproveEntityType != null ? onApproveEntityType : "")
				.append(onRejectEntityId != null ? onRejectEntityId : "")
				.append(onRejectEntityType != null ? onRejectEntityType : "")
				.append(getKey() != null ? getKey() : "")
                .toString();
    }
}
