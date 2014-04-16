package org.openiam.idm.srvc.res.service;


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.springframework.stereotype.Repository;

@Repository("resourcePropDAO")
public class ResourcePropDAOImpl extends BaseDaoImpl<ResourcePropEntity, String> implements ResourcePropDAO  {

	private static final Log log = LogFactory.getLog(ResourcePropDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	
}
