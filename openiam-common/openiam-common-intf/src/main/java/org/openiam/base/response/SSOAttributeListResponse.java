package org.openiam.base.response;

import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 20/09/16.
 */
public class SSOAttributeListResponse extends Response {
    private List<SSOAttribute> ssoAttributeList;

    public List<SSOAttribute> getSsoAttributeList() {
        return ssoAttributeList;
    }

    public void setSsoAttributeList(List<SSOAttribute> ssoAttributeList) {
        this.ssoAttributeList = ssoAttributeList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SSOAttributeListResponse{");
        sb.append(super.toString());
        sb.append(", ssoAttributeList=").append(ssoAttributeList);
        sb.append('}');
        return sb.toString();
    }
}
