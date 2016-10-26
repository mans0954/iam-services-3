package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.StartBatchTaskRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class RunTaskDispatcher extends AbstractBatchTaskDispatcher<StartBatchTaskRequest, Response>  {
    public RunTaskDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(BatchTaskAPI openIAMAPI, StartBatchTaskRequest request) throws BasicDataServiceException {
        switch (openIAMAPI){
            case Run:
                batchService.run(request.getId(), request.isSynchronous());
                break;
            case Schedule:
                batchService.schedule(request.getId(), request.getWhen());
                break;

        }
        return new Response();
    }
}
