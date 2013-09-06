package org.openiam.idm.srvc.role.service;

// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.searchbean.converter.RoleSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        return "roleId";
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof RoleSearchBean) {
            final RoleSearchBean roleSearchBean = (RoleSearchBean)searchBean;

            final RoleEntity exampleEnity = roleSearchBeanConverter.convert(roleSearchBean);
            exampleEnity.setRoleId(null);
            criteria = this.getExampleCriteria(exampleEnity);

            if(roleSearchBean.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(), roleSearchBean.getKeys()));
            }else if(StringUtils.isNotBlank(roleSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), roleSearchBean.getKey()));
            }
        }
        return criteria;
    }

	@Override
	protected Criteria getExampleCriteria(final RoleEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getRoleId())) {
				criteria.add(Restrictions.eq("roleId", entity.getRoleId()));
			} else {

				if (StringUtils.isNotEmpty(entity.getRoleName())) {
	                String roleName = entity.getRoleName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(roleName, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    roleName = roleName.substring(1);
	                }
	                if (StringUtils.isNotBlank(roleName) && StringUtils.indexOf(roleName, "*") == roleName.length() - 1) {
	                	roleName = roleName.substring(0, roleName.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotBlank(roleName)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("roleName", roleName, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("roleName", roleName));
	                    }
	                }
	            }
				
				if(StringUtils.isNotBlank(entity.getServiceId())) {
					criteria.add(Restrictions.eq("serviceId", entity.getServiceId()));
				}
				
				if(CollectionUtils.isNotEmpty(entity.getResourceRoles())) {
					final Set<String> resourceIds = new HashSet<String>();
	            	for(final ResourceRoleEntity resourceRole : entity.getResourceRoles()) {
	            		if(resourceRole != null && StringUtils.isNotBlank(resourceRole.getId().getResourceId())) {
	            			resourceIds.add(resourceRole.getId().getResourceId());
	            		}
	            	}
	            	
	            	if(CollectionUtils.isNotEmpty(resourceIds)) {
	            		criteria.createAlias("resourceRoles", "rr").add( Restrictions.in("rr.id.resourceId", resourceIds));
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
            criteria.createAlias("users", "ur")
                    .add(Restrictions.eq("ur.userId", userId));
        }

        if(StringUtils.isNotBlank(groupId)){
            criteria.createAlias("groups", "groups").add( Restrictions.eq("groups.grpId", groupId));
        }

        if(StringUtils.isNotBlank(resourceId)){
            criteria.createAlias("resourceRoles", "resourceRole").add( Restrictions.eq("resourceRole.id.resourceId", resourceId));
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
        final Criteria criteria = getCriteria().createAlias("childRoles", "role").add( Restrictions.eq("role.roleId", roleId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private Criteria getChildRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add( Restrictions.eq("role.roleId", roleId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }
	
	private Criteria getRolesForUserCriteria(final String userId, final Set<String> filter) {
		return getCriteria()
	               .createAlias("userRoles", "ur")
	               .add(Restrictions.eq("ur.userId", userId));
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

}
