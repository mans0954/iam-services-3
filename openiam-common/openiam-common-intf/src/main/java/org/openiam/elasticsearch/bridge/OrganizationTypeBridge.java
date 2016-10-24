package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;

public class OrganizationTypeBridge implements ElasticsearchBrigde {

	 @Override
	    public String objectToString(Object object) {
	        String retVal = null;
	        if(object instanceof OrganizationTypeEntity) {
	            retVal = ((OrganizationTypeEntity)object).getId();
	        }
	        return retVal;
	    }

	    @Override
	    public Object stringToObject(String stringValue) {
	        final OrganizationTypeEntity entity = new OrganizationTypeEntity();
	        entity.setId(stringValue);
	        return entity;
	    }
}
