package org.openiam.idm.srvc.audit.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.mq.constants.AuditLogAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alexander on 20/10/16.
 */
public abstract class AbstractAuditLogDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response>
        extends AbstractAPIDispatcher<RequestBody, ResponseBody, AuditLogAPI> {
    @Autowired
    protected AuditLogService auditLogService;

    public AbstractAuditLogDispatcher(Class<ResponseBody> auditLogResponseClass) {
        super(auditLogResponseClass);
    }
}
