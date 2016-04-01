package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;

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
public class URIPatternSearchBean extends AbstractSearchBean<URIPattern, String> implements SearchBean {
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
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(pattern != null ? pattern : "")
				.append(contentProviderId != null ? contentProviderId : "")
				.append(getKey() != null ? getKey() : "")
				.toString();	}
}
