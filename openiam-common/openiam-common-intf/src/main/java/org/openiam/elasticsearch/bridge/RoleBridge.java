package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;

public class RoleBridge implements ElasticsearchBrigde {

	@Override
	public String objectToString(Object object) {
		String retVal = null;
        if(object instanceof RoleEntity) {
            retVal = ((RoleEntity)object).getId();
        }
        return retVal;
	}

	@Override
	public Object stringToObject(String stringValue) {
		final RoleEntity entity = new RoleEntity();
		entity.setId(stringValue);
		return entity;
	}

}
