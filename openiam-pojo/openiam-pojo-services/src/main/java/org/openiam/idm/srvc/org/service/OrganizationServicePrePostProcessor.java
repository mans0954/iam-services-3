package org.openiam.idm.srvc.org.service;

import org.openiam.idm.srvc.org.dto.Organization;

import java.util.Map;

public interface OrganizationServicePrePostProcessor {

    public static final int FAIL =  0;
    public static final int SUCCESS = 1;

    int save(Organization org, Map<String, Object> bindingMap);

    int delete(String orgId, Map<String, Object> bindingMap);

}