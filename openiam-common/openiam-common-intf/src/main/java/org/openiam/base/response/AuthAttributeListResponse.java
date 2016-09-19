package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 16/09/16.
 */
public class AuthAttributeListResponse extends Response {
    private List<AuthAttribute> attributeList;

    public List<AuthAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<AuthAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthAttributeListResponse{");
        sb.append(super.toString());
        sb.append("attributeList=").append(attributeList);
        sb.append('}');
        return sb.toString();
    }
}
