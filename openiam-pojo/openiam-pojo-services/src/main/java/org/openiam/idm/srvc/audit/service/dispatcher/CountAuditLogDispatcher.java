package org.openiam.idm.srvc.audit.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.mq.constants.AuditLogAPI;
import org.springframework.stereotype.Component;

@Component
public class CountAuditLogDispatcher extends AbstractAuditLogDispatcher<BaseSearchServiceRequest<AuditLogSearchBean>, IntResponse> {

    public CountAuditLogDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(final AuditLogAPI openIAMAPI, final BaseSearchServiceRequest<AuditLogSearchBean> request) throws BasicDataServiceException {
        IntResponse response =  new IntResponse();
        response.setValue(auditLogService.count(request.getSearchBean()));
        return response;
    }
}
