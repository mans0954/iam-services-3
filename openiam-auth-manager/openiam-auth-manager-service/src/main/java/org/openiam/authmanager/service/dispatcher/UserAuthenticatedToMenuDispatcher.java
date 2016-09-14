package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/09/16.
 */
@Component
public class UserAuthenticatedToMenuDispatcher extends AbstractAPIDispatcher<MenuRequest, BooleanResponse, AMMenuAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public UserAuthenticatedToMenuDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(AMMenuAPI openIAMAPI, MenuRequest request) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        response.setValue(menuService.isUserAuthenticatedToMenuWithURL(request.getUserId(), request.getUrl(), request.getMenuRoot(), request.isDefaultResult()));
        return response;
    }
}
