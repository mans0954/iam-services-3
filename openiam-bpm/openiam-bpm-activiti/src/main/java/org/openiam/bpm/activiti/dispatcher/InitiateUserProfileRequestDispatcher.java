package org.openiam.bpm.activiti.dispatcher;

import org.openiam.base.request.UserProfileServiceRequest;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/08/16.
 */
@Component
public class InitiateUserProfileRequestDispatcher extends AbstractAPIDispatcher<UserProfileServiceRequest, SaveTemplateProfileResponse, ActivitiAPI> {

    @Autowired
    private ActivitiDataService activitiDataService;

    public InitiateUserProfileRequestDispatcher() {
        super(SaveTemplateProfileResponse.class);
    }

    @Override
    protected SaveTemplateProfileResponse processingApiRequest(ActivitiAPI openIAMAPI, UserProfileServiceRequest request) throws BasicDataServiceException {
        switch (openIAMAPI) {
            case InitiateNewHireRequest:
                return activitiDataService.initiateNewHireRequest(((NewUserProfileRequestModel) request.getModel()));
            case InitiateEditUserWorkflow:
                return activitiDataService.initiateEditUserWorkflow(request.getModel());
            default:
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, String.format("Cannot find the handler for %s API", openIAMAPI.name()));
        }
    }

    protected void handleTemplateException(PageTemplateException e, final SaveTemplateProfileResponse responseBody){
        IdmAuditLogEntity auditEvent = AuditLogHolder.getInstance().getEvent();

        auditEvent.fail();
        auditEvent.setFailureReason(e.getCode());
        auditEvent.setException(e);
        responseBody.setCurrentValue(e.getCurrentValue());
        responseBody.setElementName(e.getElementName());
        responseBody.setErrorCode(e.getCode());
        responseBody.setStatus(ResponseStatus.FAILURE);
    }
}
