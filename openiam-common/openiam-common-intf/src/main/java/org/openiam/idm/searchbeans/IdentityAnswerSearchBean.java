package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityAnswerSearchBean", propOrder = {
	"userId"
})
public class IdentityAnswerSearchBean extends AbstractSearchBean<UserIdentityAnswer, String> implements SearchBean<UserIdentityAnswer, String> {

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
