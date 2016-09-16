package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.AuthorizationMenuRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/09/16.
 */
@Component
public class SaveMenuTreeDispatcher extends AbstractAPIDispatcher<AuthorizationMenuRequest, MenuSaveResponse, AMMenuAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public SaveMenuTreeDispatcher() {
        super(MenuSaveResponse.class);
    }

    @Override
    protected MenuSaveResponse processingApiRequest(AMMenuAPI openIAMAPI, AuthorizationMenuRequest request) throws BasicDataServiceException {
        MenuSaveResponse response = new MenuSaveResponse();
        try{
            menuService.saveMenuTree(request.getMenu());
        } catch (AuthorizationMenuException ex){
            log.error(ex.getCode().name(), ex);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ex.getCode());
            response.setProblematicMenuName(ex.getMenuName());
        }
        return response;
    }
}
