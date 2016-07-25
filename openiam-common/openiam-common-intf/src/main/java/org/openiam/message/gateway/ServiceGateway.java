package org.openiam.message.gateway;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;

/**
 * Created by alexander on 06/07/16.
 */
public interface ServiceGateway<Data> {
    void send(OpenIAMQueue queue, final Data request);
}
