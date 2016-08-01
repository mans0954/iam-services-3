package org.openiam.mq.gateway;

import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;

/**
 * Created by alexander on 07/07/16.
 */
public interface RequestServiceGateway {
    MQResponse sendAndReceive(OpenIAMQueue queue, final MQRequest request);
    void send(OpenIAMQueue queue, final MQRequest response);
    void send(String exchange, String routingKey, final MQRequest request) ;
}
