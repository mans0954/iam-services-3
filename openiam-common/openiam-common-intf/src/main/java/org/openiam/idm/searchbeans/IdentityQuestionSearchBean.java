package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestionSearchBean", propOrder = {
	"groupId",
	"active"
})
public class IdentityQuestionSearchBean extends AbstractSearchBean<IdentityQuestion, String> implements SearchBean<IdentityQuestion, String> {

	private String groupId;
	private boolean active = true;
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
