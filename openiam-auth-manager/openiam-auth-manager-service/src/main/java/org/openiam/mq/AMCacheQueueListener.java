package org.openiam.mq;

import org.openiam.authmanager.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AMCacheAPI;
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
public class AMCacheQueueListener extends AbstractRabbitMQListener<AMCacheAPI> {
    @Autowired
    private SweepManagerDispatcher sweepManagerDispatcher;
    @Autowired
    private SweepMenuDispatcher sweepMenuDispatcher;

    public AMCacheQueueListener() {
        super(OpenIAMQueue.AMCacheQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AMCacheAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AMCacheAPI api = message.getRequestApi();
        switch (api){
            case RefreshAMManager:
                addTask(sweepManagerDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case RefreshAMMenu:
                addTask(sweepMenuDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
        }
    }
}
