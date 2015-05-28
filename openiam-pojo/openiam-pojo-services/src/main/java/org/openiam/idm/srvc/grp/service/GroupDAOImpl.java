package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
            
            if(CollectionUtils.isNotEmpty(groupSearchBean.getChildIdSet())) {
            	criteria.createAlias("childGroups", "childXrefs")
						.createAlias("childXrefs.memberEntity", "child").add(
						Restrictions.in("child.id", groupSearchBean.getChildIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(groupSearchBean.getParentIdSet())) {
				criteria.createAlias("parentGroups", "parentXrefs")
						.createAlias("parentXrefs.entity", "parent").add(
						Restrictions.in("parent.id", groupSearchBean.getParentIdSet()));
			}

            if(CollectionUtils.isNotEmpty(groupSearchBean.getRoleIdSet())){
                criteria.createAlias("roles", "r");
                criteria.add(Restrictions.in("r.id", groupSearchBean.getRoleIdSet()));
            }
            if(CollectionUtils.isNotEmpty(groupSearchBean.getOrganizationIdSet())){    
                criteria.createAlias("organizations", "organizationXrefs")
						.createAlias("organizationXrefs.entity", "organization").add(
						Restrictions.in("organization.id", groupSearchBean.getOrganizationIdSet()));
            }
            if(CollectionUtils.isNotEmpty(groupSearchBean.getResourceIdSet())){
                criteria.createAlias("resources", "res");
                criteria.add(Restrictions.in("res.id", groupSearchBean.getResourceIdSet()));
            }
            if(CollectionUtils.isNotEmpty(groupSearchBean.getUserIdSet())){
                criteria.createAlias("users", "usr");
                criteria.add(Restrictions.in("usr.id", groupSearchBean.getUserIdSet()));
            }

            if(CollectionUtils.isNotEmpty(groupSearchBean.getAttributes())) {
                for(final Tuple<String, String> attribute : groupSearchBean.getAttributes()) {
                    DetachedCriteria crit = DetachedCriteria.forClass(GroupAttributeEntity.class);
                    if(StringUtils.isNotBlank(attribute.getKey()) && StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.and(Restrictions.eq("name", attribute.getKey()),
                                Restrictions.eq("value", attribute.getValue())));
                    } else if(StringUtils.isNotBlank(attribute.getKey())) {
                        crit.add(Restrictions.eq("name", attribute.getKey()));
                    } else if(StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.eq("value", attribute.getValue()));
                    }
                    crit.setProjection(Projections.property("group.id"));
                    criteria.add(Subqueries.propertyIn("id", crit));
                }
            }

            if(StringUtils.isNotBlank(groupSearchBean.getType())){
                criteria.add(Restrictions.eq("type.id", groupSearchBean.getType()));
            }
			if(StringUtils.isNotBlank(groupSearchBean.getAdminResourceId())) {
				criteria.add(Restrictions.eq("adminResource.id", groupSearchBean.getAdminResourceId()));
			}
		}
        return criteria;
    }

    @Override
    /**
     * Without Localization
     */
    public List<GroupEntity> getByExample(final SearchBean searchBean, int from, int size) {
        return super.getByExample(searchBean, from, size);
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
			
			if(group.getAdminResource() != null && StringUtils.isNotBlank(group.getAdminResource().getId())) {
				criteria.add(Restrictions.eq("adminResource.id", group.getAdminResource().getId()));
			}
			
			if(group.getManagedSystem() != null && StringUtils.isNotBlank(group.getManagedSystem().getId())) {
				criteria.add(Restrictions.eq("managedSystem.id", group.getManagedSystem().getId()));
			}
			
//			if(group.getCompany() != null && StringUtils.isNotBlank(group.getCompany().getId())) {
//				criteria.add(Restrictions.eq("company.id", group.getCompany().getId()));
//			}

            if(CollectionUtils.isNotEmpty(group.getResources())) {
            	final Set<String> resourceIds = new HashSet<String>();
            	for(final ResourceEntity resourceEntity : group.getResources()) {
            		if(resourceEntity != null && StringUtils.isNotBlank(resourceEntity.getId())) {
            			resourceIds.add(resourceEntity.getId());
            		}
            	}
            	
            	if(CollectionUtils.isNotEmpty(resourceIds)) {
            		criteria.createAlias("resources", "resources").add( Restrictions.in("resources.id", resourceIds));
            	}
            }
		}
		return criteria;
	}

    protected void setOderByCriteria(Criteria criteria, AbstractSearchBean sb) {
        List<SortParam> sortParamList = sb.getSortBy();
        for (SortParam sort: sortParamList){
            if("managedSysName".equals(sort.getSortBy())){
                criteria.createAlias("managedSystem", "ms", Criteria.LEFT_JOIN);
                criteria.addOrder(createOrder("ms.name", sort.getOrderBy()));
            } else{
                criteria.addOrder(createOrder(sort.getSortBy(), sort.getOrderBy()));
            }
        }
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
	
	@Deprecated
	public List<GroupEntity> getGroupsForUser(final String userId, Set<String> filter, final int from, final int size) {
		final Criteria criteria = getEntitlementGroupsCriteria(userId, null, null, filter);
        return getList(criteria, from, size);
	}


	@Override
	@Deprecated
	public List<GroupEntity> getGroupsForRole(final String roleId, Set<String> filter, int from, int size) {
		final Criteria criteria = getEntitlementGroupsCriteria(null, roleId, null, filter);
        return getList(criteria, from, size);
	}


    @Override
    @Deprecated
    public List<GroupEntity> getGroupsForResource(final String resourceId, Set<String> filter, int from, int size){
        final Criteria criteria = getEntitlementGroupsCriteria(null, null, resourceId, filter);
        return getList(criteria, from, size);
    }


    @Override
    @Deprecated
    public int getNumOfGroupsForUser(final String userId, Set<String> filter) {
        final Criteria criteria = getEntitlementGroupsCriteria(userId, null, null, filter).setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForRole(final String roleId, Set<String> filter) {
        final Criteria criteria = getEntitlementGroupsCriteria(null, roleId, null, filter);
        criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
    @Deprecated
    public int getNumOfGroupsForResource(final String resourceId, Set<String> filter){
        final Criteria criteria = getEntitlementGroupsCriteria(null, null, resourceId, filter);
        criteria.setProjection(rowCount());
        return ((Number)criteria.uniqueResult()).intValue();
    }

    private Criteria getEntitlementGroupsCriteria(String userId, String roleId, String resourceId, Set<String> filter){
        final Criteria criteria = super.getCriteria();

            if(StringUtils.isNotBlank(userId)){
            criteria.createAlias("users", "u")
                    .add(Restrictions.eq("u.id", userId));
        }

        if(StringUtils.isNotBlank(roleId)){
            criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.id", roleId));
        }

        if(StringUtils.isNotBlank(resourceId)){
            criteria.createAlias("resources", "resources").add( Restrictions.eq("resources.id", resourceId));
        }

        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }

        return criteria;
    }

    @Deprecated
    private Criteria getParentGroupsCriteria(final String groupId, Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("childGroups", "group").add( Restrictions.eq("group.id", groupId));
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    @Deprecated
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


