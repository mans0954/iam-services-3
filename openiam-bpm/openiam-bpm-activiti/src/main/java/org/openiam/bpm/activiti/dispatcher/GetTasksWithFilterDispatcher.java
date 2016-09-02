package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiFilterRequest;
import org.openiam.base.response.TaskListWrapper;
import org.openiam.base.response.TaskListWrapperResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 30/08/16.
 */
@Component
public class GetTasksWithFilterDispatcher extends AbstractAPIDispatcher<ActivitiFilterRequest, TaskListWrapperResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public GetTasksWithFilterDispatcher() {
        super(TaskListWrapperResponse.class);
    }

    @Override
    protected TaskListWrapperResponse processingApiRequest(ActivitiAPI openIAMAPI, ActivitiFilterRequest request) throws BasicDataServiceException {
        TaskListWrapperResponse response = new TaskListWrapperResponse();
        TaskListWrapper taskListWrapper = null;
        switch (openIAMAPI){
            case TasksForCandidateUserWithFilter:
                taskListWrapper =activitiDataService.getTasksForCandidateUserWithFilter(request.getUserId(),request.getFrom(),request.getSize(),request.getDescription(),request.getFromDate(), request.getToDate());
                break;
            case TasksForAssignedUserWithFilter:
                taskListWrapper =activitiDataService.getTasksForAssignedUserWithFilter(request.getUserId(),request.getFrom(),request.getSize(),request.getDescription(),request.getRequesterId(),request.getFromDate(), request.getToDate());
                break;
            default:
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, String.format("Unknown API call: %s", openIAMAPI.name()));
        }
        response.setTaskListWrapper(taskListWrapper);
        return response;
    }
}
