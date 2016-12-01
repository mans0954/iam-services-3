package org.openiam.base.response;

import org.openiam.idm.srvc.lang.dto.Language;


/**
 * Created by alexander on 09/08/16.
 */
public class LanguageListResponse extends BaseListResponse<Language> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LanguageListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
