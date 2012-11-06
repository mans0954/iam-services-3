package org.openiam.authmanager.service.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerMenuWebService;
import org.openiam.authmanager.ws.request.MenuRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.authmanager.service.AuthorizationManagerMenuWebService", 
	targetNamespace = "urn:idm.openiam.org/srvc/authorizationmanager/menu/service", 
	portName = "AuthorizationManagerMenuWebServicePort",
	serviceName = "AuthorizationManagerMenuWebService")
@Service("authorizationManagerMenuWebService")
public class AuthorizationManagerMenuWebServiceImpl implements AuthorizationManagerMenuWebService {

	private static final Log log = LogFactory.getLog(AuthorizationManagerMenuWebServiceImpl.class);
	
	@Autowired
	private AuthorizationManagerMenuService menuService;
	
	@Override
	public AuthorizationMenu getMenuTreeForUserId(final MenuRequest request) {
		final StopWatch sw = new StopWatch();
		sw.start();
		AuthorizationMenu retVal = null;
		if(request != null) {
			if(StringUtils.isNotEmpty(request.getUserId())) {
				retVal = menuService.getMenuTree(request.getMenuRoot(), request.getUserId());
			} else if(request.getLoginId() != null) {
				final AuthorizationManagerLoginId login = request.getLoginId();
				retVal = menuService.getMenuTree(request.getMenuRoot(), login.getDomain(), login.getLogin(), login.getManagedSysId());
			}
		}
		sw.stop();
		if(log.isDebugEnabled()) {
			log.debug(String.format("getMenuTreeForUserId: request: %s, time: %s ms", request, sw.getTime()));
		}
		return retVal;
	}

	@Override
	public AuthorizationMenu getMenuTree(final String menuId) {
		return menuService.getMenuTree(menuId);
	}
}
