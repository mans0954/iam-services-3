package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.BatchTaskSaveRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class SaveTaskDispatcher extends AbstractBatchTaskDispatcher<BatchTaskSaveRequest, StringResponse>  {
    public SaveTaskDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(BatchTaskAPI openIAMAPI, BatchTaskSaveRequest request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(batchService.save(request.getObject(), request.isPurgeNonExecutedTasks()));
        return response;
    }
}
