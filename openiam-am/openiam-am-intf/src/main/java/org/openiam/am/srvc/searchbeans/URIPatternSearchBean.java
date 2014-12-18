package org.openiam.am.srvc.searchbeans;

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
        "showOnApplicationPage"
})
public class URIPatternSearchBean extends AbstractSearchBean<URIPattern, String> {
    private String pattern;
    private String contentProviderId;
    private String authProviderId;
    private Boolean showOnApplicationPage;

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
    
    
}
