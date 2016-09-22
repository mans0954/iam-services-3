package org.openiam.base.response;

import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 20/09/16.
 */
public class AuthResourceAMAttributeListResponse extends Response {
    private List<AuthResourceAMAttribute> amAttributeList;

    public List<AuthResourceAMAttribute> getAmAttributeList() {
        return amAttributeList;
    }

    public void setAmAttributeList(List<AuthResourceAMAttribute> amAttributeList) {
        this.amAttributeList = amAttributeList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthResourceAMAttributeListResponse{");
        sb.append(super.toString());
        sb.append(", amAttributeList=").append(amAttributeList);
        sb.append('}');
        return sb.toString();
    }

}
