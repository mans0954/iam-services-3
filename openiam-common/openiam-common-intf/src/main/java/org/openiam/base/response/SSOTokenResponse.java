package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.SSOToken;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 22/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSOTokenResponse", propOrder = {
        "ssoToken"
})
public class SSOTokenResponse extends Response {
    private SSOToken  ssoToken;

    public SSOToken getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(SSOToken ssoToken) {
        this.ssoToken = ssoToken;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SSOTokenResponse{");
        sb.append(super.toString());
        sb.append(", ssoToken=").append(ssoToken);
        sb.append('}');
        return sb.toString();
    }

    protected Object getValueInternal(){
        return this.ssoToken;
    }
}
