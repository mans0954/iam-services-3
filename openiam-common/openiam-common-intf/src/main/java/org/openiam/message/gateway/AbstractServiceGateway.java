package org.openiam.message.gateway;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.AbstractMQMessage;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.gateway.ServiceGateway;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by alexander on 06/07/16.
 */
public abstract class AbstractServiceGateway<Data extends AbstractMQMessage> implements ServiceGateway<Data> {

    public void send(OpenIAMQueue queue, Data request) {
        request.setCorrelationID(generateCorrelationId());
        doSend(queue, request);
    }

    protected String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    protected abstract void doSend(OpenIAMQueue queue, Data request);
}
