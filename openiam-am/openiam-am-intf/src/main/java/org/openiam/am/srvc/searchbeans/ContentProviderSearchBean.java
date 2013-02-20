package org.openiam.am.srvc.searchbeans;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProviderSearchBean", propOrder = {
        "providerName",
        "domainPattern",
        "contextPath",
        "isSSL"
})
public class ContentProviderSearchBean extends AbstractSearchBean<ContentProvider, String> {
    private String providerName;

    private String domainPattern;
    private String contextPath;
    private Boolean isSSL;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDomainPattern() {
        return domainPattern;
    }

    public void setDomainPattern(String domainPattern) {
        this.domainPattern = domainPattern;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Boolean isSSL() {
        return isSSL;
    }

    public void setSSL(Boolean SSL) {
        isSSL = SSL;
    }
}
