package org.openiam.message.gateway;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractRequestServiceGateway extends AbstractServiceGateway<OpenIAMMQRequest> implements RequestServiceGareway {


    @Override
    public OpenIAMMQResponse sendAndReceive(OpenIAMQueue queue, OpenIAMMQRequest request) {
        request.setCorrelationID(generateCorrelationId());
        return doSendAndReceive(queue, request);
    }

    protected abstract OpenIAMMQResponse doSendAndReceive(OpenIAMQueue queue, OpenIAMMQRequest request);
}
