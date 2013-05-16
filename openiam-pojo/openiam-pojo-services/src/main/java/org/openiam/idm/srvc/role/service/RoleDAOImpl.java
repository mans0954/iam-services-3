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
import org.openiam.idm.searchbeans.MembershipRoleSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.searchbean.converter.RoleSearchBeanConverter;
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
	                if (StringUtils.isNotEmpty(roleName) && StringUtils.indexOf(roleName, "*") == roleName.length() - 1) {
	                	roleName = roleName.substring(0, roleName.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotEmpty(roleName)) {
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
	public List<RoleEntity> findUserRoles(final String userId, final int from, final int size) {
		final Query qry = getSession().createQuery("select role from RoleEntity role, UserRoleEntity ur " +
				" where ur.userId = :userId and " +
				"       ur.roleId = role.roleId" + 
				" order by role.roleName ");
		
	
		qry.setString("userId", userId);
		if(from > -1) {
			qry.setFirstResult(from);
		}
		
		if(size > -1) {
			qry.setMaxResults(size);
		}
		return qry.list();
	}

    @Override
    protected String getPKfieldName() {
        return "roleId";
    }

//	@Override
//	public List<RoleEntity> getRolesForGroup(final String groupId, final int from, final int size) {
//		final Criteria criteria = super.getCriteria();
//		criteria.createAlias("groups", "groups").add( Restrictions.in("groups.grpId", new String[] {groupId}));
//		if(from > -1) {
//			criteria.setFirstResult(from);
//		}
//
//		if(size > -1) {
//			criteria.setMaxResults(size);
//		}
//		return criteria.list();
//	}
//
//	@Override
//	public int getNumOfRolesForGroup(String groupId) {
//		final Criteria criteria = super.getCriteria();
//		criteria.createAlias("groups", "groups").add( Restrictions.in("groups.grpId", new String[] {groupId})).setProjection(rowCount());
//
//		return ((Number)criteria.uniqueResult()).intValue();
//	}
//
//
//
//
//	@Override
//	public int getNumOfRolesForResource(final String resourceId) {
//		ResourceRoleEntity rrEntity = new ResourceRoleEntity();
//		rrEntity.setId(new ResourceRoleEmbeddableId(null, resourceId));
//
//		final RoleEntity entity = new RoleEntity();
//		entity.addResourceRole(rrEntity);
//		return count(entity);
//	}
//
//	@Override
//	public List<RoleEntity> getRolesForResource(final String resourceId, final int from, final int size) {
//		ResourceRoleEntity rrEntity = new ResourceRoleEntity();
//		rrEntity.setId(new ResourceRoleEmbeddableId(null, resourceId));
//
//		final RoleEntity entity = new RoleEntity();
//		entity.addResourceRole(rrEntity);
//		return getByExample(entity, from, size);
//	}
//
//    @Override
//    public List<RoleEntity> getRolesForUser(String userId, int from, int size) {
//        final Criteria criteria = getRolesForUserCriteria(userId);
//
//        if(from > -1) {
//            criteria.setFirstResult(from);
//        }
//
//        if(size > -1) {
//            criteria.setMaxResults(size);
//        }
//
//        return criteria.list();
//    }
//
//    @Override
//    public int getNumOfRolesForUser(String userId) {
//        final Criteria criteria = getRolesForUserCriteria(userId).setProjection(rowCount());
//        return ((Number)criteria.uniqueResult()).intValue();
//    }


    @Override
    public List<RoleEntity> getEntitlementRoles(MembershipRoleSearchBean searchBean, int from, int size){
        final Criteria criteria = getEntitlementRolesCriteria(searchBean);
        if(from > -1) {
            criteria.setFirstResult(from);
        }

        if(size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();

    }

    @Override
    public int getNumOfEntitlementRoles(MembershipRoleSearchBean searchBean){
        final Criteria criteria = getEntitlementRolesCriteria(searchBean);
        criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    private Criteria getEntitlementRolesCriteria(MembershipRoleSearchBean searchBean){
        final Criteria criteria = super.getCriteria();

        if(searchBean.getUserId()!=null && !searchBean.getUserId().isEmpty()){
            criteria.createAlias("userRoles", "ur")
                    .add(Restrictions.eq("ur.userId", searchBean.getUserId()));
        }

        if(searchBean.getGroupId()!=null && !searchBean.getGroupId().isEmpty()){
            criteria.createAlias("groups", "groups").add( Restrictions.eq("groups.grpId", searchBean.getGroupId()));
        }

        if(searchBean.getResourceId()!=null && !searchBean.getResourceId().isEmpty()){
            criteria.createAlias("resourceRoles", "resourceRole").add( Restrictions.eq("resourceRole.resourceId", searchBean.getResourceId()));
        }

        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }

        return criteria;
    }

	@Override
	public List<RoleEntity> getChildRoles(MembershipRoleSearchBean searchBean, int from, int size) {
		final Criteria criteria = getChildRolesCriteria(searchBean);
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
	
	@Override
	public List<RoleEntity> getParentRoles(MembershipRoleSearchBean searchBean, int from, int size) {
		final Criteria criteria = getParentRolesCriteria(searchBean);
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}

	@Override
	public int getNumOfChildRoles(MembershipRoleSearchBean searchBean) {
        final Criteria criteria =  getChildRolesCriteria(searchBean);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentRoles(MembershipRoleSearchBean searchBean) {
		final Criteria criteria =  getParentRolesCriteria(searchBean);
                       criteria.setProjection(rowCount());
		
		return ((Number)criteria.uniqueResult()).intValue();
	}

    private Criteria getParentRolesCriteria(final MembershipRoleSearchBean searchBean) {
        final Criteria criteria = getCriteria().createAlias("childRoles", "role").add( Restrictions.eq("role.roleId", searchBean.getMembershipRoleId()));
        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }
        return criteria;
    }

    private Criteria getChildRolesCriteria(final MembershipRoleSearchBean searchBean) {
        final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add( Restrictions.eq("role.roleId", searchBean.getMembershipRoleId()));
        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }
        return criteria;
    }
	
//	private Criteria getRolesForUserCriteria(final String userId) {
//		return getCriteria()
//	               .createAlias("userRoles", "ur")
//	               .add(Restrictions.eq("ur.userId", userId));
//	}


}
