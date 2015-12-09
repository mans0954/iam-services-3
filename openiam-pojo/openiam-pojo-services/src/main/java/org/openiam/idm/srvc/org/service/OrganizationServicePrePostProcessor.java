package org.openiam.idm.srvc.org.service;

import java.util.Map;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.org.dto.Organization;

public interface OrganizationServicePrePostProcessor {

    int FAIL =  0;
    int SUCCESS = 1;

    int save(Organization org, Map<String, Object> bindingMap, IdmAuditLogEntity auditLog);

    int delete(String orgId, Map<String, Object> bindingMap, IdmAuditLogEntity auditLog);

}