package org.openiam.idm.srvc.property.dao;

import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.property.domain.PropertyValueEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PropertyValueDAOImpl extends BaseDaoImpl<PropertyValueEntity, String> implements PropertyValueDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
