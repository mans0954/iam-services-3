package org.openiam.core.dao.lucene.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

public class OrganizationBridge implements StringBridge {

	@Override
	public String objectToString(Object object) {
		String retval = "";
    	if (object instanceof OrganizationEntity) {
    		retval = ((OrganizationEntity)object).getOrgId();
    	}
        return retval;
	}

}
