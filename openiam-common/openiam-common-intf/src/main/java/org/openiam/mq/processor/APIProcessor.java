package org.openiam.mq.processor;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.mq.dto.MQRequest;

/**
 * Created by alexander on 07/07/16.
 */
public interface APIProcessor<RequestBody extends BaseServiceRequest, ResponseBody extends Response> {
    void pushToQueue(MQRequest<RequestBody> apiRequest);
    MQRequest<RequestBody> pullFromQueue() throws InterruptedException ;
    void processRequest(MQRequest<RequestBody> apiRequest);
}
