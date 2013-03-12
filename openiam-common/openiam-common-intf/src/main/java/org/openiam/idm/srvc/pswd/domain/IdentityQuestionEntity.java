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
    @JoinColumn(name="IDENTITY_QUEST_GRP_ID", referencedColumnName="IDENTITY_QUEST_GRP_ID", insertable = false, updatable = false)
	private IdentityQuestGroupEntity identityQuestGrp;
	
	@Column(name = "QUESTION_TEXT")
	private String questionText;
	
	@Column(name = "REQUIRED")
    @Type(type = "yes_no")
	private boolean required = false;
	
	@Column(name = "ACTIVE")
    @Type(type = "yes_no")
	private boolean active = true;
	
	@Column(name = "USER_ID", length = 32)
	private String userId;
	
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
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
