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
	"questionText",
	"isEncrypted"
})
public class IdentityAnswerSearchBean extends AbstractSearchBean<UserIdentityAnswer, String> implements SearchBean<UserIdentityAnswer, String> {

	private String questionId;
	private String userId;
	private String questionText;
	private Boolean isEncrypted;

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

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Boolean getIsEncrypted() {
		return isEncrypted;
	}

	public void setIsEncrypted(Boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isEncrypted == null) ? 0 : isEncrypted.hashCode());
		result = prime * result
				+ ((questionId == null) ? 0 : questionId.hashCode());
		result = prime * result
				+ ((questionText == null) ? 0 : questionText.hashCode());
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
		if (isEncrypted == null) {
			if (other.isEncrypted != null)
				return false;
		} else if (!isEncrypted.equals(other.isEncrypted))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		if (questionText == null) {
			if (other.questionText != null)
				return false;
		} else if (!questionText.equals(other.questionText))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
