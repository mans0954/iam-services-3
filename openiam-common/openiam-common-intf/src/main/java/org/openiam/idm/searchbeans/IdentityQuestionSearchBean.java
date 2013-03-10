package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestionSearchBean", propOrder = {
	"userId",
	"groupId"
})
public class IdentityQuestionSearchBean extends AbstractSearchBean<IdentityQuestion, String> implements SearchBean<IdentityQuestion, String> {

	private String userId;
	private String groupId;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
