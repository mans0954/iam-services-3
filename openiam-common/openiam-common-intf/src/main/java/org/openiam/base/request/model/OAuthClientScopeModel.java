package org.openiam.base.request.model;

import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.openiam.base.KeyDTO;

import java.util.List;

/**
 * Created by alexander on 28/09/16.
 */
public class OAuthClientScopeModel extends KeyDTO {
    private String userId;
    private List<OAuthUserClientXref> oauthUserClientXrefList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OAuthUserClientXref> getOauthUserClientXrefList() {
        return oauthUserClientXrefList;
    }

    public void setOauthUserClientXrefList(List<OAuthUserClientXref> oauthUserClientXrefList) {
        this.oauthUserClientXrefList = oauthUserClientXrefList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OAuthClientScopeModel{");
        sb.append(super.toString());
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", oauthUserClientXrefList=").append(oauthUserClientXrefList);
        sb.append('}');
        return sb.toString();
    }
}
