package org.openiam.mq;

import org.openiam.authmanager.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AMAdminAPI;
import org.openiam.mq.constants.AMMenuAPI;
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
public class AMMenuQueueListener extends AbstractRabbitMQListener<AMMenuAPI> {
    @Autowired
    private MenuTreeDispatcher menuTreeDispatcher;
    @Autowired
    private UserAuthenticatedToMenuDispatcher userAuthenticatedToMenuDispatcher;
    @Autowired
    private MenuEntitleDispatcher menuEntitleDispatcher;
    @Autowired
    private DeleteMenuDispatcher deleteMenuDispatcher;
    @Autowired
    private SaveMenuTreeDispatcher saveMenuTreeDispatcher;

    public AMMenuQueueListener() {
        super(OpenIAMQueue.AMMenuQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AMMenuAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AMMenuAPI api = message.getRequestApi();
        switch (api){
            case MenuTree:
            case MenuTreeForUser:
            case NonCachedMenuTree:
                addTask(menuTreeDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case IsUserAuthenticatedToMenuWithURL:
                addTask(userAuthenticatedToMenuDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case Entitle:
                addTask(menuEntitleDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case DeleteMenuTree:
                addTask(deleteMenuDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case SaveMenuTree:
                addTask(saveMenuTreeDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
        }
    }
}
