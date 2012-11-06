package org.openiam.idm.srvc.res.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.dto.*;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Resources.
 * 
 */
@Repository("resourceDAO")
public class ResourceDAOImpl extends BaseDaoImpl<Resource, String> implements ResourceDAO  {

	private static final Log log = LogFactory.getLog(ResourceDAOImpl.class);
	
	private static final String GET_BY_RESOURCE_TYPE = String.format("select resource from %s as resource where resource.resourceType.resourceTypeId = :resourceTypeId " +
																	 "order by resource.displayOrder asc", Resource.class.getCanonicalName());
	private static final String GET_BY_RESOURCE_ROLE = String.format("SELECT resource FROM %s as resource " +
																	 "	inner join resource.resourceRoles as rr " +
																	 " where rr.id.roleId = :roleId " +
																	 " order by resource.managedSysId, resource.name ", Resource.class.getCanonicalName());
	
	@Override
	public List<Resource> getRootResources(Resource resource, int startAt, int size) {
		final Criteria criteria = getExampleCriteria(resource);
		//criteria.add(Restrictions.isNull("parentResources"));
		
		if(startAt > -1) {
			criteria.setFirstResult(startAt);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		criteria.add(Restrictions.isEmpty("parentResources"));
		
		return (List<Resource>)criteria.list();
	}
	
	@Override
	protected Criteria getExampleCriteria(final Resource resource) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(resource.getResourceId())) {
			criteria.add(Restrictions.eq(getPKfieldName(), resource.getResourceId()));
		} else {
			if(StringUtils.isNotEmpty(resource.getName())) {
				String resourceName = resource.getName();
				MatchMode matchMode = null;
				if(StringUtils.indexOf(resourceName, "*") == 0) {
					matchMode = MatchMode.START;
					resourceName = resourceName.substring(1);
				}
				if(StringUtils.isNotEmpty(resourceName) && StringUtils.indexOf(resourceName, "*") == resourceName.length() - 1) {
					resourceName = resourceName.substring(0, resourceName.length() - 1);
					matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
				}
				
				if(StringUtils.isNotEmpty(resourceName)) {
					if(matchMode != null) {
						criteria.add(Restrictions.ilike("name", resourceName, matchMode));
					} else {
						criteria.add(Restrictions.eq("name", resourceName));
					}
				}
			}
			
			if(resource.getResourceType() != null) {
				final ResourceType type = resource.getResourceType();
				if(StringUtils.isNotBlank(type.getResourceTypeId())) {
					criteria.add(Restrictions.eq("resourceType.resourceTypeId", type.getResourceTypeId()));
				}
			}
		}
		return criteria;
	}

    @Override
    public Resource findByName(String name) {
        Criteria criteria = getCriteria().add(Restrictions.eq("name", name));
        return (Resource) criteria.uniqueResult();
    }

	public List<Resource> getResourcesByType(String resourceTypeId) {
		final Query qry = getSession().createQuery(GET_BY_RESOURCE_TYPE);
		qry.setString("resourceTypeId", resourceTypeId);
		qry.setCacheable(true);
		qry.setCacheRegion("query.resource.getResourceByType");
		return (List<Resource>) qry.list();
	}

	public List<Resource> findResourcesForRole(String roleId) {
		final Query qry = getSession().createQuery(GET_BY_RESOURCE_ROLE);
		qry.setString("roleId", roleId);
		qry.setCacheable(true);
		qry.setCacheRegion("query.resource.findResourcesForRole");
		final List<Resource> result = (List<Resource>) qry.list();
		return (CollectionUtils.isNotEmpty(result)) ? result : null;
	}

    	public List<Resource> findResourcesForUserRole(String userId) {

             String select = " select DISTINCT r.RESOURCE_ID, r.RESOURCE_TYPE_ID, " +
                " r.DESCRIPTION, r.NAME, r.RESOURCE_PARENT, " +
                " r.BRANCH_ID, r.CATEGORY_ID, r.DISPLAY_ORDER, " +
                " r.NODE_LEVEL, r.SENSITIVE_APP, r.MANAGED_SYS_ID," +
                " r.URL, r.RES_OWNER_GROUP_ID, r.RES_OWNER_USER_ID " +
                " FROM  USER_ROLE ur, RESOURCE_ROLE rr, RES r " +
                " WHERE ur.USER_ID = :userId and ur.SERVICE_ID = rr.SERVICE_ID AND ur.ROLE_ID = rr.ROLE_ID AND " +
                "       rr.RESOURCE_ID = r.RESOURCE_ID";

		Session session = sessionFactory.getCurrentSession();
		try{


            SQLQuery qry = session.createSQLQuery(select);
            qry.addEntity(Resource.class);
            qry.setString("userId", userId);

			List<Resource> result = (List<Resource>) qry.list();
			if (result == null || result.isEmpty()) {
				log.debug("get successful, no instance found");
				return null;
			}
			log.debug("get successful, resource instances found");


           for (Resource r:result) {
                Hibernate.initialize(r.getResourceType());
                Hibernate.initialize(r.getResourceProps());
                Hibernate.initialize(r.getResourceRoles());
                Hibernate.initialize(r.getEntitlements());
                Hibernate.initialize(r.getResourceGroups());
            }

			return result;
		} catch (HibernateException re) {
			log.error("persist failed", re);
			throw re;
		}
	}


	
	public List<Resource> findResourcesForRoles(List<String> roleIdList) {
		Session session = sessionFactory.getCurrentSession();
		try{
			Query qry = session.createQuery( 
					"SELECT distinct resource  " +
					"FROM org.openiam.idm.srvc.res.dto.Resource as resource " +
					"	inner join resource.resourceRoles as rr " +
					" where rr.id.roleId in ( :roleIdList ) " +
					" order by resource.managedSysId, resource.name  "
					);
			qry.setParameterList("roleIdList", roleIdList);
			
			qry.setCacheable(true);
			qry.setCacheRegion("query.resource.findResourcesForRole");
			List<Resource> result = (List<Resource>) qry.list();
			if (result == null || result.isEmpty()) {
				log.debug("get successful, no instance found");
				return null;
			}
			log.debug("get successful, resource instances found");
			return result;
		} catch (HibernateException re) {
			log.error("persist failed", re);
			throw re;
		}


	}

	@Override
	protected String getPKfieldName() {
		return "resourceId";
	}

}


