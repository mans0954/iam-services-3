package org.openiam.base.response;

import org.openiam.am.srvc.dto.jdbc.ResourceAuthorizationRight;
import org.openiam.base.ws.Response;

import java.util.Set;

/**
 * Created by alexander on 15/09/16.
 */
public class ResourceAuthorizationRightSetResponse extends Response {
    private Set<ResourceAuthorizationRight> resourceSet;

    public Set<ResourceAuthorizationRight> getResourceSet() {
        return resourceSet;
    }

    public void setResourceSet(Set<ResourceAuthorizationRight> resourceSet) {
        this.resourceSet = resourceSet;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResourceAuthorizationRightSetResponse{");
        sb.append(super.toString());
        sb.append(", resourceSet=").append(resourceSet);
        sb.append('}');
        return sb.toString();
    }
}
