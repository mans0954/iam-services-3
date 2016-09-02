package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiFilterRequest;
import org.openiam.base.response.TaskListWrapperResponse;
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
public class GetTasksForUserDispatcher extends AbstractAPIDispatcher<ActivitiFilterRequest, TaskListWrapperResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public GetTasksForUserDispatcher() {
        super(TaskListWrapperResponse.class);
    }

    @Override
    protected TaskListWrapperResponse processingApiRequest(ActivitiAPI openIAMAPI, ActivitiFilterRequest request) throws BasicDataServiceException {
        TaskListWrapperResponse response = new TaskListWrapperResponse();
        response.setTaskListWrapper(activitiDataService.getTasksForUser(request.getUserId(), request.getFrom(), request.getSize()));
        return response;
    }
}
