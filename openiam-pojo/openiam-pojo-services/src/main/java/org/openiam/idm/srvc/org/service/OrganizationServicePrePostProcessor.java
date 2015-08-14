package org.openiam.idm.srvc.org.service;

import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.org.dto.Organization;

import java.util.Map;

public interface OrganizationServicePrePostProcessor {

    int FAIL =  0;
    int SUCCESS = 1;

    int save(Organization org, Map<String, Object> bindingMap, IdmAuditLog auditLog);

    int delete(String orgId, Map<String, Object> bindingMap, IdmAuditLog auditLog);

}