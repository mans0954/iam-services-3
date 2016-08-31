package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.GenericWorkflowRequest;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 30/08/16.
 */
@Component
public class InitiateWorkflowDispatcher extends AbstractAPIDispatcher<GenericWorkflowRequest, BasicWorkflowResponse, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public InitiateWorkflowDispatcher() {
        super(BasicWorkflowResponse.class);
    }

    @Override
    protected BasicWorkflowResponse processingApiRequest(ActivitiAPI openIAMAPI, GenericWorkflowRequest genericWorkflowRequest) throws BasicDataServiceException {
        return activitiDataService.initiateWorkflow(genericWorkflowRequest);
    }
}
