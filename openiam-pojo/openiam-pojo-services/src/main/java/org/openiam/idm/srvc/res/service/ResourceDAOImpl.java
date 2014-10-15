package org.openiam.idm.srvc.res.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.Tuple;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.openiam.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * DAO Implementation for Resources.
 */
@Repository("resourceDAO")
public class ResourceDAOImpl extends BaseDaoImpl<ResourceEntity, String>
		implements ResourceDAO {

	private static final Log log = LogFactory.getLog(ResourceDAOImpl.class);
	
	@Autowired
    private ResourceSearchBeanConverter resourceSearchBeanConverter;

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ResourceSearchBean) {
			final ResourceSearchBean resourceSearchBean = (ResourceSearchBean)searchBean;
			criteria = getExampleCriteria(resourceSearchBeanConverter.convert(resourceSearchBean));
			addSearchBeanCriteria(criteria, resourceSearchBean);
		}
		return criteria;
	}

	@Override
	protected Criteria getExampleCriteria(final ResourceEntity resource) {
		final Criteria criteria = getCriteria();
		if (StringUtils.isNotBlank(resource.getId())) {
			criteria.add(Restrictions.eq(getPKfieldName(),
					resource.getId()));
		} else {
            if(resource.getRisk() != null) {
                criteria.add(Restrictions.eq("risk", resource.getRisk()));
            }

			if (StringUtils.isNotEmpty(resource.getName())) {
				String resourceName = resource.getName();
				MatchMode matchMode = null;
				if (StringUtils.indexOf(resourceName, "*") == 0) {
					matchMode = MatchMode.END;
					resourceName = resourceName.substring(1);
				}
				if (StringUtils.isNotEmpty(resourceName) && StringUtils.indexOf(resourceName, "*") == resourceName.length() - 1) {
					resourceName = resourceName.substring(0, resourceName.length() - 1);
					matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
				}

				if (StringUtils.isNotEmpty(resourceName)) {
					if (matchMode != null) {
						criteria.add(Restrictions.ilike("name", resourceName, matchMode));
					} else {
						criteria.add(Restrictions.eq("name", resourceName));
					}
				}
			}

            if (StringUtils.isNotEmpty(resource.getURL())) {
                String url = resource.getURL();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(url, "*") == 0) {
                    matchMode = MatchMode.END;
                    url = url.substring(1);
                }
                if (StringUtils.isNotEmpty(url) && StringUtils.indexOf(url, "*") == url.length() - 1) {
                    url = url.substring(0, url.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(url)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("URL", url, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("URL", url));
                    }
                }
            }
			
			if(resource.getAdminResource() != null && StringUtils.isNotBlank(resource.getAdminResource().getId())) {
				criteria.add(Restrictions.eq("adminResource.id", resource.getAdminResource().getId()));
			}

			if (resource.getResourceType() != null) {
				final ResourceTypeEntity type = resource.getResourceType();
				if (StringUtils.isNotBlank(type.getId())) {
					criteria.add(Restrictions.eq("resourceType.id", type.getId()));
				}
			}

			if (CollectionUtils.isNotEmpty(resource.getParentResources())) {
				final Set<String> parentResourceIds = new HashSet<String>();
				for (final ResourceEntity parent : resource
						.getParentResources()) {
					if (parent != null
							&& StringUtils.isNotBlank(parent.getId())) {
						parentResourceIds.add(parent.getId());
					}
				}

				if (CollectionUtils.isNotEmpty(parentResourceIds)) {
					criteria.createAlias("parentResources", "parent").add(
							Restrictions.in("parent.id",
									parentResourceIds));
				}
			}

			if (CollectionUtils.isNotEmpty(resource.getChildResources())) {
				final Set<String> childResoruceIds = new HashSet<String>();
				for (final ResourceEntity child : resource.getChildResources()) {
					if (child != null
							&& StringUtils.isNotBlank(child.getId())) {
						childResoruceIds.add(child.getId());
					}
				}

				if (CollectionUtils.isNotEmpty(childResoruceIds)) {
					criteria.createAlias("childResources", "child").add(
							Restrictions.in("child.id",
									childResoruceIds));
				}
			}
			if(resource.getAdminResource() != null) {
				final ResourceEntity adminResource = resource.getAdminResource();
				if (StringUtils.isNotBlank(adminResource.getId())) {
					criteria.add(Restrictions.eq("adminResource.id", adminResource.getId()));
				}
			}
		}
		return criteria;
	}

	/*
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
	*/

	@Override
	@LocalizedDatabaseGet
	public ResourceEntity findByName(String name) {
		Criteria criteria = getCriteria().add(Restrictions.eq("name", name));
		return (ResourceEntity) criteria.uniqueResult();
	}
	
    @Override
    @LocalizedDatabaseGet
	public List<ResourceEntity> getResourcesByType(String id) {
		Criteria criteria = getCriteria().createAlias("resourceType", "rt")
				.add(Restrictions.eq("rt.id", id))
				.addOrder(Order.asc("displayOrder"));
		return (List<ResourceEntity>) criteria.list();
	}
    
    @Override
    @LocalizedDatabaseGet
	public List<ResourceEntity> getResourcesForRole(final String roleId,
			final int from, final int size, final ResourceSearchBean searchBean) {
		final Criteria criteria = getCriteria()
				.createAlias("roles", "rr")
				.add(Restrictions.eq("rr.id", roleId))
				.addOrder(Order.asc("name"));
		addSearchBeanCriteria(criteria, searchBean);
		
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
	@Deprecated
	public int getNumOfResourcesForRole(String roleId, final ResourceSearchBean searchBean) {
		final Criteria criteria = getCriteria()
				.createAlias("roles", "rr")
				.add(Restrictions.eq("rr.id", roleId))
				.setProjection(rowCount());
		addSearchBeanCriteria(criteria, searchBean);
		
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	@LocalizedDatabaseGet
	@Deprecated
	public List<ResourceEntity> getResourcesForGroup(final String groupId,
			final int from, final int size, final ResourceSearchBean searchBean) {
		final Criteria criteria = getCriteria()
				.createAlias("groups", "rg")
				.add(Restrictions.eq("rg.id", groupId))
				.addOrder(Order.asc("name"));
		addSearchBeanCriteria(criteria, searchBean);
		
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
	@Deprecated
	public int getNumOfResourcesForGroup(final String groupId, final ResourceSearchBean searchBean) {
		final Criteria criteria = getCriteria()
				.createAlias("groups", "rg")
				.add(Restrictions.eq("rg.id", groupId))
				.setProjection(rowCount());
		addSearchBeanCriteria(criteria, searchBean);
		
		return ((Number) criteria.uniqueResult()).intValue();
	}

	private Criteria getResourceForUserCriteria(final String userId) {
		final Criteria criteria = getCriteria().createAlias("users", "ru").add(Restrictions.eq("ru.id", userId));
		return criteria;
	}

	@Override
	@LocalizedDatabaseGet
	@Deprecated
	public List<ResourceEntity> getResourcesForUser(final String userId,
			final int from, final int size, final ResourceSearchBean searchBean) {
		final Criteria criteria = getResourceForUserCriteria(userId);
		addSearchBeanCriteria(criteria, searchBean);
		
		if (from > -1) {
			criteria.setFirstResult(from);
		}

		if (size > -1) {
			criteria.setMaxResults(size);
		}

		return criteria.list();
	}
	
    @Override
    @LocalizedDatabaseGet
    @Deprecated
    public List<ResourceEntity> getResourcesForUserByType(final String userId, String resourceTypeId, final ResourceSearchBean searchBean){
        final Criteria criteria = getResourceForUserCriteria(userId);
        addSearchBeanCriteria(criteria, searchBean);
        criteria.createAlias("resourceType", "rt").add(Restrictions.eq("rt.id", resourceTypeId));
        return criteria.list();
    }
    
	@Override
	@Deprecated
	public int getNumOfResourcesForUser(String userId, final ResourceSearchBean searchBean) {
		final Criteria criteria = getResourceForUserCriteria(userId).setProjection(rowCount());
		addSearchBeanCriteria(criteria, searchBean);
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	private void addSearchBeanCriteria(final Criteria criteria, final ResourceSearchBean searchBean) {
		if(searchBean != null && criteria != null) {
			if(Boolean.TRUE.equals(searchBean.getRootsOnly())) {
				criteria.add(Restrictions.isEmpty("parentResources"));
			}
			
			if(CollectionUtils.isNotEmpty(searchBean.getExcludeResourceTypes())) {
				criteria.add(Restrictions.not(Restrictions.in("resourceType.id", searchBean.getExcludeResourceTypes())));
			}
            if(StringUtils.isNotEmpty(searchBean.getResourceTypeId())) {
                criteria.add(Restrictions.eq("resourceType.id", searchBean.getResourceTypeId()));
            }
			
			if(CollectionUtils.isNotEmpty(searchBean.getAttributes())) {
				criteria.createAlias("resourceProps", "prop");
				for(final Tuple<String, String> attribute : searchBean.getAttributes()) {
					if(StringUtils.isNotBlank(attribute.getKey()) && StringUtils.isNotBlank(attribute.getValue())) {
						criteria.add(Restrictions.and(Restrictions.eq("prop.name", attribute.getKey()), 
								Restrictions.eq("prop.value", attribute.getValue())));
					} else if(StringUtils.isNotBlank(attribute.getKey())) {
						criteria.add(Restrictions.eq("prop.name", attribute.getKey()));
					} else if(StringUtils.isNotBlank(attribute.getValue())) {
						criteria.add(Restrictions.eq("prop.value", attribute.getValue()));
					}
				}
			}

            if(StringUtils.isNotBlank(searchBean.getMetadataType())){
                criteria.add(Restrictions.eq("type.id", searchBean.getMetadataType()));
            }

            if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())){
                criteria.createAlias("groups", "gr");
                criteria.add(Restrictions.in("gr.id", searchBean.getGroupIdSet()));
            }

            if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())){
                criteria.createAlias("parentResources", "pr");
                criteria.add(Restrictions.in("pr.id", searchBean.getParentIdSet()));
            }
            if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())){
                criteria.createAlias("childResources", "ch");
                criteria.add(Restrictions.in("ch.id", searchBean.getChildIdSet()));
            }
            if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())){
                criteria.createAlias("roles", "r");
                criteria.add(Restrictions.in("r.id", searchBean.getRoleIdSet()));
            }
            if(CollectionUtils.isNotEmpty(searchBean.getUserIdSet())){
                criteria.createAlias("users", "usr");
                criteria.add(Restrictions.in("usr.id", searchBean.getUserIdSet()));
            }
			if(StringUtils.isNotBlank(searchBean.getAdminResourceId())){
				criteria.add(Restrictions.eq("adminResource.id", searchBean.getAdminResourceId()));
			}
		}
	}
}
