package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.mngsys.service.dispatcher.GetAllManagedSysDispatcher;
import org.openiam.mq.constants.ManagedSystemAPI;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
public class ManagedSystemMessageListener extends AbstractRabbitMQListener<ManagedSystemAPI> {
    @Autowired
    private GetAllManagedSysDispatcher getAllManagedSysDispatcher;

    public ManagedSystemMessageListener() {
        super(OpenIAMQueue.ManagedSysQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, ManagedSystemAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        ManagedSystemAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetAllManagedSys:
                addTask(getAllManagedSysDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
