package org.openiam.authmanager.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.ws.request.MenuEntitlementsRequest;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.openiam.authmanager.ws.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", name = "AuthorizationManagerMenuWebService")
public interface AuthorizationManagerMenuWebService {

	@WebMethod
	public boolean isUserAuthenticatedToMenuWithURL(
			@WebParam(name = "userId", targetNamespace = "") final String userId, 
			@WebParam(name = "url", targetNamespace = "") final String url,
			@WebParam(name = "menuId", targetNamespace = "") final String menuId,
			@WebParam(name = "defaultResult", targetNamespace = "") final boolean defaultResult);
	
	@WebMethod
	public AuthorizationMenu getMenuTreeForUserId(
			final @WebParam(name = "request", targetNamespace = "") MenuRequest request,
			final @WebParam(name="language", targetNamespace = "") Language language
	);
	
	@WebMethod
	public AuthorizationMenu getMenuTree(
			@WebParam(name = "menuId", targetNamespace = "") final String menuId,
			final @WebParam(name="language", targetNamespace = "") Language language
	);
	
	/**
	 * Called after the menu tree has been validated
	 * @param root - root of the menu tree
	 * @return
	 */
	@WebMethod
	public MenuSaveResponse saveMenuTree(@WebParam(name = "menu", targetNamespace = "") final AuthorizationMenu root);
	
	@WebMethod
	public MenuSaveResponse deleteMenuTree(@WebParam(name = "rootId", targetNamespace = "") final String rootId);
	
	/**
	 * This method gets a non-cached version of a user's, group's, role's, or resource's entitlements to a particular tree.
	 * It should NOT be called by anything that requires good performance, as this method will make lots and lots of DB calls
	 * before completing.  It is designed for use ONLY for Admin purposes, nothing more.  If you call this method outside of
	 * an Administrative console - don't call it.
	 * @param menuId - ID of the menu
	 * @param principalId - the "id" of the user, group, role, or resource
	 * @param principalType - the "type" represented by the <p>principalId</p>.  Valid values are 'group', 'role', 'user', or 'resource'
	 * @return An AuthorizationMenu representing the tree, and the entity's entitlements to this tree.  Returns null if the menu can't be found,
	 * or of the principal of the type can't be found.
	 */
	@WebMethod
	public AuthorizationMenu getNonCachedMenuTree(final @WebParam(name="menuId", targetNamespace = "") String menuId,
												  final @WebParam(name="principalId", targetNamespace = "") String principalId,
												  final @WebParam(name="principalType", targetNamespace = "") String principalType,
												  final @WebParam(name="language", targetNamespace = "") Language language);
	
	@WebMethod
	public Response entitle(final @WebParam(name="menuEntitlementsRequest", targetNamespace = "") MenuEntitlementsRequest menuEntitlementsRequest);
}
