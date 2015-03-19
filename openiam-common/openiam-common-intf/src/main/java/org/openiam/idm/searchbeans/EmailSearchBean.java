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
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(email != null ? email : "")
                .append(parentId != null ? parentId : "")
                .append(metadataTypeId != null ? metadataTypeId : "")
                .append(emailMatchToken != null ? emailMatchToken : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
