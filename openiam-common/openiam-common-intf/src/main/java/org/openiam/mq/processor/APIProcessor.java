package org.openiam.mq.processor;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.api.OpenIAMAPI;
import org.openiam.mq.dto.MQRequest;

/**
 * Created by alexander on 07/07/16.
 */
public interface APIProcessor<RequestBody extends BaseServiceRequest, ResponseBody extends Response, API extends OpenIAMAPI> {
    void pushToQueue(MQRequest<RequestBody, API> apiRequest);
    MQRequest<RequestBody, API> pullFromQueue() throws InterruptedException ;
    void processRequest(MQRequest<RequestBody,API> apiRequest);
}
