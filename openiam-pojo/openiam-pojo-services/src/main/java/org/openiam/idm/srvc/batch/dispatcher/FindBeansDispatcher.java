package org.openiam.idm.srvc.batch.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.BatchTaskListResponse;
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
public class FindBeansDispatcher extends AbstractBatchTaskDispatcher<BaseSearchServiceRequest<BatchTaskSearchBean>, BatchTaskListResponse>  {
    public FindBeansDispatcher() {
        super(BatchTaskListResponse.class);
    }

    @Override
    protected BatchTaskListResponse processingApiRequest(BatchTaskAPI openIAMAPI, BaseSearchServiceRequest<BatchTaskSearchBean> request) throws BasicDataServiceException {
        BatchTaskListResponse response = new BatchTaskListResponse();
        response.setList(batchService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
