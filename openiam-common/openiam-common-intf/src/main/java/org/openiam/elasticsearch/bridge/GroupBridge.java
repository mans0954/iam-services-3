package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.grp.domain.GroupEntity;

public class GroupBridge implements ElasticsearchBrigde {

	@Override
	public String objectToString(Object object) {
		String retVal = null;
        if(object instanceof GroupEntity) {
            retVal = ((GroupEntity)object).getId();
        }
        return retVal;
	}

	@Override
	public Object stringToObject(String stringValue) {
		 final GroupEntity entity = new GroupEntity();
		 entity.setId(stringValue);
		 return entity;
	}
}
