package org.openiam.idm.srvc.res.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Resources.
 */
@Repository("resourceDAO")
public class ResourceDAOImpl extends BaseDaoImpl<ResourceEntity, String> implements ResourceDAO {

    private static final Log log = LogFactory.getLog(ResourceDAOImpl.class);

    @Override
    protected Criteria getExampleCriteria(final ResourceEntity resource) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(resource.getResourceId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), resource.getResourceId()));
        } else {
            if (StringUtils.isNotEmpty(resource.getName())) {
                String resourceName = resource.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(resourceName, "*") == 0) {
                    matchMode = MatchMode.START;
                    resourceName = resourceName.substring(1);
                }
                if (StringUtils.isNotEmpty(resourceName) && StringUtils.indexOf(resourceName, "*") == resourceName.length() - 1) {
                    resourceName = resourceName.substring(0, resourceName.length() - 1);
                    matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
                }

                if (StringUtils.isNotEmpty(resourceName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", resourceName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", resourceName));
                    }
                }
            }

            if (resource.getResourceType() != null) {
                final ResourceTypeEntity type = resource.getResourceType();
                if (StringUtils.isNotBlank(type.getResourceTypeId())) {
                    criteria.add(Restrictions.eq("resourceType.resourceTypeId", type.getResourceTypeId()));
                }
            }
        }
        return criteria;
    }

	@Override
	public List<ResourceEntity> getRootResources(ResourceEntity resource, int startAt, int size) {
		final Criteria criteria = getExampleCriteria(resource);
		//criteria.add(Restrictions.isNull("parentResources"));

		if(startAt > -1) {
			criteria.setFirstResult(startAt);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		criteria.add(Restrictions.isEmpty("parentResources"));

		return (List<ResourceEntity>)criteria.list();
	}

    @Override
    public ResourceEntity findByName(String name) {
        Criteria criteria = getCriteria().add(Restrictions.eq("name", name));
        return (ResourceEntity) criteria.uniqueResult();
    }

    public List<ResourceEntity> getResourcesByType(String resourceTypeId) {
        Criteria criteria = getCriteria()
                .createAlias("resourceType", "rt")
                .add(Restrictions.eq("rt.resourceTypeId", resourceTypeId))
                .addOrder(Order.asc("displayOrder"));
        criteria.setCacheable(true);
        criteria.setCacheRegion("query.resource.getResourceByType");
        return (List<ResourceEntity>) criteria.list();
    }

    public List<ResourceEntity> findResourcesForRole(String roleId) {
        Criteria criteria = getCriteria()
                .createAlias("resourceRoles", "rr")
                .add(Restrictions.eq("rr.id.roleId", roleId))
                .addOrder(Order.asc("managedSysId"))
                .addOrder(Order.asc("name"));
        criteria.setCacheable(true);
        criteria.setCacheRegion("query.resource.findResourcesForRole");
        final List<ResourceEntity> result = (List<ResourceEntity>) criteria.list();
        return (CollectionUtils.isNotEmpty(result)) ? result : null;
    }

    public List<ResourceEntity> findResourcesForUserRole(String userId) {

        String select = " select DISTINCT r.RESOURCE_ID, r.RESOURCE_TYPE_ID, " +
                " r.DESCRIPTION, r.NAME, " +
                " r.BRANCH_ID, r.CATEGORY_ID, r.DISPLAY_ORDER, " +
                " r.NODE_LEVEL, r.SENSITIVE_APP, r.MANAGED_SYS_ID," +
                " r.URL, r.RES_OWNER_GROUP_ID, r.RES_OWNER_USER_ID " +
                " FROM  USER_ROLE ur, RESOURCE_ROLE rr, RES r " +
                " WHERE ur.USER_ID = :userId AND ur.ROLE_ID = rr.ROLE_ID AND " +
                "       rr.RESOURCE_ID = r.RESOURCE_ID";

        Session session = sessionFactory.getCurrentSession();
        try {


            SQLQuery qry = session.createSQLQuery(select);
            qry.addEntity(ResourceEntity.class);
            qry.setString("userId", userId);

            List<ResourceEntity> result = (List<ResourceEntity>) qry.list();
            if (result == null || result.isEmpty()) {
                log.debug("get successful, no instance found");
                return null;
            }
            log.debug("get successful, resource instances found");


            for (ResourceEntity r : result) {
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


    public List<ResourceEntity> findResourcesForRoles(List<String> roleIdList) {
        try {
            Criteria criteria = getCriteria()
                    .createAlias("resourceRoles", "rr")
                    .add(Restrictions.in("rr.id.roleId", roleIdList))
                    .addOrder(Order.asc("managedSysId"))
                    .addOrder(Order.asc("name"))
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.setCacheable(true);
            criteria.setCacheRegion("query.resource.findResourcesForRole");
            List<ResourceEntity> result = (List<ResourceEntity>) criteria.list();
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


