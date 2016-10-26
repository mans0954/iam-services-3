package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.BatchTaskScheduleListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.mq.constants.BatchTaskAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class GetSchedulesForTaskDispatcher extends AbstractBatchTaskDispatcher<BaseSearchServiceRequest<BatchTaskScheduleSearchBean>, BatchTaskScheduleListResponse>  {
    public GetSchedulesForTaskDispatcher() {
        super(BatchTaskScheduleListResponse.class);
    }

    @Override
    protected BatchTaskScheduleListResponse processingApiRequest(BatchTaskAPI openIAMAPI, BaseSearchServiceRequest<BatchTaskScheduleSearchBean> request) throws BasicDataServiceException {
        BatchTaskScheduleListResponse response = new BatchTaskScheduleListResponse();
        response.setList(batchService.getSchedulesForTask(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
