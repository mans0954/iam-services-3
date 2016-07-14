package org.openiam.message.gateway;

import org.openiam.message.dto.OpenIAMMQRequest;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by alexander on 06/07/16.
 */
public abstract class AbstractServiceGateway<Data> implements ServiceGateway<Data>{

    public void send(String queueName, Data request) {
        doSend(queueName, request);
    }

    protected String generateCorrelationId() throws UnsupportedEncodingException {
        return UUID.randomUUID().toString();
    }

    protected abstract void doSend(String queueName, Data request);
}
