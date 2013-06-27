package org.openiam.idm.srvc.pswd.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

@Entity
@Table(name = "IDENTITY_QUESTION")
@DozerDTOCorrespondence(IdentityQuestion.class)
public class IdentityQuestionEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "IDENTITY_QUESTION_ID", length = 32)
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="IDENTITY_QUEST_GRP_ID", referencedColumnName="IDENTITY_QUEST_GRP_ID", insertable = true, updatable = true)
	private IdentityQuestGroupEntity identityQuestGrp;
	
	@Column(name = "QUESTION_TEXT")
	private String questionText;
	
	@Column(name = "ACTIVE")
    @Type(type = "yes_no")
	private Boolean active;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public IdentityQuestGroupEntity getIdentityQuestGrp() {
		return identityQuestGrp;
	}
	public void setIdentityQuestGrp(IdentityQuestGroupEntity identityQuestGrp) {
		this.identityQuestGrp = identityQuestGrp;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public Boolean isActive() {
		return active;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((identityQuestGrp == null) ? 0 : identityQuestGrp.hashCode());
		result = prime * result
				+ ((questionText == null) ? 0 : questionText.hashCode());
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
		IdentityQuestionEntity other = (IdentityQuestionEntity) obj;
		if (active != other.active)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identityQuestGrp == null) {
			if (other.identityQuestGrp != null)
				return false;
		} else if (!identityQuestGrp.equals(other.identityQuestGrp))
			return false;
		if (questionText == null) {
			if (other.questionText != null)
				return false;
		} else if (!questionText.equals(other.questionText))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("IdentityQuestionEntity [id=%s, identityQuestGrp=%s, questionText=%s, active=%s]",
						id, (identityQuestGrp != null) ? identityQuestGrp.getId() : null, questionText, active);
	}
	
	
	
}
