package org.openiam.am.srvc.dto;

import org.openiam.base.response.BaseListResponse;
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
      "clientId"
})
public class OAuthScopesResponse extends BaseListResponse<Resource> {
    private String clientId;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OAuthScopesResponse{");
        sb.append(super.toString());
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
