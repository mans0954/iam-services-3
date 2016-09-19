package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.ActivitiUserField;
import org.openiam.mq.constants.AuthProviderAPI;
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
public class AuthProviderListener  extends AbstractRabbitMQListener<AuthProviderAPI> {
    @Autowired
    private FindAuthAttributesDispatcher findAuthAttributesDispatcher;
    @Autowired
    private GetAuthProviderTypeDispatcher getAuthProviderTypeDispatcher;
    @Autowired
    private AuthProviderTypeListDispatcher authProviderTypeListDispatcher;
    @Autowired
    private AddAuthProviderTypeDispatcher addAuthProviderTypeDispatcher;
    @Autowired
    private FindAuthProvidersDispatcher findAuthProvidersDispatcher;
    @Autowired
    private CountAuthProvidersDispatcher countAuthProvidersDispatcher;
    @Autowired
    private GetAuthProvidersDispatcher getAuthProvidersDispatcher;
    @Autowired
    private SaveAuthProvidersDispatcher saveAuthProvidersDispatcher;
    @Autowired
    private DeleteAuthProvidersDispatcher deleteAuthProvidersDispatcher;

    public AuthProviderListener() {
        super(OpenIAMQueue.AuthProviderQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AuthProviderAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AuthProviderAPI apiName = message.getRequestApi();
        switch (apiName){
            case FindAuthAttributes:
                addTask(findAuthAttributesDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthProviderType:
                addTask(getAuthProviderTypeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthProviderTypeList:
            case GetSocialAuthProviderTypeList:
                addTask(authProviderTypeListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case AddProviderType:
                addTask(addAuthProviderTypeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindAuthProviders:
                addTask(findAuthProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CountAuthProviders:
                addTask(countAuthProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthProvider:
                addTask(getAuthProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveAuthProvider:
                addTask(saveAuthProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            case DeleteAuthProvider:
                addTask(deleteAuthProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
