package org.openiam.authmanager.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.openiam.authmanager.ws.response.MenuSaveResponse;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", name = "AuthorizationManagerMenuWebService")
public interface AuthorizationManagerMenuWebService {

	@WebMethod
	public AuthorizationMenu getMenuTreeForUserId(
			@WebParam(name = "request", targetNamespace = "") final MenuRequest request
	);
	
	@WebMethod
	public AuthorizationMenu getMenuTree(@WebParam(name = "menuId", targetNamespace = "") final String menuId);
	
	/**
	 * Called after the menu tree has been validated
	 * @param root - root of the menu tree
	 * @return
	 */
	@WebMethod
	public MenuSaveResponse saveMenuTree(@WebParam(name = "menu", targetNamespace = "") final AuthorizationMenu root);
	
	@WebMethod
	public MenuSaveResponse deleteMenuTree(@WebParam(name = "rootId", targetNamespace = "") final String rootId);
}
