package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.HistorySearchRequest;
import org.openiam.base.request.TaskSearchRequest;
import org.openiam.base.response.TaskListResponse;
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
public class FindTasksDispatcher extends AbstractAPIDispatcher<TaskSearchRequest, TaskListResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public FindTasksDispatcher() {
        super(TaskListResponse.class);
    }

    @Override
    protected TaskListResponse processingApiRequest(ActivitiAPI openIAMAPI, TaskSearchRequest request) throws BasicDataServiceException {
        TaskListResponse response = new TaskListResponse();
        response.setTaskList(activitiDataService.findTasks(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
