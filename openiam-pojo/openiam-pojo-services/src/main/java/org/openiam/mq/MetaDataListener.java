package org.openiam.mq;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.openiam.idm.srvc.meta.service.MetadataTypeGetProcessor;
import org.openiam.message.constants.OpenIAMAPI;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.consumer.AbstractKafkaMessageListener;
import org.openiam.message.consumer.AbstractRedisMessageListener;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class MetaDataListener extends AbstractKafkaMessageListener {
    @Autowired
    private MetadataTypeGetProcessor metadataTypeGetProcessor;

    public MetaDataListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public MetaDataListener() {
        this(OpenIAMQueue.MetadataQueue);
    }


    @Override
    protected void doOnMessage(OpenIAMMQRequest message) throws Exception {
        OpenIAMAPI apiName = message.getRequestApi();
        boolean isAsync= StringUtils.isBlank(message.getReplyTo());
        switch (apiName){
            case MetadataTypeGet:
                addTask(metadataTypeGetProcessor, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
