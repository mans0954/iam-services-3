package org.openiam.base.response;

import org.openiam.base.ws.Response;

import java.util.HashMap;

/**
 * Created by alexander on 12/09/16.
 */
public class EntityOwnerResponse extends Response {
    private HashMap<String, SetStringResponse> ownersMap;

    public HashMap<String, SetStringResponse> getOwnersMap() {
        return ownersMap;
    }

    public void setOwnersMap(HashMap<String, SetStringResponse> ownersMap) {
        this.ownersMap = ownersMap;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EntityOwnerResponse{");
        sb.append(super.toString());
        sb.append("ownersMap=").append(ownersMap);
        sb.append('}');
        return sb.toString();
    }
}
