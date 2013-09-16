package org.openiam.idm.srvc.res.service;

import static org.hibernate.criterion.Projections.rowCount;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Resources.
 */
@Repository("resourceDAO")
public class ResourceDAOImpl extends BaseDaoImpl<ResourceEntity, String>
		implements ResourceDAO {

	private static final Log log = LogFactory.getLog(ResourceDAOImpl.class);

	@Override
	protected Criteria getExampleCriteria(final ResourceEntity resource) {
		final Criteria criteria = getCriteria();
		if (StringUtils.isNotBlank(resource.getResourceId())) {
			criteria.add(Restrictions.eq(getPKfieldName(),
					resource.getResourceId()));
		} else {
			if (StringUtils.isNotEmpty(resource.getName())) {
				String resourceName = resource.getName();
				MatchMode matchMode = null;
				if (StringUtils.indexOf(resourceName, "*") == 0) {
					matchMode = MatchMode.END;
					resourceName = resourceName.substring(1);
				}
				if (StringUtils.isNotEmpty(resourceName)
						&& StringUtils.indexOf(resourceName, "*") == resourceName
								.length() - 1) {
					resourceName = resourceName.substring(0,
							resourceName.length() - 1);
					matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE
							: MatchMode.START;
				}

				if (StringUtils.isNotEmpty(resourceName)) {
					if (matchMode != null) {
						criteria.add(Restrictions.ilike("name", resourceName,
								matchMode));
					} else {
						criteria.add(Restrictions.eq("name", resourceName));
					}
				}
			}

			if (resource.getResourceType() != null) {
				final ResourceTypeEntity type = resource.getResourceType();
				if (StringUtils.isNotBlank(type.getResourceTypeId())) {
					criteria.add(Restrictions.eq("resourceType.resourceTypeId",
							type.getResourceTypeId()));
				}
			}

			if (CollectionUtils.isNotEmpty(resource.getParentResources())) {
				final Set<String> parentResourceIds = new HashSet<String>();
				for (final ResourceEntity parent : resource
						.getParentResources()) {
					if (parent != null
							&& StringUtils.isNotBlank(parent.getResourceId())) {
						parentResourceIds.add(parent.getResourceId());
					}
				}

				if (CollectionUtils.isNotEmpty(parentResourceIds)) {
					criteria.createAlias("parentResources", "parent").add(
							Restrictions.in("parent.resourceId",
									parentResourceIds));
				}
			}

			if (CollectionUtils.isNotEmpty(resource.getChildResources())) {
				final Set<String> childResoruceIds = new HashSet<String>();
				for (final ResourceEntity child : resource.getChildResources()) {
					if (child != null
							&& StringUtils.isNotBlank(child.getResourceId())) {
						childResoruceIds.add(child.getResourceId());
					}
				}

				if (CollectionUtils.isNotEmpty(childResoruceIds)) {
					criteria.createAlias("childResources", "child").add(
							Restrictions.in("child.resourceId",
									childResoruceIds));
				}
			}
		}
		return criteria;
	}

	@Override
	public List<ResourceEntity> getRootResources(ResourceEntity resource,
			int startAt, int size) {
		final Criteria criteria = getExampleCriteria(resource);
		// criteria.add(Restrictions.isNull("parentResources"));

		if (startAt > -1) {
			criteria.setFirstResult(startAt);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}
		criteria.add(Restrictions.isEmpty("parentResources"));

		return (List<ResourceEntity>) criteria.list();
	}

	@Override
	public ResourceEntity findByName(String name) {
		Criteria criteria = getCriteria().add(Restrictions.eq("name", name));
		return (ResourceEntity) criteria.uniqueResult();
	}

	public List<ResourceEntity> getResourcesByType(String resourceTypeId) {
		Criteria criteria = getCriteria().createAlias("resourceType", "rt")
				.add(Restrictions.eq("rt.resourceTypeId", resourceTypeId))
				.addOrder(Order.asc("displayOrder"));
		return (List<ResourceEntity>) criteria.list();
	}

	public List<ResourceEntity> getResourcesForRole(final String roleId,
			final int from, final int size) {
		final Criteria criteria = getCriteria()
				.createAlias("roles", "rr")
				.add(Restrictions.eq("rr.roleId", roleId))
				.addOrder(Order.asc("managedSysId"))
				.addOrder(Order.asc("name"));

		if (from > -1) {
			criteria.setFirstResult(from);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}

		final List<ResourceEntity> retVal = (List<ResourceEntity>) criteria
				.list();
		return retVal;
	}

	@Override
	public List<ResourceEntity> getResourcesForManagedSys(
			final String mngSysId, final int from, final int size) {
		final Criteria criteria = getCriteria().add(
				Restrictions.eq("managedSysId", mngSysId)).addOrder(
				Order.asc("name"));

		if (from > -1) {
			criteria.setFirstResult(from);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}

		final List<ResourceEntity> retVal = (List<ResourceEntity>) criteria
				.list();
		return retVal;
	}

	@Override
	public int getNumOfResourcesForRole(String roleId) {
		final Criteria criteria = getCriteria()
				.createAlias("roles", "rr")
				.add(Restrictions.eq("rr.roleId", roleId))
				.setProjection(rowCount());

		return ((Number) criteria.uniqueResult()).intValue();
	}

	@Override
	protected String getPKfieldName() {
		return "resourceId";
	}

	@Override
	public List<ResourceEntity> getResourcesForGroup(final String groupId,
			final int from, final int size) {
		final Criteria criteria = getCriteria()
				.createAlias("groups", "rg")
				.add(Restrictions.eq("rg.grpId", groupId))
				.addOrder(Order.asc("managedSysId"))
				.addOrder(Order.asc("name"));

		if (from > -1) {
			criteria.setFirstResult(from);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}

		final List<ResourceEntity> retVal = (List<ResourceEntity>) criteria
				.list();
		return retVal;
	}

	@Override
	public int getNumOfResourcesForGroup(final String groupId) {
		final Criteria criteria = getCriteria()
				.createAlias("groups", "rg")
				.add(Restrictions.eq("rg.grpId", groupId))
				.setProjection(rowCount());

		return ((Number) criteria.uniqueResult()).intValue();
	}

	private Criteria getResourceForUserCriteria(final String userId) {
		return getCriteria().createAlias("users", "ru").add(
				Restrictions.eq("ru.userId", userId));
	}

	@Override
	public List<ResourceEntity> getResourcesForUser(final String userId,
			final int from, final int size) {
		final Criteria criteria = getResourceForUserCriteria(userId);

		if (from > -1) {
			criteria.setFirstResult(from);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}

		return criteria.list();
	}

	@Override
	public int getNumOfResourcesForUser(String userId) {
		final Criteria criteria = getResourceForUserCriteria(userId)
				.setProjection(rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
}
