package org.openiam.idm.srvc.mngsys.service;

import java.util.Map;

public interface AttributeNamesLookupService {

    public Object lookupPolicyMapAttributes(Map<String, Object> binding);
    public Object lookupManagedSystemAttributes(Map<String, Object> binding);

}
