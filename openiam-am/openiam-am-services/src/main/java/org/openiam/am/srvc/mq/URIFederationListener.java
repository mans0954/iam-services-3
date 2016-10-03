package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class URIFederationListener extends AbstractRabbitMQListener<URIFederationAPI> {
    @Autowired
    private URIFederationMetadataDispatcher uriFederationMetadataDispatcher;

    @Autowired
    private CachedContentProviderDispatcher cachedContentProviderDispatcher;
    @Autowired
    private CachedURIPatternDispatcher cachedURIPatternDispatcher;
    @Autowired
    private CertificateLoginDispatcher certificateLoginDispatcher;
    @Autowired
    private SSOLoginDispatcher ssoLoginDispatcher;

    public URIFederationListener() {
        super(OpenIAMQueue.URIFederationQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, URIFederationAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        URIFederationAPI apiName = message.getRequestApi();
        switch (apiName){
            case URIFederationMetadata:
            case FederateProxyURI:
                addTask(uriFederationMetadataDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CachedContentProviderGet:
                addTask(cachedContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CachedURIPatternGet:
                addTask(cachedURIPatternDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetCookieFromProxyURIAndPrincipal:
                addTask(ssoLoginDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetIdentityFromCert:
                addTask(certificateLoginDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
