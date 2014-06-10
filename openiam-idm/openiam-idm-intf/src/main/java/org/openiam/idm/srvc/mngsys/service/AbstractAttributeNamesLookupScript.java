package org.openiam.idm.srvc.mngsys.service;

import org.springframework.context.ApplicationContext;

import java.util.Map;

public class AbstractAttributeNamesLookupScript implements AttributeNamesLookupService {

    protected ApplicationContext context;
    protected Map<String, Object> binding;

    @Override
    public Object lookupPolicyMapAttributes(Map<String, Object> binding) {
        return null;
    }

    @Override
    public Object lookupManagedSystemAttributes(Map<String, Object> binding) {
        return null;
    }
}
