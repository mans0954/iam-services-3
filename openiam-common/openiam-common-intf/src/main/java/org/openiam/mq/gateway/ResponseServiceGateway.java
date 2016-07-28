package org.openiam.mq.gateway;

import org.openiam.mq.dto.MQResponse;

/**
 * Created by alexander on 06/07/16.
 */
public interface ResponseServiceGateway {
    void send(String routingKey, final MQResponse<?> response, final byte[] correlationId);
}
