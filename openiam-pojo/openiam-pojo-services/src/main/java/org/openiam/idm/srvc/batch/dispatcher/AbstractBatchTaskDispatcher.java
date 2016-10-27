package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.mq.constants.BatchTaskAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alexander on 25/10/16.
 */
public abstract class AbstractBatchTaskDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response>
        extends AbstractAPIDispatcher<RequestBody, ResponseBody, BatchTaskAPI> {
    @Autowired
    protected BatchService batchService;

    public AbstractBatchTaskDispatcher(Class<ResponseBody> responseBodyClass) {
        super(responseBodyClass);
    }
}
