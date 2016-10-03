package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
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
public class OAuthCacheListener extends AbstractRabbitMQListener<OAuthAPI> {
    @Autowired
    private RefreshOAuthCacheDispatcher refreshOAuthCacheDispatcher;

    public OAuthCacheListener() {
        super(OpenIAMQueue.RefreshOAuthCache);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OAuthAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OAuthAPI apiName = message.getRequestApi();
        addTask(refreshOAuthCacheDispatcher, correlationId, message, apiName, isAsync);
    }
}
