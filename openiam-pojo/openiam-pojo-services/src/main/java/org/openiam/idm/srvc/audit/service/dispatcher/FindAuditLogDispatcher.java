package org.openiam.idm.srvc.audit.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IdmAuditLogListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.mq.constants.AuditLogAPI;
import org.springframework.stereotype.Component;

@Component
public class FindAuditLogDispatcher extends AbstractAuditLogDispatcher<BaseSearchServiceRequest<AuditLogSearchBean>, IdmAuditLogListResponse> {

    public FindAuditLogDispatcher() {
        super(IdmAuditLogListResponse.class);
    }

    @Override
    protected IdmAuditLogListResponse processingApiRequest(final AuditLogAPI openIAMAPI, final BaseSearchServiceRequest<AuditLogSearchBean> request) throws BasicDataServiceException {
        IdmAuditLogListResponse response =  new IdmAuditLogListResponse();
        response.setList(auditLogService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
