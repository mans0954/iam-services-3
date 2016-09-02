package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiRequestDecision;
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
public class MakeDecisionDispatcher extends AbstractAPIDispatcher<ActivitiRequestDecision, Response, ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    public MakeDecisionDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(ActivitiAPI openIAMAPI, ActivitiRequestDecision activitiRequestDecision) throws BasicDataServiceException {
        activitiDataService.makeDecision(activitiRequestDecision);
        return new Response(ResponseStatus.SUCCESS);
    }
}
