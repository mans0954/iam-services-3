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
public class URIFederationCacheListener extends AbstractRabbitMQListener<URIFederationAPI> {
    @Autowired
    private RefreshUriFederationCacheDispatcher refreshUriFederationCacheDispatcher;

    public URIFederationCacheListener() {
        super(OpenIAMQueue.RefreshUriFederationCache);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, URIFederationAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        URIFederationAPI apiName = message.getRequestApi();
        addTask(refreshUriFederationCacheDispatcher, correlationId, message, apiName, isAsync);
    }
}
