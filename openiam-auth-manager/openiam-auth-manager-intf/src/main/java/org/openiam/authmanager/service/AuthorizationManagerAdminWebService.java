package org.openiam.authmanager.service;

import org.openiam.authmanager.model.UserEntitlementsMatrix;

import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);

    public HashSet<String> getOwnerIdsForResource(String resourceId);
    public HashMap<String, HashSet<String>> getOwnerIdsForResourceSet(HashSet<String> resourceIdSet);
}
