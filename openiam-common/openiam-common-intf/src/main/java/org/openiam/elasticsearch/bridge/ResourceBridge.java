package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.res.domain.ResourceEntity;

public class ResourceBridge implements ElasticsearchBrigde {

	@Override
	public String objectToString(Object object) {
		String retVal = null;
        if(object instanceof ResourceEntity) {
            retVal = ((ResourceEntity)object).getId();
        }
        return retVal;
	}

	@Override
	public Object stringToObject(String stringValue) {
		 final ResourceEntity entity = new ResourceEntity();
		 entity.setId(stringValue);
		 return entity;
	}

}
