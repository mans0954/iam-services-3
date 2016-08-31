package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiFilterRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.TaskListWrapperResponse;
import org.openiam.base.response.TaskWrapperResponse;
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
public class NumTaskWithFilterDispatcher extends AbstractAPIDispatcher<ActivitiFilterRequest, IntResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public NumTaskWithFilterDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(ActivitiAPI openIAMAPI, ActivitiFilterRequest request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        int count = 0;
        switch (openIAMAPI){
            case NumOfAssignedTasksWithFilter:
                count =activitiDataService.getNumOfAssignedTasksWithFilter(request.getUserId(),request.getDescription(),request.getRequesterId(),request.getFromDate(), request.getToDate());
                break;
            case NumOfCandidateTasksWithFilter:
                count =activitiDataService.getNumOfCandidateTasksWithFilter(request.getUserId(),request.getDescription(),request.getFromDate(), request.getToDate());
                break;
            default:
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, String.format("Unknown API call: %s", openIAMAPI.name()));
        }
        response.setValue(count);
        return response;
    }
}
