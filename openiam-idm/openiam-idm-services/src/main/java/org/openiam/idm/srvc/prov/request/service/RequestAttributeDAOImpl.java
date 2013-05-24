package org.openiam.idm.srvc.prov.request.service;

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.prov.request.RequestAttributeDAO;
import org.openiam.idm.srvc.prov.request.domain.RequestAttributeEntity;
import org.openiam.idm.srvc.prov.request.dto.RequestAttribute;

import static org.hibernate.criterion.Example.create;

public class RequestAttributeDAOImpl extends BaseDaoImpl<RequestAttributeEntity, String> implements RequestAttributeDAO {

	private static final Log log = LogFactory
			.getLog(RequestAttributeDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
