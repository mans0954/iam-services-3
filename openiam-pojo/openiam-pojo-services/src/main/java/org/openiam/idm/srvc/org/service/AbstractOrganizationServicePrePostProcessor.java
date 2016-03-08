package org.openiam.idm.srvc.org.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.springframework.context.ApplicationContext;


public abstract class AbstractOrganizationServicePrePostProcessor implements OrganizationServicePrePostProcessor {

    protected final Log log = LogFactory.getLog(AbstractOrganizationServicePrePostProcessor.class);

    protected ApplicationContext context;

    @Override
    public int save(Organization org, Map<String, Object> bindingMap, IdmAuditLogEntity auditLog) {
        return OrganizationServicePrePostProcessor.SUCCESS;
    }

    @Override
    public int delete(String orgId, Map<String, Object> bindingMap, IdmAuditLogEntity auditLog) {
        return OrganizationServicePrePostProcessor.SUCCESS;
    }
}
