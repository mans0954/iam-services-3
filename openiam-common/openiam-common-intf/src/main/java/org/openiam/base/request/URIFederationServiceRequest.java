package org.openiam.base.request;


/**
 * Created by alexander on 10/08/16.
 */
public class URIFederationServiceRequest extends AbstractFederationServiceRequest {
    private String userId;
    private String principal;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("URIFederationServiceRequest{");
        sb.append(super.toString());
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", principal='").append(principal).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
