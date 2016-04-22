package org.openiam.idm.searchbeans;

import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmailSearchBean", propOrder = {
        "name",
        "parentId",
        "metadataTypeId",
        "email",
        "emailMatchToken"
})
public class EmailSearchBean extends AbstractSearchBean<EmailAddress, String> implements SearchBean<EmailAddress, String>,
        Serializable {
    private String name;
    private String parentId;
    //private String parentType;
    
    @Deprecated
    private String email;
    private String metadataTypeId;
    private SearchParam emailMatchToken;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /*
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
    */

    @Deprecated
	public String getEmail() {
		return (emailMatchToken != null) ? emailMatchToken.getValue() : null;
	}

    @Deprecated
	public void setEmail(String email) {
		this.emailMatchToken = new SearchParam(email, MatchType.STARTS_WITH);
	}
    
    public SearchParam getEmailMatchToken() {
		return emailMatchToken;
	}

	public void setEmailMatchToken(SearchParam emailMatchToken) {
		this.emailMatchToken = emailMatchToken;
	}

	public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((emailMatchToken == null) ? 0 : emailMatchToken.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
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
		EmailSearchBean other = (EmailSearchBean) obj;
		if (emailMatchToken == null) {
			if (other.emailMatchToken != null)
				return false;
		} else if (!emailMatchToken.equals(other.emailMatchToken))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}

    
}
