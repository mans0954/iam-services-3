package org.openiam.authmanager.service;

import org.openiam.authmanager.model.UserEntitlementsMatrix;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Set;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);

    public Set<String> getOwnerIdsForResource(String resourceId);
    public HashMap<String, Set<String>> getOwnerIdsForResourceSet(Set<String> resourceIdSet);
}
