package org.openiam.idm.srvc.audit.service;

import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    @Qualifier("auditLogBuilderCache")
    private net.sf.ehcache.Ehcache auditLogBuilderCache;

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
        final long threadId = Thread.currentThread().getId();
        Element chachedElement = auditLogBuilderCache.get(threadId);

        AuditLogBuilder value = (chachedElement != null ) ? (AuditLogBuilder)chachedElement.getObjectValue():null;
        if(value == null) {
            value = new AuditLogBuilder();
            auditLogBuilderCache.put(new Element(threadId, value));
        }
        return value;
    }

    public void updateAuditLogBuilder(AuditLogBuilder value) {
        final long threadId = Thread.currentThread().getId();
        if(value != null) {
            auditLogBuilderCache.put(new Element(threadId, value));
        }
    }
}
