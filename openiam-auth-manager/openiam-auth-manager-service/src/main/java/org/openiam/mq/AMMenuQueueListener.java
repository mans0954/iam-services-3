package org.openiam.mq;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.*;
import org.openiam.base.response.AuthorizationMenuResponse;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AMMenuQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="amMenuQueueListener",
        queues = "#{AMMenuQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AMMenuQueueListener extends AbstractListener<AMMenuAPI> {
    @Autowired
    private AuthorizationManagerMenuService menuService;

    @Autowired
    public AMMenuQueueListener(AMMenuQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMMenuAPI api, MenuRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMMenuAPI, MenuRequest>(){
            @Override
            public Response doProcess(AMMenuAPI api, MenuRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case MenuTree:
                        response = new AuthorizationMenuResponse();
                        ((AuthorizationMenuResponse)response).setMenu(menuService.getMenuTree(request.getMenuRoot(), request.getLanguage()));
                        break;
                    case MenuTreeForUser:
                        response = new AuthorizationMenuResponse();
                        ((AuthorizationMenuResponse)response).setMenu(menuService.getMenuTreeForUserId(request.getMenuRoot(), request.getMenuName(), request.getUserId(), request.getLanguage()));
                        break;
                    case NonCachedMenuTree:
                        response = new AuthorizationMenuResponse();
                        ((AuthorizationMenuResponse)response).setMenu(menuService.getNonCachedMenuTree(request.getMenuRoot(), request.getPrincipalId(), request.getPrincipalType(), request.getLanguage()));
                        break;
                    case IsUserAuthenticatedToMenuWithURL:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(menuService.isUserAuthenticatedToMenuWithURL(request.getUserId(), request.getUrl(), request.getMenuRoot(), request.isDefaultResult()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMMenuAPI api, MenuEntitlementsRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMMenuAPI, MenuEntitlementsRequest>(){
            @Override
            public Response doProcess(AMMenuAPI api, MenuEntitlementsRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Entitle:
                        response = new Response();
                        menuService.entitle(request);
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AMMenuAPI api, AuthorizationMenuRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AMMenuAPI, AuthorizationMenuRequest>(){
            @Override
            public Response doProcess(AMMenuAPI api, AuthorizationMenuRequest request) throws BasicDataServiceException {
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
        });
    }

    protected RequestProcessor<AMMenuAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<AMMenuAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(AMMenuAPI amMenuAPI, IdServiceRequest request) throws BasicDataServiceException {
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
        };
    }
}
