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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "IDENTITY_QUESTION_ID", referencedColumnName = "IDENTITY_QUESTION_ID", insertable = true, updatable = true)
    private IdentityQuestionEntity identityQuestion;

    @Column(name = "USER_ID", length = 32, nullable = false)
    private String userId;

    @Column(name = "QUESTION_ANSWER", length = 2000, nullable = false)
    private String questionAnswer;

    @Column(name = "IS_ENCRYPTED", nullable = false)
    @Type(type = "yes_no")
    private boolean isEncrypted = false;


    @Column(name = "QUESTION_TEXT", length = 2000)
    private String questionText;

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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserIdentityAnswerEntity)) return false;
        if (!super.equals(o)) return false;

        UserIdentityAnswerEntity that = (UserIdentityAnswerEntity) o;

        if (isEncrypted != that.isEncrypted) return false;
        if (identityQuestion != null ? !identityQuestion.equals(that.identityQuestion) : that.identityQuestion != null)
            return false;
        if (questionText != null ? !questionText.equals(that.questionText) : that.questionText != null) return false;
        if (questionAnswer != null ? !questionAnswer.equals(that.questionAnswer) : that.questionAnswer != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (identityQuestion != null ? identityQuestion.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (questionAnswer != null ? questionAnswer.hashCode() : 0);
        result = 31 * result + (isEncrypted ? 1 : 0);
        result = 31 * result + (questionText != null ? questionText.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserIdentityAnswerEntity{" +
                "identityQuestion=" + identityQuestion +
                ", userId='" + userId + '\'' +
                ", questionAnswer='" + questionAnswer + '\'' +
                ", isEncrypted=" + isEncrypted +
                ", questionText='" + questionText + '\'' +
                '}';
    }
}
