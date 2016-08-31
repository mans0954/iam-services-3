package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiRequestDecision;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.TaskWrapperResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
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
public class GetTaskDispatcher extends AbstractAPIDispatcher<IdServiceRequest, TaskWrapperResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public GetTaskDispatcher() {
        super(TaskWrapperResponse.class);
    }

    @Override
    protected TaskWrapperResponse processingApiRequest(ActivitiAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        TaskWrapperResponse response = new TaskWrapperResponse();
        response.setTask(activitiDataService.getTask(request.getId()));
        return response;
    }
}
