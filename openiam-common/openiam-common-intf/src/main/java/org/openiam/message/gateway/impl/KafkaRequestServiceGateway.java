package org.openiam.message.gateway.impl;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;
import org.openiam.message.gateway.AbstractRequestServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 22/07/16.
 */
@Component("kafkaRequestServiceGateway")
public class KafkaRequestServiceGateway  extends AbstractRequestServiceGateway {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${org.openiam.message.broker.reply.timeout}")
    private Long replyTimeout;

    @Override
    protected void doSend(OpenIAMQueue queue, OpenIAMMQRequest request) {
        kafkaTemplate.send(queue.getQueueName(), queue.getPartitionId(request.getRequestApi()), request.getCorrelationID(), request);
    }
    @Override
    protected OpenIAMMQResponse doSendAndReceive(OpenIAMQueue queue, OpenIAMMQRequest request) {


        kafkaTemplate.send(queue.getQueueName(), queue.getPartitionId(request.getRequestApi()), request.getCorrelationID(), request);
    }


}
