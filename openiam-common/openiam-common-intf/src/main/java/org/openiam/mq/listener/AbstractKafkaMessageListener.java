package org.openiam.mq.listener;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.util.OpenIAMUtils;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Created by alexander on 21/07/16.
 */
public abstract class AbstractKafkaMessageListener  extends AbstractMessageListener implements AcknowledgingMessageListener {
    public AbstractKafkaMessageListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }

    public  void  onMessage(ConsumerRecord record, Acknowledgment acknowledgment){
        try {
            log.debug("AbstractApiRequestListener caught mq");
            log.debug("Message : {}", record);

            MQRequest request = (MQRequest)record.value();
            log.info("Caught request : {} correlationId: {}", request, OpenIAMUtils.byteArrayToString(request.getCorrelationId()));
            boolean isAsync = StringUtils.isBlank(request.getReplyTo());
            doOnMessage(request, request.getCorrelationId(),isAsync);
        } catch (Exception e) {
            log.warn("Cannot process mq now. pus it back to queue: {}", e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

}
