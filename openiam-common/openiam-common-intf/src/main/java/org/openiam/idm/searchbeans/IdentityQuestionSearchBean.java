package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestionSearchBean", propOrder = {
	"groupId",
	"active"
})
public class IdentityQuestionSearchBean extends AbstractSearchBean<IdentityQuestion, String> implements SearchBean<IdentityQuestion, String> {

	private String groupId;
	private Boolean active;

	public String getGroupId() {
		return groupId;
	}
	
	public Boolean getActive() {
		return active;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
		IdentityQuestionSearchBean other = (IdentityQuestionSearchBean) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	
}
