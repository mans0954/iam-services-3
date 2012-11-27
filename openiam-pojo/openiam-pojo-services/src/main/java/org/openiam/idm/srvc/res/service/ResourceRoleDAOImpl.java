package org.openiam.idm.srvc.res.service;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.springframework.stereotype.Repository;

@Repository("resourceRoleDAO")
public class ResourceRoleDAOImpl extends BaseDaoImpl<ResourceRoleEntity, ResourceRoleEmbeddableId>  implements ResourceRoleDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
	
	private static String DELETE_BY_ROLE_ID = "DELETE FROM %s ur WHERE ur.id.roleId = :roleId";
	private static String DELETE_BY_RESOURCE_ID = "DELETE FROM %s ur WHERE ur.id.resourceId = :resourceId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ROLE_ID = String.format(DELETE_BY_ROLE_ID, domainClass.getSimpleName());
		DELETE_BY_RESOURCE_ID = String.format(DELETE_BY_RESOURCE_ID, domainClass.getSimpleName());
	}

	@Override
	public void deleteByRoleId(String roleId) {
		final Session session = sessionFactory.getCurrentSession();
		final Query qry = session.createQuery(DELETE_BY_ROLE_ID);
		qry.setString("roleId", roleId);
		qry.executeUpdate();
	}

	@Override
	public void deleteByResourceId(String resourceId) {
		final Query query = getSession().createQuery(DELETE_BY_RESOURCE_ID);
		query.setParameter("resourceId", resourceId);
		query.executeUpdate();
	}

}

