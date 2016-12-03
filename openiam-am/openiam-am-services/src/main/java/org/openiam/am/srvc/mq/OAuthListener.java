package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.am.srvc.dto.OAuthScopesResponse;
import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.*;
import org.openiam.base.request.model.OAuthClientScopeModel;
import org.openiam.base.response.data.AuthProviderResponse;
import org.openiam.base.response.data.OAuthCodeResponse;
import org.openiam.base.response.data.OAuthTokenResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.OAuthAPI;
import org.openiam.mq.constants.queue.am.OAuthQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
@RabbitListener(id="oauthListener",
        queues = "#{OAuthQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class OAuthListener extends AbstractListener<OAuthAPI> {

    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    public OAuthListener(OAuthQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<OAuthAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<OAuthAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(OAuthAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case CleanAuthorizedScopes:
                        authProviderService.sweepAuthorizedScopes();
                        return new Response();
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }
    @Override
    protected RequestProcessor<OAuthAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<OAuthAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(OAuthAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetClient:
                        response = new AuthProviderResponse();
                        ((AuthProviderResponse)response).setValue(authProviderService.getOAuthClient(request.getId()));
                        break;
                    case GetCachedOAuthProviderById:
                        response = new AuthProviderResponse();
                        ((AuthProviderResponse)response).setValue(authProviderService.getCachedOAuthProviderById(request.getId()));
                        break;
                    case GetCachedOAuthProviderByName:
                        response = new AuthProviderResponse();
                        ((AuthProviderResponse)response).setValue(authProviderService.getCachedOAuthProviderByName(request.getId()));
                        break;
                    case GetOAuthToken:
                        response = new OAuthTokenResponse();
                        ((OAuthTokenResponse)response).setValue(authProviderService.getOAuthToken(request.getId()));
                        break;
                    case GetOAuthTokenByRefreshToken:
                        response = new OAuthTokenResponse();
                        ((OAuthTokenResponse)response).setValue(authProviderService.getOAuthTokenByRefreshToken(request.getId()));
                        break;
                    case GetOAuthCode:
                        response = new OAuthCodeResponse();
                        ((OAuthCodeResponse)response).setValue(authProviderService.getOAuthCode(request.getId()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<OAuthAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<OAuthAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(OAuthAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case SaveClientScopeAuthorization:
                        response = new Response();
                        BaseCrudServiceRequest<OAuthClientScopeModel> req = ((BaseCrudServiceRequest<OAuthClientScopeModel>)request);
                        authProviderService.saveClientScopeAuthorization(req.getObject().getId(), req.getObject().getUserId(), req.getObject().getOauthUserClientXrefList());
                        break;
                    case SaveOAuthCode:
                        response = new Response();
                        authProviderService.saveOAuthCode(((BaseCrudServiceRequest<OAuthCode>)request).getObject());
                        break;
                    case SaveOAuthToken:
                        response = new OAuthTokenResponse();
                        ((OAuthTokenResponse)response).setValue(authProviderService.saveOAuthToken(((BaseCrudServiceRequest<OAuthToken>)request).getObject()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) OAuthAPI api, OAuthScopesRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new AbstractListener.RequestProcessor<OAuthAPI, OAuthScopesRequest>(){
            @Override
            public Response doProcess(OAuthAPI api, OAuthScopesRequest request) throws BasicDataServiceException {
                Response response = new OAuthScopesResponse();
                ((OAuthScopesResponse)response).setClientId(request.getClientId());
                switch (api) {
                    case GetAuthorizedScopes:
                        ((OAuthScopesResponse) response).setList(authProviderService.getAuthorizedScopes(request.getClientId(), request.getToken(), request.getLanguage()));
                        break;
                    case GetAuthorizedScopesByUser:
                        ((OAuthScopesResponse) response).setList(authProviderService.getAuthorizedScopesByUser(request.getClientId(), request.getUserId(), request.getLanguage()));
                        break;
                    case GetScopesForAuthrorization:
                        ((OAuthScopesResponse) response).setList(authProviderService.getScopesForAuthrorization(request.getClientId(), request.getUserId(), request.getLanguage()));
                        break;
                    case DeAuthorizeClient:
                        authProviderService.deAuthorizeClient(request.getClientId(), request.getUserId());
                        return new Response();
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
