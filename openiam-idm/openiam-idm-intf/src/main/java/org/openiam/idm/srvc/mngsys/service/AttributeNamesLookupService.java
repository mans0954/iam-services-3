package org.openiam.idm.srvc.mngsys.service;

import java.util.Map;

public interface AttributeNamesLookupService {

    Object lookupPolicyMapAttributes(Map<String, Object> binding);
    Object lookupManagedSystemAttributes(Map<String, Object> binding);

}
