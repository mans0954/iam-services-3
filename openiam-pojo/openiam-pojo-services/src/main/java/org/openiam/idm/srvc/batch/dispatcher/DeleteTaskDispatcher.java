package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.StartBatchTaskRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class DeleteTaskDispatcher extends AbstractBatchTaskDispatcher<IdServiceRequest, Response>  {
    public DeleteTaskDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(BatchTaskAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        switch (openIAMAPI){
            case Delete:
                batchService.delete(request.getId());
                break;
            case DeleteScheduledTask:
                batchService.deleteScheduledTask(request.getId());
                break;
        }
        return new Response();
    }
}
