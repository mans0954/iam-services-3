package org.openiam.base.response;

import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.base.ws.Response;

import java.util.Set;

/**
 * Created by alexander on 15/09/16.
 */
public class OrganizationAuthorizationRightSetResponse extends Response {
    private Set<OrganizationAuthorizationRight> organizationSet;

    public Set<OrganizationAuthorizationRight> getOrganizationSet() {
        return organizationSet;
    }

    public void setOrganizationSet(Set<OrganizationAuthorizationRight> organizationSet) {
        this.organizationSet = organizationSet;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OrganizationAuthorizationRightSetResponse{");
        sb.append(super.toString());
        sb.append(", organizationSet=").append(organizationSet);
        sb.append('}');
        return sb.toString();
    }
}
