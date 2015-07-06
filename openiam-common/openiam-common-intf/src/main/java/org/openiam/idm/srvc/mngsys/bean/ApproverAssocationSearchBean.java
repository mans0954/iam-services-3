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
        "associationEntityId"
})
public class ApproverAssocationSearchBean extends AbstractSearchBean<ApproverAssociation, String> {
	private AssociationType associationType;
	private String associationEntityId;
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

}
