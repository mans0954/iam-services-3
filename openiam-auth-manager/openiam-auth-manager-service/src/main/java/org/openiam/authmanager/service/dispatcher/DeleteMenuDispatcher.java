package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.MenuRequest;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/09/16.
 */
@Component
public class DeleteMenuDispatcher extends AbstractAPIDispatcher<IdServiceRequest, MenuSaveResponse, AMMenuAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public DeleteMenuDispatcher() {
        super(MenuSaveResponse.class);
    }

    @Override
    protected MenuSaveResponse processingApiRequest(AMMenuAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        MenuSaveResponse response = new MenuSaveResponse();

        try{
            menuService.deleteMenuTree(request.getId());
        } catch (AuthorizationMenuException ex){
            log.error(ex.getCode().name(), ex);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ex.getCode());
            response.setProblematicMenuName(ex.getMenuName());
        }
        return response;
    }
}
