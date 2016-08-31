package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiFilterRequest;
import org.openiam.base.ws.Response;
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
public class DeleteUnclaimTaskDispatcher extends AbstractAPIDispatcher<ActivitiFilterRequest, Response, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public DeleteUnclaimTaskDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(ActivitiAPI openIAMAPI, ActivitiFilterRequest request) throws BasicDataServiceException {
        Response response = new Response();
        switch (openIAMAPI){
            case DeleteTask:
                activitiDataService.deleteTask(request.getTaskId(), request.getUserId());
                break;
            case UnclaimTask:
                activitiDataService.unclaimTask(request.getTaskId(), request.getUserId());
                break;
            case DeleteTasksForUser:
                activitiDataService.deleteTasksForUser(request.getUserId());
                break;
            default:
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, String.format("Unknown API call: %s", openIAMAPI.name()));
        }
        return response;
    }
}
