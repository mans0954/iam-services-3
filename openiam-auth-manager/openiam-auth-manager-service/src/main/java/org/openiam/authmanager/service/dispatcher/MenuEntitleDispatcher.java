package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/09/16.
 */
@Component
public class MenuEntitleDispatcher extends AbstractAPIDispatcher<MenuEntitlementsRequest, Response, AMMenuAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public MenuEntitleDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AMMenuAPI openIAMAPI, MenuEntitlementsRequest request) throws BasicDataServiceException {
        Response response = new Response();
        menuService.entitle(request);
        return response;
    }
}
