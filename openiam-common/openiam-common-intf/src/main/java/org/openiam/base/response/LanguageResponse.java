package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;

/**
 * Created by alexander on 09/08/16.
 */
public class LanguageResponse extends Response{
    private Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LanguageResponse{");
        sb.append(super.toString());
        sb.append(", language=").append(language);
        sb.append('}');
        return sb.toString();
    }
}
