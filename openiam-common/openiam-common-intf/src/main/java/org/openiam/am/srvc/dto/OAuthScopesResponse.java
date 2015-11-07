package org.openiam.am.srvc.dto;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.res.dto.Resource;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alexander on 02/08/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAuthScopesResponse", propOrder = {
      "clientId",
      "oauthScopeList"
})
public class OAuthScopesResponse extends Response {
    private String clientId;
    private List<Resource> oauthScopeList;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<Resource> getOauthScopeList() {
        return oauthScopeList;
    }

    public void setOauthScopeList(List<Resource> oauthScopeList) {
        this.oauthScopeList = oauthScopeList;
    }

}
