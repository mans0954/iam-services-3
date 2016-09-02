package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.ActivitiClaimRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/08/16.
 */
@Component
public class ClaimRequestDispatcher extends AbstractAPIDispatcher<ActivitiClaimRequest, Response, ActivitiAPI> {

    @Autowired
    private ActivitiDataService activitiDataService;

    public ClaimRequestDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(ActivitiAPI openIAMAPI, ActivitiClaimRequest request) throws BasicDataServiceException {
        activitiDataService.claimRequest(request);
        return new Response();
    }
}
