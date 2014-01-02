package org.openiam.idm.srvc.audit.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AuditLogProviderImpl implements AuditLogProvider  {

    private final static ThreadLocal<AuditLogBuilder> auditLogBuilderThreadLocal = new ThreadLocal<AuditLogBuilder>();

    @Autowired
    private AuditLogService auditLogService;


    @Override
    public AuditLogBuilder persist(AuditLogBuilder auditLogBuilder) {
        IdmAuditLogEntity auditLogEntity = auditLogBuilder.getEntity();
        if(StringUtils.isEmpty(auditLogEntity.getId())) {
          String id = auditLogService.save(auditLogEntity);
          auditLogEntity = auditLogService.findById(id);
          auditLogBuilder.setEntity(auditLogEntity);
        }
        return auditLogBuilder;
    }

    public AuditLogBuilder getAuditLogBuilder () {

        AuditLogBuilder value = auditLogBuilderThreadLocal.get();
        if(value == null) {
            value = new AuditLogBuilder();
            auditLogBuilderThreadLocal.set(value);
        }
        return value;
    }

    public void updateAuditLogBuilder(AuditLogBuilder value) {
        if(value != null) {
            auditLogBuilderThreadLocal.remove();
            auditLogBuilderThreadLocal.set(value);
        }
    }
}
