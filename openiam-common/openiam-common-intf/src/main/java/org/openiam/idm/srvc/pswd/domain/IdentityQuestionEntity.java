package org.openiam.idm.srvc.pswd.domain;

import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name = "IDENTITY_QUESTION")
@DozerDTOCorrespondence(IdentityQuestion.class)
@AttributeOverride(name = "id", column = @Column(name = "IDENTITY_QUESTION_ID"))
@Internationalized
public class IdentityQuestionEntity extends KeyEntity {
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="IDENTITY_QUEST_GRP_ID", referencedColumnName="IDENTITY_QUEST_GRP_ID", insertable = true, updatable = true)
	private IdentityQuestGroupEntity identityQuestGrp;
	
	//@Column(name = "QUESTION_TEXT")
	//private String questionText;
	
	@Column(name = "ACTIVE")
    @Type(type = "yes_no")
	private Boolean active;
	
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
    @Transient
    private String displayName;
	
	public IdentityQuestGroupEntity getIdentityQuestGrp() {
		return identityQuestGrp;
	}
	public void setIdentityQuestGrp(IdentityQuestGroupEntity identityQuestGrp) {
		this.identityQuestGrp = identityQuestGrp;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Map<String, LanguageMappingEntity> getDisplayNameMap() {
		return displayNameMap;
	}
	public void setDisplayNameMap(Map<String, LanguageMappingEntity> displayNameMap) {
		this.displayNameMap = displayNameMap;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((displayNameMap == null) ? 0 : displayNameMap.hashCode());
		result = prime
				* result
				+ ((identityQuestGrp == null) ? 0 : identityQuestGrp.hashCode());
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
		IdentityQuestionEntity other = (IdentityQuestionEntity) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (displayNameMap == null) {
			if (other.displayNameMap != null)
				return false;
		} else if (!displayNameMap.equals(other.displayNameMap))
			return false;
		if (identityQuestGrp == null) {
			if (other.identityQuestGrp != null)
				return false;
		} else if (!identityQuestGrp.equals(other.identityQuestGrp))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("IdentityQuestionEntity [identityQuestGrp=%s, active=%s, displayNameMap=%s, displayName=%s, toString()=%s]",
						identityQuestGrp, active, displayNameMap, displayName,
						super.toString());
	}
	
	
}
