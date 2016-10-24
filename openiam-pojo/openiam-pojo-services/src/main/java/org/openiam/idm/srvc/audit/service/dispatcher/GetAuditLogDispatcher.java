package org.openiam.idm.srvc.audit.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.IdmAuditLogResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuditLogAPI;
import org.springframework.stereotype.Component;

@Component
public class GetAuditLogDispatcher extends AbstractAuditLogDispatcher<IdServiceRequest, IdmAuditLogResponse> {

    public GetAuditLogDispatcher() {
        super(IdmAuditLogResponse.class);
    }

    @Override
    protected IdmAuditLogResponse processingApiRequest(final AuditLogAPI openIAMAPI, final IdServiceRequest request) throws BasicDataServiceException {
        IdmAuditLogResponse response =  new IdmAuditLogResponse();
        response.setValue(auditLogService.findById(request.getId()));
        return response;
    }
}
