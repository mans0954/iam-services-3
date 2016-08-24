package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.openiam.provision.service.ProvisionDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
public class ProvisionMessageListener extends AbstractRabbitMQListener<OpenIAMAPICommon> {
    @Autowired
    private ProvisionDispatcher provisionDispatcher;

    public ProvisionMessageListener() {
        super(OpenIAMQueue.ProvisionQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OpenIAMAPICommon> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPICommon apiName = message.getRequestApi();
        switch (apiName){
            case UserProvisioning:
                addTask(provisionDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
