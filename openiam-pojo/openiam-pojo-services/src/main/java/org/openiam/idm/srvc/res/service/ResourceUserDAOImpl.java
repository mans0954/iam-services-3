package org.openiam.idm.srvc.res.service;


import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.springframework.stereotype.Repository;

@Repository("resourceUserDAO")
public class ResourceUserDAOImpl extends BaseDaoImpl<ResourceUserEntity, String>  implements ResourceUserDAO {

	private static String DELETE_BY_USER_ID_AND_RESOURCE_ID_BATCH = "DELETE FROM %s ru WHERE ru.resourceId IN(:resourceIds) AND ru.userId = :userId";
	private static String DELETE_BY_RESOURCE_ID = "DELETE FROM %s ru WHERE ru.resourceId = :resourceId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID_AND_RESOURCE_ID_BATCH = String.format(DELETE_BY_USER_ID_AND_RESOURCE_ID_BATCH, domainClass.getSimpleName());
	}
	
	@Override
	protected String getPKfieldName() {
		return "resourceUserId";
	}

	@Override
	public ResourceUserEntity getRecord(String resourceId, String userId) {
		final Criteria criteria = getCriteria()
									.add(Restrictions.eq("resourceId", resourceId))
									.add(Restrictions.eq("userId", userId));
		final List<ResourceUserEntity> resultList = criteria.list();
		return (CollectionUtils.isNotEmpty(resultList) && resultList.size() == 1) ? resultList.get(0) : null;
	}

	@Override
	public void deleteByUserId(String userId, Collection<String> resourceIds) {
		if(CollectionUtils.isNotEmpty(resourceIds)) {
			final Query query = getSession().createQuery(DELETE_BY_USER_ID_AND_RESOURCE_ID_BATCH);
			query.setParameterList("resourceIds", resourceIds);
			query.setParameter("userId", userId);
			query.executeUpdate();
		}
	}

	@Override
	public void deleteByResourceId(String resourceId) {
		final Query query = getSession().createQuery(DELETE_BY_RESOURCE_ID);
		query.setParameter("resourceId", resourceId);
		query.executeUpdate();
	}
	
}
