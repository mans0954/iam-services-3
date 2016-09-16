package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.EntityOwnerRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.EntityOwnerResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMAdminAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/09/16.
 */
@Component
public class MenuTreeDispatcher extends AbstractAPIDispatcher<MenuRequest, AuthorizationMenuResponse, AMMenuAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public MenuTreeDispatcher() {
        super(AuthorizationMenuResponse.class);
    }

    @Override
    protected AuthorizationMenuResponse processingApiRequest(AMMenuAPI openIAMAPI, MenuRequest request) throws BasicDataServiceException {
        AuthorizationMenuResponse response = new AuthorizationMenuResponse();
        switch (openIAMAPI){
            case MenuTree:
                response.setMenu(menuService.getMenuTree(request.getMenuRoot(), request.getLanguage()));
                break;
            case MenuTreeForUser:
                response.setMenu(menuService.getMenuTreeForUserId(request.getMenuRoot(), request.getMenuName(), request.getUserId(), request.getLanguage()));
                break;
            case NonCachedMenuTree:
                response.setMenu(menuService.getNonCachedMenuTree(request.getMenuRoot(), request.getPrincipalId(), request.getPrincipalType(), request.getLanguage()));
                break;
        }
        return response;
    }
}
