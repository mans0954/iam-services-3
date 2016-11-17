package org.openiam.mq.gateway;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.api.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;

/**
 * Created by alexander on 07/07/16.
 */
public interface RequestServiceGateway {
    Response sendAndReceive(MqQueue queue, OpenIAMAPI api, final BaseServiceRequest request);
    void send(MqQueue queue, OpenIAMAPI api,final BaseServiceRequest request);
    void publish(MqQueue queue, OpenIAMAPI api, final BaseServiceRequest request);
}
