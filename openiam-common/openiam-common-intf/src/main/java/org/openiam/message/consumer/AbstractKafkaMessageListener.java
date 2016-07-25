package org.openiam.message.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Created by alexander on 21/07/16.
 */
public abstract class AbstractKafkaMessageListener  extends AbstractMessageListener<OpenIAMMQRequest> implements AcknowledgingMessageListener {
    public AbstractKafkaMessageListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }

    public  void  onMessage(ConsumerRecord record, Acknowledgment acknowledgment){
        try {
            log.debug("AbstractApiRequestListener caught message");
            log.debug("Message : {}", record);

            OpenIAMMQRequest request = (OpenIAMMQRequest)record.value();
            log.info("Caught request : {} correlationId: {}", request, request.getCorrelationID());
            doOnMessage((OpenIAMMQRequest)record.value());
        } catch (Exception e) {
            log.warn("Cannot process message now. pus it back to queue: {}", e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    protected abstract void doOnMessage(OpenIAMMQRequest message) throws Exception;
}
