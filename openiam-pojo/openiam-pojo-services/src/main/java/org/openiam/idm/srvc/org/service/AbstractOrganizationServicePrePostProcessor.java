package org.openiam.idm.srvc.org.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.org.dto.Organization;
import org.springframework.context.ApplicationContext;

import java.util.Map;


public abstract class AbstractOrganizationServicePrePostProcessor implements OrganizationServicePrePostProcessor {

    protected final Log log = LogFactory.getLog(AbstractOrganizationServicePrePostProcessor.class);

    protected ApplicationContext context;

    @Override
    public int save(Organization org, Map<String, Object> bindingMap) {
        return OrganizationServicePrePostProcessor.SUCCESS;
    }

    @Override
    public int delete(String orgId, Map<String, Object> bindingMap) {
        return OrganizationServicePrePostProcessor.SUCCESS;
    }
}
