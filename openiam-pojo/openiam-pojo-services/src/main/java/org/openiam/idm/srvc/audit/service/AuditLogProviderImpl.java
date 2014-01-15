package org.openiam.idm.srvc.audit.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private final Map<String, AuditLogBuilder> idmAuditLogMap = new ConcurrentHashMap<String, AuditLogBuilder>();

    @Override
    public AuditLogBuilder getAuditLogBuilder(String id) {
        return idmAuditLogMap.get(id);
    }

    @Override
    public void remove(String auditLogId) {
        idmAuditLogMap.remove(auditLogId);
    }

    @Override
    public AuditLogBuilder persist(AuditLogBuilder auditLogBuilder) {
        IdmAuditLogEntity auditLogEntity = auditLogBuilder.getEntity();
        String auditLogEntityId = auditLogService.save(auditLogEntity);
        auditLogEntity.setId(auditLogEntityId);
        auditLogBuilder.setEntity(auditLogEntity);
        idmAuditLogMap.put(auditLogEntityId, auditLogBuilder);
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
