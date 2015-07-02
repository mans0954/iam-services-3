package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;

public class OrganizationBridge implements ElasticsearchBrigde {

	@Override
	public String objectToString(Object object) {
		String retVal = null;
        if(object instanceof OrganizationEntity) {
            retVal = ((OrganizationEntity)object).getId();
        }
        return retVal;
	}

	@Override
	public Object stringToObject(String stringValue) {
		final OrganizationEntity entity = new OrganizationEntity();
		entity.setId(stringValue);
		return entity;
	}

}
