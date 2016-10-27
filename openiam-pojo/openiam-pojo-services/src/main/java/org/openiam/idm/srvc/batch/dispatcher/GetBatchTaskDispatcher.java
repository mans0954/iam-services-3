package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.BatchTaskResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class GetBatchTaskDispatcher extends AbstractBatchTaskDispatcher<IdServiceRequest, BatchTaskResponse> {

    public GetBatchTaskDispatcher() {
        super(BatchTaskResponse.class);
    }

    @Override
    protected BatchTaskResponse processingApiRequest(BatchTaskAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        BatchTaskResponse response = new BatchTaskResponse();
        response.setValue(batchService.findDto(request.getId()));
        return response;
    }
}
