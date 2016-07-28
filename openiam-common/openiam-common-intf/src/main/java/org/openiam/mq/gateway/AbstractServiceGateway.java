package org.openiam.mq.gateway;

import org.springframework.amqp.rabbit.core.RabbitGatewaySupport;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by alexander on 06/07/16.
 */
public abstract class AbstractServiceGateway extends RabbitGatewaySupport {
    @Value("${org.openiam.mq.broker.encoding}")
    protected String encoding;

    protected byte[] generateCorrelationId() throws UnsupportedEncodingException {
        return UUID.randomUUID().toString().getBytes(encoding);
    }

    protected String getEncoding(){
        return this.encoding;
    }
}
