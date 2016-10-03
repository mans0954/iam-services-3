package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class OAuthListener extends AbstractRabbitMQListener<OAuthAPI> {
    @Autowired
    private GetClientScopesDispatcher getClientScopesDispatcher;
    @Autowired
    private GetOAuthClientDispatcher getOAuthClientDispatcher;
    @Autowired
    private GetOAuthTokenDispatcher getOAuthTokenDispatcher;
    @Autowired
    private GetOAuthCodeDispatcher getOAuthCodeDispatcher;
    @Autowired
    private SaveClientScopesDispatcher saveClientScopesDispatcher;
    @Autowired
    private SaveOAuthCodeDispatcher saveOAuthCodeDispatcher;
    @Autowired
    private SaveOAuthTokenDispatcher saveOAuthTokenDispatcher;

    public OAuthListener() {
        super(OpenIAMQueue.OAuthQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OAuthAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OAuthAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetAuthorizedScopes:
            case GetScopesForAuthrorization:
                addTask(getClientScopesDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetClient:
            case GetCachedOAuthProviderById:
            case GetCachedOAuthProviderByName:
                addTask(getOAuthClientDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetOAuthToken:
            case GetOAuthTokenByRefreshToken:
                addTask(getOAuthTokenDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetOAuthCode:
                addTask(getOAuthCodeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveClientScopeAuthorization:
                addTask(saveClientScopesDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveOAuthCode:
                addTask(saveOAuthCodeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveOAuthToken:
                addTask(saveOAuthTokenDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
