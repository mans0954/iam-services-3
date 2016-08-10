package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.CachedContentProviderDispatcher;
import org.openiam.am.srvc.service.dispatcher.CachedURIPatternDispatcher;
import org.openiam.am.srvc.service.dispatcher.URIFederationMetadataDispatcher;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class URIFederationListener extends AbstractRabbitMQListener {
    @Autowired
    private URIFederationMetadataDispatcher uriFederationMetadataDispatcher;

    @Autowired
    private CachedContentProviderDispatcher cachedContentProviderDispatcher;
    @Autowired
    private CachedURIPatternDispatcher cachedURIPatternDispatcher;

    public URIFederationListener() {
        super(OpenIAMQueue.URIFederationQueue);
    }

    @Override
    protected void doOnMessage(MQRequest message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPI apiName = message.getRequestApi();
        switch (apiName){
            case URIFederationMetadata:
                addTask(uriFederationMetadataDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CachedContentProviderGet:
                addTask(cachedContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CachedURIPatternGet:
                addTask(cachedURIPatternDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
