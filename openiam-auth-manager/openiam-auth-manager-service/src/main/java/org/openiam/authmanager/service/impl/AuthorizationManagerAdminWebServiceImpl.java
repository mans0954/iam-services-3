package org.openiam.authmanager.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerAdminWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId, final Date date) {
		return authManagerAdminService.getUserEntitlementsMatrix(entityId, date);
	}
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForResource(final String resourceId, final Date date){
        return authManagerAdminService.getOwnerIdsForResource(resourceId, date);
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForResourceSet(final Set<String> resourceIdSet, final Date date){
        return authManagerAdminService.getOwnerIdsForResourceSet(resourceIdSet, date);
    }
    @Override
    @WebMethod
    public Set<String> getOwnerIdsForGroup(String groupId, final Date date){
        return authManagerAdminService.getOwnerIdsForGroup(groupId, date);
    }
    @Override
    @WebMethod
    public HashMap<String, SetStringResponse> getOwnerIdsForGroupSet(final Set<String> groupIdSet, final Date date){
        return authManagerAdminService.getOwnerIdsForGroupSet(groupIdSet, date);
    }
}
