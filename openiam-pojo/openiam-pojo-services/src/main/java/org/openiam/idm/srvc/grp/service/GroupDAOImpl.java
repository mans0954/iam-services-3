package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
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
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof GroupSearchBean) {
            final GroupSearchBean groupSearchBean = (GroupSearchBean)searchBean;

            final GroupEntity exampleEnity = groupSearchBeanConverter.convert(groupSearchBean);
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
		if(StringUtils.isNotBlank(group.getId())) {
			criteria.add(Restrictions.eq(getPKfieldName(), group.getId()));
		} else {
			if (StringUtils.isNotEmpty(group.getName())) {
                String groupName = group.getName();
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
                        criteria.add(Restrictions.ilike("name", groupName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", groupName));
                    }
                }
            }
			
			/*
			if(StringUtils.isNotBlank(group.getOwnerId())) {
				criteria.add(Restrictions.eq("ownerId", group.getOwnerId()));
			}
			
			if(StringUtils.isNotBlank(group.getInternalGroupId())) {
				criteria.add(Restrictions.eq("internalGroupId", group.getInternalGroupId()));
			}
			*/
            
            if(CollectionUtils.isNotEmpty(group.getResources())) {
            	final Set<String> resourceIds = new HashSet<String>();
            	for(final ResourceEntity resourceEntity : group.getResources()) {
            		if(resourceEntity != null && StringUtils.isNotBlank(resourceEntity.getResourceId())) {
            			resourceIds.add(resourceEntity.getResourceId());
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
	

	public List<GroupEntity> getGroupsForUser(final String userId, Set<String> filter, final int from, final int size) {
		final Criteria criteria = getEntitlementGroupsCriteria(userId, null, null, filter);
        return getList(criteria, from, size);
	}


	@Override
	public List<GroupEntity> getGroupsForRole(final String roleId, Set<String> filter, int from, int size) {
		final Criteria criteria = getEntitlementGroupsCriteria(null, roleId, null, filter);
        return getList(criteria, from, size);
	}


    @Override
    public List<GroupEntity> getGroupsForResource(final String resourceId, Set<String> filter, int from, int size){
        final Criteria criteria = getEntitlementGroupsCriteria(null, null, resourceId, filter);
        return getList(criteria, from, size);
    }


    @Override
    public List<GroupEntity> getChildGroups(final String groupId, Set<String> filter, int from, int size) {
        final Criteria criteria = getChildGroupsCriteria(groupId, filter);
        return getList(criteria, from, size);
    }

    @Override
    public List<GroupEntity> getParentGroups(final String groupId, Set<String> filter, int from, int size) {
        final Criteria criteria = getParentGroupsCriteria(groupId, filter);
        return getList(criteria, from, size);
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, Set<String> filter) {
        final Criteria criteria = getEntitlementGroupsCriteria(userId, null, null, filter).setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
    public int getNumOfGroupsForRole(final String roleId, Set<String> filter) {
        final Criteria criteria = getEntitlementGroupsCriteria(null, roleId, null, filter);
        criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
    public int getNumOfGroupsForResource(final String resourceId, Set<String> filter){
        final Criteria criteria = getEntitlementGroupsCriteria(null, null, resourceId, filter);
        criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
	public int getNumOfChildGroups(final String groupId, Set<String> filter) {
		final Criteria criteria = getChildGroupsCriteria(groupId, filter);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentGroups(final String groupId, Set<String> filter) {
        final Criteria criteria = getParentGroupsCriteria(groupId, filter);
                       criteria.setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

    private Criteria getEntitlementGroupsCriteria(String userId, String roleId, String resourceId, Set<String> filter){
        final Criteria criteria = super.getCriteria();

            if(StringUtils.isNotBlank(userId)){
            criteria.createAlias("users", "u")
                    .add(Restrictions.eq("u.userId", userId));
        }

        if(StringUtils.isNotBlank(roleId)){
            criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.id", roleId));
        }

        if(StringUtils.isNotBlank(resourceId)){
            criteria.createAlias("resources", "resources").add( Restrictions.eq("resources.resourceId", resourceId));
        }

        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }

        return criteria;
    }


    private Criteria getParentGroupsCriteria(final String groupId, Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("childGroups", "group").add( Restrictions.eq("group.id", groupId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private Criteria getChildGroupsCriteria(final String groupId, Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("parentGroups", "group").add( Restrictions.eq("group.id", groupId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private List<GroupEntity> getList(Criteria criteria, int from, int size){
        if(from > -1) {
            criteria.setFirstResult(from);
        }

        if(size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }
}


