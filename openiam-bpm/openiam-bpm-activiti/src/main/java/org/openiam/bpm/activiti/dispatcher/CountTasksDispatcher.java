package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.HistorySearchRequest;
import org.openiam.base.request.TaskSearchRequest;
import org.openiam.base.response.IntResponse;
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
public class CountTasksDispatcher extends AbstractAPIDispatcher<TaskSearchRequest, IntResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public CountTasksDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(ActivitiAPI openIAMAPI, TaskSearchRequest request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(activitiDataService.countTasks(request.getSearchBean()));
        return response;
    }
}
