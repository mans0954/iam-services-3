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
public class IdentityQuestionSearchBean extends AbstractLanguageSearchBean<IdentityQuestion, String> implements SearchBean<IdentityQuestion, String> {

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
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(groupId != null ? groupId : "")
                .append(active != null ? active.booleanValue() : "")
                .append(getKey() != null ? getKey() : "")
				.append(getSortKeyForCache())
                .toString();
    }
}
