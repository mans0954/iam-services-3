package org.openiam.idm.srvc.audit.service.dispatcher;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.response.AuditLogResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.mq.constants.AuditLogAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("auditLogDispatcher")
public class AuditLogDispatcher extends AbstractAuditLogDispatcher<IdmAuditLogRequest, AuditLogResponse> {

	private static final Log LOG = LogFactory.getLog(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;

    public AuditLogDispatcher() {
        super(AuditLogResponse.class);
    }

    private IdmAuditLogEntity process(final IdmAuditLogRequest request) {
        IdmAuditLogEntity event = request.getLogEntity();
        if (StringUtils.isNotEmpty(event.getId())) {
        	final IdmAuditLogEntity srcLog = auditLogService.findById(event.getId());
            if (srcLog != null) {

                for(IdmAuditLogCustomEntity customLog : event.getCustomRecords()) {
                    if(!srcLog.getCustomRecords().contains(customLog)){
                        srcLog.addCustomRecord(customLog.getKey(),customLog.getValue());
                    }
                }
                for(IdmAuditLogEntity newChildren : event.getChildLogs()) {
                   if(!srcLog.getChildLogs().contains(newChildren)) {
                       srcLog.addChild(newChildren);
                   }
                }
                for(AuditLogTargetEntity newTarget : event.getTargets()) {
                     if(!srcLog.getTargets().contains(newTarget)) {
                        srcLog.addTarget(newTarget.getId(), newTarget.getTargetType(), newTarget.getObjectPrincipal());
                    }
                }

                event =auditLogService.save(srcLog);
            }
        } else {
            event =auditLogService.save(event);
        }
        return event;
    }

    @Override
    protected AuditLogResponse processingApiRequest(final AuditLogAPI openIAMAPI, final IdmAuditLogRequest idmAuditLogRequest) throws BasicDataServiceException {
        AuditLogResponse response =  new AuditLogResponse();
        IdmAuditLogEntity event = process(idmAuditLogRequest);
        response.setEvent(event);
        return response;
    }
}
