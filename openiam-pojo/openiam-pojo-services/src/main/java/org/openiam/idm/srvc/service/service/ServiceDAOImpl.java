package org.openiam.idm.srvc.service.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.service.domain.ServiceEntity;
import org.springframework.stereotype.Repository;

@Repository("serviceDAO")
public class ServiceDAOImpl extends BaseDaoImpl<ServiceEntity, String> implements ServiceDAO {

	private static final Log log = LogFactory.getLog(ServiceDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
