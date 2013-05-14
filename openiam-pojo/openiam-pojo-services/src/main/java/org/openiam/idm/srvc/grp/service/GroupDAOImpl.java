package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MembershipGroupSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

@Repository("groupDAO")
public class GroupDAOImpl extends BaseDaoImpl<GroupEntity, String> implements GroupDAO {
    @Autowired
    private GroupSearchBeanConverter groupSearchBeanConverter;

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof GroupSearchBean) {
            final GroupSearchBean groupSearchBean = (GroupSearchBean)searchBean;

            final GroupEntity exampleEnity = groupSearchBeanConverter.convert(groupSearchBean);
            exampleEnity.setGrpId(null);
            criteria = this.getExampleCriteria(exampleEnity);

            if(groupSearchBean.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(), groupSearchBean.getKeys()));
            }else if(StringUtils.isNotBlank(groupSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), groupSearchBean.getKey()));
            }
        }
        return criteria;
    }

	@Override
	protected Criteria getExampleCriteria(GroupEntity group) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(group.getGrpId())) {
			criteria.add(Restrictions.eq("grpId", group.getGrpId()));
		} else {
			if (StringUtils.isNotEmpty(group.getGrpName())) {
                String groupName = group.getGrpName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(groupName, "*") == 0) {
                    matchMode = MatchMode.END;
                    groupName = groupName.substring(1);
                }
                if (StringUtils.isNotEmpty(groupName) && StringUtils.indexOf(groupName, "*") == groupName.length() - 1) {
                	groupName = groupName.substring(0, groupName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(groupName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("grpName", groupName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("grpName", groupName));
                    }
                }
            }
			
			if(StringUtils.isNotBlank(group.getOwnerId())) {
				criteria.add(Restrictions.eq("ownerId", group.getOwnerId()));
			}
			
			if(StringUtils.isNotBlank(group.getInternalGroupId())) {
				criteria.add(Restrictions.eq("internalGroupId", group.getInternalGroupId()));
			}
            
            if(CollectionUtils.isNotEmpty(group.getResourceGroups())) {
            	final Set<String> resourceIds = new HashSet<String>();
            	for(final ResourceGroupEntity resourceGroupEntity : group.getResourceGroups()) {
            		if(resourceGroupEntity != null && StringUtils.isNotBlank(resourceGroupEntity.getResourceId())) {
            			resourceIds.add(resourceGroupEntity.getResourceId());
            		}
            	}
            	
            	if(CollectionUtils.isNotEmpty(resourceIds)) {
            		criteria.createAlias("resourceGroups", "resourceGroup").add( Restrictions.in("resourceGroup.resourceId", resourceIds));
            	}
            }
		}
		return criteria;
	}
	
	public List<GroupEntity> findRootGroups(final int from, final int size) {
		final Criteria criteria = getCriteria();
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > 0) {
			criteria.setMaxResults(size);
		}
		criteria.add(Restrictions.isEmpty("parentGroups"));

		return (List<GroupEntity>)criteria.list();
	}
	
	private Criteria getGroupsForUserCriteria(final String userId) {
		return getCriteria()
	               .createAlias("userGroups", "ug")
	               .add(Restrictions.eq("ug.userId", userId));
	}
	
//	@Override
//	public int getNumOfGroupsForUser(MembershipGroupSearchBean searchBean) {
//		final Criteria criteria = getGroupsForUserCriteria(userId).setProjection(rowCount());
//		return ((Number)criteria.uniqueResult()).intValue();
//	}
//
//	public List<GroupEntity> getGroupsForUser(final MembershipGroupSearchBean searchBean, final int from, final int size) {
//		final Criteria criteria = getGroupsForUserCriteria(userId);
//
//		if(from > -1) {
//			criteria.setFirstResult(from);
//		}
//
//		if(size > -1) {
//			criteria.setMaxResults(size);
//		}
//
//		return criteria.list();
//	}

	@Override
	protected String getPKfieldName() {
		return "grpId";
	}

//	@Override
//	public List<GroupEntity> getGroupsForRole(MembershipGroupSearchBean searchBean, int from, int size) {
//		final Criteria criteria = super.getCriteria();
//		criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.roleId", roleId));
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
//	public int getNumOfGroupsForRole(MembershipGroupSearchBean searchBean) {
//		final Criteria criteria = super.getCriteria();
//		criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.roleId", roleId)).setProjection(rowCount());
//		return ((Number)criteria.uniqueResult()).intValue();
//	}

    @Override
    public List<GroupEntity> getEntitlementGroups(MembershipGroupSearchBean searchBean, int from, int size){
        final Criteria criteria = getEntitlementGroupsCriteria(searchBean);
        if(from > -1) {
            criteria.setFirstResult(from);
        }

        if(size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();

    }

    @Override
    public int getNumOfEntitlementGroups(MembershipGroupSearchBean searchBean){
        final Criteria criteria = getEntitlementGroupsCriteria(searchBean);
                       criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    private Criteria getEntitlementGroupsCriteria(MembershipGroupSearchBean searchBean){
        final Criteria criteria = super.getCriteria();

        if(searchBean.getUserId()!=null && !searchBean.getUserId().isEmpty()){
            criteria.createAlias("userGroups", "ug")
                    .add(Restrictions.eq("ug.userId", searchBean.getUserId()));
        }

        if(searchBean.getRoleId()!=null && !searchBean.getRoleId().isEmpty()){
            criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.roleId", searchBean.getRoleId()));
        }

        if(searchBean.getResourceId()!=null && !searchBean.getResourceId().isEmpty()){
            criteria.createAlias("resourceGroups", "resourceGroup").add( Restrictions.eq("resourceGroup.resourceId", searchBean.getResourceId()));
        }

        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }

        return criteria;
    }


    @Override
	public int getNumOfChildGroups(MembershipGroupSearchBean searchBean) {
		final Criteria criteria = getChildGroupsCriteria(searchBean);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentGroups(MembershipGroupSearchBean searchBean) {
        final Criteria criteria = getParentGroupsCriteria(searchBean);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public List<GroupEntity> getChildGroups(MembershipGroupSearchBean searchBean, int from, int size) {
		final Criteria criteria = getChildGroupsCriteria(searchBean);

		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}

	@Override
	public List<GroupEntity> getParentGroups(MembershipGroupSearchBean searchBean, int from, int size) {
		final Criteria criteria = getParentGroupsCriteria(searchBean);

		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}


    private Criteria getParentGroupsCriteria(final MembershipGroupSearchBean searchBean) {
        final Criteria criteria = getCriteria().createAlias("childGroups", "group").add( Restrictions.eq("group.grpId", searchBean.getMembershipGroupId()));
        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }
        return criteria;
    }

    private Criteria getChildGroupsCriteria(final MembershipGroupSearchBean searchBean) {
        final Criteria criteria = getCriteria().createAlias("parentGroups", "group").add( Restrictions.eq("group.grpId", searchBean.getMembershipGroupId()));
        if(searchBean.hasMultipleKeys()){
            criteria.add( Restrictions.in(getPKfieldName(), searchBean.getKeys()));
        }
        return criteria;
    }
}


