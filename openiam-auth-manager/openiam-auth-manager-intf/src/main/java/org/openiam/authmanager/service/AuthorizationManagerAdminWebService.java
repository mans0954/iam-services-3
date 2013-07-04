package org.openiam.authmanager.service;

import javax.jws.WebService;

import org.openiam.authmanager.model.UserEntitlementsMatrix;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
	public UserEntitlementsMatrix getUserEntitlementsMatrix(final String entityId);
}
