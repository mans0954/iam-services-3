package org.openiam.mq;

import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.openiam.mq.processor.AbstractAPIDispatcher;

/**
 * Created by alexander on 01/08/16.
 */
public abstract class AbstractAttributeListener extends AbstractRabbitMQListener {
    public AbstractAttributeListener(OpenIAMQueue queue) {
        super(queue);
    }


    @Override
    protected void doOnMessage(MQRequest message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPICommon apiName = (OpenIAMAPICommon)message.getRequestApi();
        switch (apiName){
            case UpdateAttributesByMetadata:
                addTask(getProcessorTask(), correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }

    protected abstract AbstractAPIDispatcher getProcessorTask();
}
