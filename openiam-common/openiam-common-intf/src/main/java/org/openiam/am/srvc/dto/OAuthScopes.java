package org.openiam.am.srvc.dto;

import org.openiam.idm.srvc.res.dto.Resource;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alexander on 02/08/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAuthScopes", propOrder = {
      "oauthScopeList"
})
public class OAuthScopes implements Serializable {
    private List<Resource> oauthScopeList;

    public List<Resource> getOauthScopeList() {
        return oauthScopeList;
    }

    public void setOauthScopeList(List<Resource> oauthScopeList) {
        this.oauthScopeList = oauthScopeList;
    }
}
