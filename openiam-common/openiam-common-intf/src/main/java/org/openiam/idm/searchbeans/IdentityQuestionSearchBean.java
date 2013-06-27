package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestionSearchBean", propOrder = {
	"groupId",
	"active",
	"questionText"
})
public class IdentityQuestionSearchBean extends AbstractSearchBean<IdentityQuestion, String> implements SearchBean<IdentityQuestion, String> {

	private String groupId;
	private Boolean active;
	private String questionText;
	
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public Boolean getActive() {
		return active;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public void setActive(Boolean active) {
		this.active = active;
	}
	
	
}
