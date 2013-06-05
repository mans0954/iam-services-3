package org.openiam.idm.srvc.res.service;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for ResourceType
 */
@Repository("resourceTypeDAO")
public class ResourceTypeDAOImpl extends BaseDaoImpl<ResourceTypeEntity, String>  implements ResourceTypeDAO  {

	@Override
	protected String getPKfieldName() {
		return "resourceTypeId";
	}

}
