package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProviderSearchBean", propOrder = {
        "domainPattern",
        "authProviderId",
        "isSSL",
        "resourceId"
})
public class ContentProviderSearchBean extends AbstractKeyNameSearchBean<ContentProvider, String> {

    private static final long serialVersionUID = 4085178050605918460L;
    private String domainPattern;
    private String authProviderId;
    private Boolean isSSL;
    private String resourceId;

    public String getDomainPattern() {
        return domainPattern;
    }

    public void setDomainPattern(String domainPattern) {
        this.domainPattern = domainPattern;
    }

    

    public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public Boolean isSSL() {
        return isSSL;
    }

    public void setSSL(Boolean SSL) {
        isSSL = SSL;
    }
    
    

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authProviderId == null) ? 0 : authProviderId.hashCode());
		result = prime * result
				+ ((domainPattern == null) ? 0 : domainPattern.hashCode());
		result = prime * result + ((isSSL == null) ? 0 : isSSL.hashCode());
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
		ContentProviderSearchBean other = (ContentProviderSearchBean) obj;
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		if (domainPattern == null) {
			if (other.domainPattern != null)
				return false;
		} else if (!domainPattern.equals(other.domainPattern))
			return false;
		if (isSSL == null) {
			if (other.isSSL != null)
				return false;
		} else if (!isSSL.equals(other.isSSL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContentProviderSearchBean [domainPattern=" + domainPattern
				+ ", authProviderId=" + authProviderId + ", isSSL=" + isSSL
				+ "]";
	}

	
    
}
