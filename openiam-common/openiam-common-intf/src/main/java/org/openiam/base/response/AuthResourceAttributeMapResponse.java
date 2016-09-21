package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 20/09/16.
 */
public class AuthResourceAttributeMapResponse extends Response {
    private AuthResourceAttributeMap attributeMap;

    public AuthResourceAttributeMap getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(AuthResourceAttributeMap attributeMap) {
        this.attributeMap = attributeMap;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthResourceAttributeMapResponse{");
        sb.append(super.toString());
        sb.append(", attributeMap=").append(attributeMap);
        sb.append('}');
        return sb.toString();
    }
}
