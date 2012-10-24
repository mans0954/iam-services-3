package org.openiam.authmanager.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.ws.request.MenuRequest;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", name = "AuthorizationManagerMenuWebService")
public interface AuthorizationManagerMenuWebService {

	@WebMethod
	public AuthorizationMenu getMenuTreeForUserId(
			@WebParam(name = "request", targetNamespace = "") final MenuRequest request
	);
}
