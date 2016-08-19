package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;

import java.util.List;

/**
 * Created by alexander on 09/08/16.
 */
public class LanguageListResponse extends Response {
    private List<Language> languageList;

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LanguageListResponse{");
        sb.append(super.toString());
        sb.append(", languageList=").append(languageList);
        sb.append('}');
        return sb.toString();
    }
}
