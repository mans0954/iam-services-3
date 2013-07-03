package org.openiam.authmanager.service;

import javax.jws.WebService;

import org.openiam.authmanager.model.EntitlementsMatrix;

@WebService(targetNamespace = "urn:idm.openiam.org/authmanager/service", 
			name = "AuthorizationManagerAdminWebService")
public interface AuthorizationManagerAdminWebService {
	public EntitlementsMatrix getEntitlementsMatrix(final String entityId, final String entityType);
}
