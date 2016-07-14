package org.openiam.message.processor;

import org.openiam.base.ws.Response;
import org.openiam.message.dto.OpenIAMMQRequest;

/**
 * Created by alexander on 07/07/16.
 */
public interface APIProcessor<RequestBody, ResponseBody extends Response> {
    void pushToQueue(OpenIAMMQRequest<RequestBody> apiRequest);
    OpenIAMMQRequest<RequestBody> pullFromQueue() throws InterruptedException ;
    void processRequest(OpenIAMMQRequest<RequestBody> apiRequest);
}
