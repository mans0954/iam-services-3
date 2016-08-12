package org.openiam.base.request;

import org.openiam.idm.srvc.lang.dto.Language;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by alexander on 08/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseServiceRequest", propOrder = {
        "requesterId",
        "language"
})
public class BaseServiceRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private String requesterId;
    private Language language;

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLanguageId(String languageId) {
        this.language = new Language();
        this.language.setId(languageId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseServiceRequest{");
        sb.append("requesterId='").append(requesterId).append('\'');
        sb.append(", language=").append(language);
        sb.append('}');
        return sb.toString();
    }
}
