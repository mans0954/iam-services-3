package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

public class ManagedSysBridge implements ElasticsearchBrigde {
	
	 @Override
	 public String objectToString(Object object) {
		 String retVal = null;
		 if(object instanceof ManagedSysEntity) {
			 retVal = ((ManagedSysEntity)object).getId();
		 }
		 return retVal;
	 }

    @Override
    public Object stringToObject(String stringValue) {
        final ManagedSysEntity entity = new ManagedSysEntity();
        entity.setId(stringValue);
        return entity;
    }
}
