package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.StringResponse;
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
public class ProcessInstanceIdByExecutionIdDispatcher extends AbstractAPIDispatcher<IdServiceRequest, StringResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public ProcessInstanceIdByExecutionIdDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(ActivitiAPI openIAMAPI, IdServiceRequest idServiceRequest) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(activitiDataService.getProcessInstanceIdByExecutionId(idServiceRequest.getId()));
        return response;
    }
}
