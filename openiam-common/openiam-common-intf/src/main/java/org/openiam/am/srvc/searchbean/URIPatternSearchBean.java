package org.openiam.am.srvc.searchbean;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternSearchBean", propOrder = {
        "pattern",
        "contentProviderId",
        "authProviderId",
        "showOnApplicationPage",
        "resourceId"
})
public class URIPatternSearchBean extends AbstractSearchBean<URIPattern, String> {
    private String pattern;
    private String contentProviderId;
    private String authProviderId;
    private Boolean showOnApplicationPage;
    private String resourceId;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getContentProviderId() {
        return contentProviderId;
    }

    public void setContentProviderId(String contentProviderId) {
        this.contentProviderId = contentProviderId;
    }

	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	public Boolean getShowOnApplicationPage() {
		return showOnApplicationPage;
	}

	public void setShowOnApplicationPage(Boolean showOnApplicationPage) {
		this.showOnApplicationPage = showOnApplicationPage;
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
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
						.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime
				* result
				+ ((showOnApplicationPage == null) ? 0 : showOnApplicationPage
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
		URIPatternSearchBean other = (URIPatternSearchBean) obj;
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (showOnApplicationPage == null) {
			if (other.showOnApplicationPage != null)
				return false;
		} else if (!showOnApplicationPage.equals(other.showOnApplicationPage))
			return false;
		return true;
	}

	
}
