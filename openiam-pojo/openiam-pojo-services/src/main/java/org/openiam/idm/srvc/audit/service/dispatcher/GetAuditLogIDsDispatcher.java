package org.openiam.idm.srvc.audit.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IdmAuditLogListResponse;
import org.openiam.base.response.StringListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.mq.constants.AuditLogAPI;
import org.springframework.stereotype.Component;

@Component
public class GetAuditLogIDsDispatcher extends AbstractAuditLogDispatcher<BaseSearchServiceRequest<AuditLogSearchBean>, StringListResponse> {

    public GetAuditLogIDsDispatcher() {
        super(StringListResponse.class);
    }

    @Override
    protected StringListResponse processingApiRequest(final AuditLogAPI openIAMAPI, final BaseSearchServiceRequest<AuditLogSearchBean> request) throws BasicDataServiceException {
        StringListResponse response =  new StringListResponse();
        response.setList(auditLogService.findIDs(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
