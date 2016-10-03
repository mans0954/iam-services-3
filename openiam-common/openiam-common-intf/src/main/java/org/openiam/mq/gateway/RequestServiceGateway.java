package org.openiam.mq.gateway;

import org.openiam.mq.constants.MqQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;

/**
 * Created by alexander on 07/07/16.
 */
public interface RequestServiceGateway {
    MQResponse sendAndReceive(MqQueue queue, final MQRequest request);
    void send(MqQueue queue, final MQRequest response);
    void send(String exchange, String routingKey, final MQRequest request) ;
    void publish(MqQueue queue, final MQRequest request);
}
