package org.openiam.srvc.am;


import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.AuthorizationMenu;
import org.openiam.base.request.*;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.mq.constants.api.AMCacheAPI;
import org.openiam.mq.constants.api.AMMenuAPI;
import org.openiam.mq.constants.queue.am.AMCacheQueue;
import org.openiam.mq.constants.queue.am.AMMenuQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.am.AuthorizationManagerMenuWebService",
	targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", 
	portName = "AuthorizationManagerMenuWebServicePort",
	serviceName = "AuthorizationManagerMenuWebService")
@Service("authorizationManagerMenuWebService")
public class AuthorizationManagerMenuWebServiceImpl  extends AbstractApiService implements AuthorizationManagerMenuWebService {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceImpl.class);
	@Autowired
	private AMCacheQueue amCacheQueue;
	@Autowired
	public AuthorizationManagerMenuWebServiceImpl(AMMenuQueue queue) {
		super(queue);
	}

	@Override
	public AuthorizationMenu getMenuTreeForUserId(final MenuRequest request) {
		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.MenuTreeForUser, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}

	@Override
	public AuthorizationMenu getMenuTree(final String menuId) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);

		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.MenuTree, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}
	

	@Override
	public AuthorizationMenu getNonCachedMenuTree(final String menuId, final String principalId, final String principalType) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);
		request.setPrincipalId(principalId);
		request.setPrincipalType(principalType);

		AuthorizationMenuResponse response= this.manageApiRequest(AMMenuAPI.NonCachedMenuTree, request, AuthorizationMenuResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getMenu();
	}
	
	@Override
	public MenuSaveResponse deleteMenuTree(final String rootId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(rootId);
		return this.manageApiRequest(AMMenuAPI.DeleteMenuTree, request, MenuSaveResponse.class);
	}

	@Override
	public boolean isUserAuthenticatedToMenuWithURL(final String userId, final String url, final String menuId, final boolean defaultResult) {
		MenuRequest request = new MenuRequest();
		request.setMenuRoot(menuId);
		request.setUserId(userId);
		request.setUrl(url);
		request.setDefaultResult(defaultResult);

		BooleanResponse response= this.manageApiRequest(AMMenuAPI.IsUserAuthenticatedToMenuWithURL, request, BooleanResponse.class);
		if(response.isFailure()){
			return false;
		}
		return response.getValue();
	}

	@Override
	public void sweep() {
		this.publish(amCacheQueue, AMCacheAPI.RefreshAMMenu, new EmptyServiceRequest());
	}

	@Override
	public MenuSaveResponse saveMenuTree(final AuthorizationMenu root) {
		AuthorizationMenuRequest request = new AuthorizationMenuRequest();
		request.setMenu(root);
		return this.manageApiRequest(AMMenuAPI.SaveMenuTree, request, MenuSaveResponse.class);
	}

	@Override
	public Response entitle(final MenuEntitlementsRequest menuEntitlementsRequest) {
		return this.manageApiRequest(AMMenuAPI.Entitle, menuEntitlementsRequest, Response.class);
	}

}
