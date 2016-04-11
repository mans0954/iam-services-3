package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityAnswerSearchBean", propOrder = {
	"userId",
	"questionId"
})
public class IdentityAnswerSearchBean extends AbstractSearchBean<UserIdentityAnswer, String> implements SearchBean<UserIdentityAnswer, String> {

	private String questionId;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	@Override
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(questionId != null ? questionId : "")
				.append(userId != null ? userId : "")
				.append(getKey() != null ? getKey() : "")
				.toString();	}
}
