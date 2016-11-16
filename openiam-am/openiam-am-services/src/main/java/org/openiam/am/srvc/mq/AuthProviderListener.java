package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.constants.queue.am.AuthProviderQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
@RabbitListener(id="authProviderListener",
        queues = "#{AuthProviderQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AuthProviderListener  extends AbstractListener<AuthProviderAPI> {

    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    public AuthProviderListener(AuthProviderQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<AuthProviderAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<AuthProviderAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(AuthProviderAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                AuthProviderTypeListResponse response = new AuthProviderTypeListResponse();
                switch (api){
                    case GetAuthProviderTypeList:
                        response.setAuthProviderTypeList(authProviderService.getAuthProviderTypeList());
                        break;
                    case GetSocialAuthProviderTypeList:
                        response.setAuthProviderTypeList(authProviderService.getSocialAuthProviderTypeList());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<AuthProviderAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<AuthProviderAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(AuthProviderAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindAuthProviders:
                        response = new AuthProviderListResponse();
                        ((AuthProviderListResponse)response).setAuthProviderList(authProviderService.findAuthProviderBeans(((BaseSearchServiceRequest<AuthProviderSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize()));
                        return response;
                    case CountAuthProviders:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(authProviderService.countAuthProviderBeans(((BaseSearchServiceRequest<AuthProviderSearchBean>)request).getSearchBean()));
                        return response;
                    case FindAuthAttributes:
                        response = new AuthAttributeListResponse();
                        ((AuthAttributeListResponse)response).setAttributeList(authProviderService.findAuthAttributeBeans(((BaseSearchServiceRequest<AuthAttributeSearchBean>)request).getSearchBean(), request.getSize(), request.getFrom()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<AuthProviderAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<AuthProviderAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(AuthProviderAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAuthProviderType:
                        response = new AuthProviderTypeResponse();
                        ((AuthProviderTypeResponse)response).setAuthProviderType(authProviderService.getAuthProviderType(request.getId()));
                        return response;
                    case GetAuthProvider:
                        response = new AuthProviderResponse();
                        ((AuthProviderResponse)response).setValue(authProviderService.getProvider(request.getId()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<AuthProviderAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<AuthProviderAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(AuthProviderAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case AddProviderType:
                        response = new Response(ResponseStatus.SUCCESS);
                        authProviderService.addProviderType(((BaseCrudServiceRequest<AuthProviderType>)request).getObject());
                        return response;
                    case SaveAuthProvider:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(authProviderService.saveAuthProvider(((BaseCrudServiceRequest<AuthProvider>)request).getObject(), request.getRequesterId()));
                        return response;
                    case DeleteAuthProvider:
                        response = new Response();
                        authProviderService.deleteAuthProvider(((BaseCrudServiceRequest<AuthProvider>)request).getObject().getId());
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }
}
