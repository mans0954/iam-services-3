package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityAnswerSearchBean", propOrder = {
	"userId",
	"questionId",
	"decryptAnswers"
})
public class IdentityAnswerSearchBean extends AbstractSearchBean<UserIdentityAnswer, String> implements SearchBean<UserIdentityAnswer, String> {

	private boolean decryptAnswers;
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

	public boolean isDecryptAnswers() {
		return decryptAnswers;
	}

	public void setDecryptAnswers(boolean decryptAnswers) {
		this.decryptAnswers = decryptAnswers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (decryptAnswers ? 1231 : 1237);
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		IdentityAnswerSearchBean other = (IdentityAnswerSearchBean) obj;
		if (decryptAnswers != other.decryptAnswers)
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
