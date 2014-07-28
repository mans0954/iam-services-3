package org.openiam.idm.srvc.role.service;

// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.openiam.base.Tuple;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.searchbean.converter.RoleSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

@Repository("roleDAO")
public class RoleDAOImpl extends BaseDaoImpl<RoleEntity, String> implements RoleDAO {

	private static final Log log = LogFactory.getLog(RoleDAOImpl.class);

    @Autowired
    private RoleSearchBeanConverter roleSearchBeanConverter;

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof RoleSearchBean) {
            final RoleSearchBean roleSearchBean = (RoleSearchBean)searchBean;

            final RoleEntity exampleEnity = roleSearchBeanConverter.convert(roleSearchBean);
            criteria = this.getExampleCriteria(exampleEnity);

            if(roleSearchBean.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(), roleSearchBean.getKeys()));
            }else if(StringUtils.isNotBlank(roleSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), roleSearchBean.getKey()));
            }

            if(CollectionUtils.isNotEmpty(roleSearchBean.getAttributes())) {
                for(final Tuple<String, String> attribute : roleSearchBean.getAttributes()) {
                    DetachedCriteria crit = DetachedCriteria.forClass(RoleAttributeEntity.class);
                    if(StringUtils.isNotBlank(attribute.getKey()) && StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.and(Restrictions.eq("name", attribute.getKey()),
                                Restrictions.eq("value", attribute.getValue())));
                    } else if(StringUtils.isNotBlank(attribute.getKey())) {
                        crit.add(Restrictions.eq("name", attribute.getKey()));
                    } else if(StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.eq("value", attribute.getValue()));
                    }
                    crit.setProjection(Projections.property("role.id"));
                    criteria.add(Subqueries.propertyIn("id", crit));
                }
            }

            if(StringUtils.isNotBlank(roleSearchBean.getType())){
                criteria.add(Restrictions.eq("type.id", roleSearchBean.getType()));
            }
        }
        return criteria;
    }

	@Override
	protected Criteria getExampleCriteria(final RoleEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
			} else {

				if (StringUtils.isNotEmpty(entity.getName())) {
	                String name = entity.getName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(name, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    name = name.substring(1);
	                }
	                if (StringUtils.isNotBlank(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
	                	name = name.substring(0, name.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotBlank(name)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("name", name, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("name", name));
	                    }
	                }
	            }

                if(entity.getManagedSystem()!=null && StringUtils.isNotBlank(entity.getManagedSystem().getId())){
                    criteria.add(Restrictions.eq("managedSystem.id", entity.getManagedSystem().getId()));
                }
				
				if(entity.getAdminResource() != null && StringUtils.isNotBlank(entity.getAdminResource().getId())) {
					criteria.add(Restrictions.eq("adminResource.id", entity.getAdminResource().getId()));
				}
				
				if(CollectionUtils.isNotEmpty(entity.getResources())) {
					final Set<String> resourceIds = new HashSet<String>();
	            	for(final ResourceEntity resourceRole : entity.getResources()) {
	            		if(resourceRole != null && StringUtils.isNotBlank(resourceRole.getId())) {
	            			resourceIds.add(resourceRole.getId());
	            		}
	            	}
	            	
	            	if(CollectionUtils.isNotEmpty(resourceIds)) {
	            		criteria.createAlias("resources", "rr").add( Restrictions.in("rr.id", resourceIds));
	            	}
				}
			}
		}
		return criteria;
	}

	@Override
	public List<RoleEntity> getRolesForGroup(final String groupId, final Set<String> filter, final int from, final int size) {
		final Criteria criteria = getEntitlementRolesCriteria(null, groupId, null, filter);
        return getList(criteria, from, size);
	}

	@Override
	public int getNumOfRolesForGroup(String groupId, final Set<String> filter) {
		final Criteria criteria = getEntitlementRolesCriteria(null, groupId, null, filter).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId, final Set<String> filter) {
        final Criteria criteria = getEntitlementRolesCriteria(null, null, resourceId, filter).setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public List<RoleEntity> getRolesForResource(final String resourceId, final Set<String> filter, final int from, final int size) {
        final Criteria criteria = getEntitlementRolesCriteria(null, null, resourceId, filter);
        return getList(criteria, from, size);
	}

    @Override
    public List<RoleEntity> getRolesForUser(String userId, final Set<String> filter, int from, int size) {
        final Criteria criteria = getEntitlementRolesCriteria(userId, null, null, filter);
        return getList(criteria, from, size);
    }

    @Override
    public int getNumOfRolesForUser(String userId, final Set<String> filter) {
        final Criteria criteria = getEntitlementRolesCriteria(userId, null, null, filter).setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }


    private Criteria getEntitlementRolesCriteria(String userId, String groupId, String resourceId, final Set<String> filter){
        final Criteria criteria = super.getCriteria();

        if(StringUtils.isNotBlank(userId)){
            criteria.createAlias("users", "u")
                    .add(Restrictions.eq("u.id", userId));
        }

        if(StringUtils.isNotBlank(groupId)){
            criteria.createAlias("groups", "groups").add( Restrictions.eq("groups.id", groupId));
        }

        if(StringUtils.isNotBlank(resourceId)){
            criteria.createAlias("resources", "resources").add( Restrictions.eq("resources.id", resourceId));
        }

        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }

        return criteria;
    }

	@Override
	public List<RoleEntity> getChildRoles(final String roleId, final Set<String> filter, int from, int size) {
		final Criteria criteria = getChildRolesCriteria(roleId, filter);
		return  getList(criteria, from, size);
	}
	
	@Override
	public List<RoleEntity> getParentRoles(final String roleId, final Set<String> filter, int from, int size) {
		final Criteria criteria = getParentRolesCriteria(roleId, filter);
		return getList(criteria, from, size);
	}

	@Override
	public int getNumOfChildRoles(final String roleId, final Set<String> filter) {
        final Criteria criteria =  getChildRolesCriteria(roleId, filter);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentRoles(final String roleId, final Set<String> filter) {
		final Criteria criteria =  getParentRolesCriteria(roleId, filter);
                       criteria.setProjection(rowCount());
		
		return ((Number)criteria.uniqueResult()).intValue();
	}

    private Criteria getParentRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("childRoles", "role").add( Restrictions.eq("role.id", roleId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private Criteria getChildRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add( Restrictions.eq("role.id", roleId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }
	
	private Criteria getRolesForUserCriteria(final String userId, final Set<String> filter) {
		return getCriteria()
	               .createAlias("users", "u")
	               .add(Restrictions.eq("u.id", userId));
	}

    private List<RoleEntity> getList(Criteria criteria, int from, int size){
        if(from > -1) {
            criteria.setFirstResult(from);
        }

        if(size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    public List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue) {
        List ret = new ArrayList<RoleEntity>();
        if (StringUtils.isNotBlank(attrName)) {
            // Can't use Criteria for @ElementCollection due to Hibernate bug
            // (org.hibernate.MappingException: collection was not an association)
            ret = getHibernateTemplate().find("select ra.role from RoleAttributeEntity ra left join ra.values av where ra.name = ? and ((ra.isMultivalued = false and ra.value = ?) or (ra.isMultivalued = true and av in ?))", attrName, attrValue, attrValue);
        }
        return ret;
    }

}
