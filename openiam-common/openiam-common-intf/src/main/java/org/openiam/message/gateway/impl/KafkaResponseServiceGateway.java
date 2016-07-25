package org.openiam.message.gateway.impl;

import org.openiam.message.dto.OpenIAMMQResponse;
import org.openiam.message.gateway.AbstractResponseServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created by alexander on 22/07/16.
 */
public class KafkaResponseServiceGateway extends AbstractResponseServiceGateway {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    protected void doSend(String queueName, OpenIAMMQResponse request) {

    }
}
