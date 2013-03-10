package org.openiam.idm.srvc.pswd.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

@Entity
@Table(name = "USER_IDENTITY_ANS")
@DozerDTOCorrespondence(UserIdentityAnswer.class)
public class UserIdentityAnswerEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "IDENTITY_ANS_ID", length = 32)
	private String identityAnsId;
	
	@Column(name = "IDENTITY_QUESTION_ID", length = 32)
	private String identityQuestionId;
	
	@Column(name = "QUESTION_TEXT")
	private String questionText;
	
	@Column(name = "USER_ID", length = 32)
	private String userId;
	
	@Column(name = "QUESTION_ANSWER")
	private String questionAnswer;

	public String getIdentityAnsId() {
		return identityAnsId;
	}

	public void setIdentityAnsId(String identityAnsId) {
		this.identityAnsId = identityAnsId;
	}

	public String getIdentityQuestionId() {
		return identityQuestionId;
	}

	public void setIdentityQuestionId(String identityQuestionId) {
		this.identityQuestionId = identityQuestionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identityAnsId == null) ? 0 : identityAnsId.hashCode());
		result = prime
				* result
				+ ((identityQuestionId == null) ? 0 : identityQuestionId
						.hashCode());
		result = prime * result
				+ ((questionAnswer == null) ? 0 : questionAnswer.hashCode());
		result = prime * result
				+ ((questionText == null) ? 0 : questionText.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserIdentityAnswerEntity other = (UserIdentityAnswerEntity) obj;
		if (identityAnsId == null) {
			if (other.identityAnsId != null)
				return false;
		} else if (!identityAnsId.equals(other.identityAnsId))
			return false;
		if (identityQuestionId == null) {
			if (other.identityQuestionId != null)
				return false;
		} else if (!identityQuestionId.equals(other.identityQuestionId))
			return false;
		if (questionAnswer == null) {
			if (other.questionAnswer != null)
				return false;
		} else if (!questionAnswer.equals(other.questionAnswer))
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

	@Override
	public String toString() {
		return String
				.format("UserIdentityAnswerEntity [identityAnsId=%s, identityQuestionId=%s, questionText=%s, userId=%s, questionAnswer=%s]",
						identityAnsId, identityQuestionId, questionText,
						userId, questionAnswer);
	}
	
	
}
