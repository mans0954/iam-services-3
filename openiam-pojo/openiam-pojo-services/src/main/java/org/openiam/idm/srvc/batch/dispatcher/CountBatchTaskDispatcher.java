package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class CountBatchTaskDispatcher extends AbstractBatchTaskDispatcher<BaseSearchServiceRequest<BatchTaskSearchBean>, IntResponse>  {
    public CountBatchTaskDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(BatchTaskAPI openIAMAPI, BaseSearchServiceRequest<BatchTaskSearchBean> request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(batchService.count(request.getSearchBean()));
        return response;
    }
}
