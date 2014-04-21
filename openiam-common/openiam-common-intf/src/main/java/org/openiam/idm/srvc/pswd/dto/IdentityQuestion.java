package org.openiam.idm.srvc.pswd.dto;


import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.BaseObject;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestion", propOrder = {
        "identityQuestGrpId",
        "active",
        "displayNameMap",
        "displayName"
})
@DozerDTOCorrespondence(IdentityQuestionEntity.class)
@Internationalized
public class IdentityQuestion extends KeyDTO {

    /**
     *
     */
    private static final long serialVersionUID = -1802758764731284709L;
    protected String identityQuestGrpId;
    protected Boolean active;
    
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> displayNameMap;
	    
    private String displayName;

    public IdentityQuestion() {
    }

    public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getIdentityQuestGrpId() {
		return identityQuestGrpId;
	}

	public void setIdentityQuestGrpId(String identityQuestGrpId) {
		this.identityQuestGrpId = identityQuestGrpId;
	}

	public Map<String, LanguageMapping> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMapping> displayNameMap) {
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
				+ ((identityQuestGrpId == null) ? 0 : identityQuestGrpId
						.hashCode());
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
		IdentityQuestion other = (IdentityQuestion) obj;
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
		if (identityQuestGrpId == null) {
			if (other.identityQuestGrpId != null)
				return false;
		} else if (!identityQuestGrpId.equals(other.identityQuestGrpId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("IdentityQuestion [identityQuestGrpId=%s, active=%s, displayNameMap=%s, displayName=%s, toString()=%s]",
						identityQuestGrpId, active, displayNameMap,
						displayName, super.toString());
	}

	
}
