package org.openiam.core.dao.lucene.bridge;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.hibernate.search.bridge.TwoWayStringBridge;
import org.openiam.idm.srvc.user.domain.UserEntity;

public class UserBridge implements TwoWayStringBridge {

	@Override
	public String objectToString(Object object) {
		String retVal = null;
		if(object instanceof UserEntity) {
			retVal = ((UserEntity)object).getUserId();
		}
		return retVal;
	}

	@Override
	public Object stringToObject(String stringValue) {
		final UserEntity entity = new UserEntity();
		entity.setUserId(stringValue);
		return entity;
	}
}
