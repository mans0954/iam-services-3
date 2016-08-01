package org.openiam.idm.srvc.audit.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("auditLogDispatcher")
public class AuditLogDispatcher extends AbstractAPIDispatcher<IdmAuditLogEntity, Response> {

	private static final Log LOG = LogFactory.getLog(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;

    public AuditLogDispatcher() {
        super(Response.class);
    }

    private void process(final IdmAuditLogEntity event) {
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

                auditLogService.save(srcLog);
            }
        } else {
            auditLogService.save(event);
        }
    }

    @Override
    protected void processingApiRequest(final IdmAuditLogEntity idmAuditLogEntity, String languageId, Response response) throws BasicDataServiceException {
        process(idmAuditLogEntity);
    }
}
