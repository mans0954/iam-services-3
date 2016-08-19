package org.openiam.base.response;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 10/08/16.
 */
public class URIPatternResponse extends Response {
    private URIPattern uriPattern;

    public URIPattern getUriPattern() {
        return uriPattern;
    }

    public void setUriPattern(URIPattern uriPattern) {
        this.uriPattern = uriPattern;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("URIPatternResponse{");
        sb.append(super.toString());
        sb.append(", uriPattern=").append(uriPattern);
        sb.append('}');
        return sb.toString();
    }
}
