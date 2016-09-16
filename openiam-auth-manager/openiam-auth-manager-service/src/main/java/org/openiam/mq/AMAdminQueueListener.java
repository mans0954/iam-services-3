package org.openiam.mq;

import org.openiam.authmanager.service.dispatcher.OwnerMapRequestDispatcher;
import org.openiam.authmanager.service.dispatcher.UserEntitlementsMatrixDispatcher;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AMAdminAPI;
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
public class AMAdminQueueListener extends AbstractRabbitMQListener<AMAdminAPI> {
    @Autowired
    private UserEntitlementsMatrixDispatcher userEntitlementsMatrixDispatcher;
    @Autowired
    private OwnerMapRequestDispatcher ownerMapRequestDispatcher;

    public AMAdminQueueListener() {
        super(OpenIAMQueue.AMAdminQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AMAdminAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AMAdminAPI api = message.getRequestApi();
        switch (api){
            case UserEntitlementsMatrix:
                addTask(userEntitlementsMatrixDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case OwnerIdsForResourceSet:
            case OwnerIdsForGroupSet:
                addTask(ownerMapRequestDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
        }
    }
}
