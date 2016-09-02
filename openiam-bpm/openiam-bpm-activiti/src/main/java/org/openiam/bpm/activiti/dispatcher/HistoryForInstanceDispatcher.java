package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.TaskHistoryListResponse;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by alexander on 31/08/16.
 */
@Component
public class HistoryForInstanceDispatcher extends AbstractAPIDispatcher<IdServiceRequest, TaskHistoryListResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public HistoryForInstanceDispatcher() {
        super(TaskHistoryListResponse.class);
    }

    @Override
    protected TaskHistoryListResponse processingApiRequest(ActivitiAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        TaskHistoryListResponse response = new TaskHistoryListResponse();
        response.setTaskHistoryList(activitiDataService.getHistoryForInstance(request.getId()));
        return response;
    }
}
