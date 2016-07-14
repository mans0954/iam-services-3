package org.openiam.message.gateway;

import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractRequestServiceGateway extends AbstractServiceGateway<OpenIAMMQRequest> implements RequestServiceGareway {


    @Override
    public OpenIAMMQResponse sendAndReceive(String queueName, OpenIAMMQRequest request) {
        return doSendAndReceive(queueName, request);
    }

    protected abstract OpenIAMMQResponse doSendAndReceive(String queueName, OpenIAMMQRequest request);
}
