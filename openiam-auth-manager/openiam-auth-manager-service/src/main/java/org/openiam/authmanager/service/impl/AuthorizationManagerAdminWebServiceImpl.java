package org.openiam.authmanager.service.impl;

import javax.jws.WebService;

import org.openiam.authmanager.model.EntitlementsMatrix;
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
	public EntitlementsMatrix getEntitlementsMatrix(final String entityId, final String entityType) {
		return authManagerAdminService.getEntitlementsMatrix(entityId, entityType);
	}

}
