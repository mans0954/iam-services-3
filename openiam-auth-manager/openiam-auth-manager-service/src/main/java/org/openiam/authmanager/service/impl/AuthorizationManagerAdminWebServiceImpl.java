package org.openiam.authmanager.service.impl;

import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerAdminWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service("authorizationManagerAdminWebService")
@WebService(endpointInterface = "org.openiam.authmanager.service.AuthorizationManagerAdminWebService", 
			targetNamespace = "urn:idm.openiam.org/authmanager/service",
			portName = "AuthorizationManagerAdminWebServicePort", 
			serviceName = "AuthorizationManagerAdminWebService")
public class AuthorizationManagerAdminWebServiceImpl implements AuthorizationManagerAdminWebService {

	@Autowired
	private AuthorizationManagerAdminService authManagerAdminService;
	
	@Override
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId) {
		return authManagerAdminService.getUserEntitlementsMatrix(entityId);
	}
    @Override
    public HashSet<String> getOwnerIdsForResource(String resourceId){
        return (HashSet)authManagerAdminService.getOwnerIdsForResource(resourceId);
    }
    @Override
    public HashMap<String, HashSet<String>> getOwnerIdsForResourceSet(HashSet<String> resourceIdSet){
        return authManagerAdminService.getOwnerIdsForResourceSet(resourceIdSet);
    }

}
