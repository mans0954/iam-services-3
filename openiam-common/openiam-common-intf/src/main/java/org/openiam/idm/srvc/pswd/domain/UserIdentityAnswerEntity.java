package org.openiam.idm.srvc.pswd.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

import javax.persistence.*;

@Entity
@Table(name = "USER_IDENTITY_ANS")
@DozerDTOCorrespondence(UserIdentityAnswer.class)
@AttributeOverride(name = "id", column = @Column(name = "IDENTITY_ANS_ID"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserIdentityAnswerEntity extends KeyEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="IDENTITY_QUESTION_ID", referencedColumnName="IDENTITY_QUESTION_ID", insertable = true, updatable = true)
	private IdentityQuestionEntity identityQuestion;
	
	@Column(name = "USER_ID", length = 32)
	private String userId;
	
	@Column(name = "QUESTION_ANSWER", length = 1024)
	private String questionAnswer;

    @Column(name="IS_ENCRYPTED")
    @Type(type = "yes_no")
    private boolean isEncrypted=false;

	public IdentityQuestionEntity getIdentityQuestion() {
		return identityQuestion;
	}

	public void setIdentityQuestion(IdentityQuestionEntity identityQuestion) {
		this.identityQuestion = identityQuestion;
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


    public boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((identityQuestion == null) ? 0 : identityQuestion
						.hashCode());
		result = prime * result
				+ ((questionAnswer == null) ? 0 : questionAnswer.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identityQuestion == null) {
			if (other.identityQuestion != null)
				return false;
		} else if (!identityQuestion.equals(other.identityQuestion))
			return false;
		if (questionAnswer == null) {
			if (other.questionAnswer != null)
				return false;
		} else if (!questionAnswer.equals(other.questionAnswer))
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
				.format("UserIdentityAnswerEntity [identityAnsId=%s, identityQuestion=%s, userId=%s, questionAnswer=%s]",
						id, identityQuestion, userId, questionAnswer);
	}
	
	
}
