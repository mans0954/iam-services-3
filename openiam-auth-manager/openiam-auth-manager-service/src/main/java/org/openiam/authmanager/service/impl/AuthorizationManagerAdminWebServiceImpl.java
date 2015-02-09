package org.openiam.authmanager.service.impl;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerAdminWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
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
    @WebMethod
	public UserEntitlementsMatrix getUserEntitlementsMatrix(@WebParam(name = "entityId", targetNamespace = "") final String entityId) {
		return authManagerAdminService.getUserEntitlementsMatrix(entityId);
	}
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForResource(@WebParam(name = "resourceId", targetNamespace = "") String resourceId){
        return authManagerAdminService.getOwnerIdsForResource(resourceId);
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(@WebParam(name = "resourceIdSet", targetNamespace = "") Set<String> resourceIdSet){
        return authManagerAdminService.getOwnerIdsForResourceSet(resourceIdSet);
    }
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForGroup(String groupId){
        return authManagerAdminService.getOwnerIdsForGroup(groupId);
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForGroupSet(@WebParam(name = "groupIdSet", targetNamespace = "") Set<String> groupIdSet){
        return authManagerAdminService.getOwnerIdsForGroupSet(groupIdSet);
    }
}
