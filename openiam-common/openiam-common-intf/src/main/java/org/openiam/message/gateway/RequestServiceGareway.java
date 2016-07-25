package org.openiam.message.gateway;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;

/**
 * Created by alexander on 07/07/16.
 */
public interface RequestServiceGareway extends ServiceGateway<OpenIAMMQRequest> {
    OpenIAMMQResponse sendAndReceive(OpenIAMQueue queue, final OpenIAMMQRequest request);
}
