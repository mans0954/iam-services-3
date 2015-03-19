package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternSearchBean", propOrder = {
        "pattern",
        "contentProviderId"
})
public class URIPatternSearchBean extends AbstractSearchBean<URIPattern, String> {
    private String pattern;
    private String contentProviderId;

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

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(pattern != null ? pattern : "")
                .append(contentProviderId != null ? contentProviderId : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
