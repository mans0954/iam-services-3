package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.meta.service.MetadataTypeGetDispatcher;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class MetaDataListener extends AbstractRabbitMQListener<OpenIAMAPICommon> {
    @Autowired
    private MetadataTypeGetDispatcher metadataTypeGetDispatcher;

    public MetaDataListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public MetaDataListener() {
        this(OpenIAMQueue.MetadataQueue);
    }


    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OpenIAMAPICommon> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPICommon apiName = message.getRequestApi();
        switch (apiName){
            case MetadataTypeGet:
                addTask(metadataTypeGetDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
