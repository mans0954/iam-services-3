package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openiam.base.SysConfiguration;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.searchbean.converter.GroupSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("groupDAO")
public class GroupDAOImpl extends BaseDaoImpl<GroupEntity, String> implements GroupDAO {
    @Autowired
    private GroupSearchBeanConverter groupSearchBeanConverter;
    
    @Autowired
    private SysConfiguration sysConfig;

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
            
            if(CollectionUtils.isNotEmpty(groupSearchBean.getRoleIdSet())){    
                criteria.createAlias("roles", "roleXrefs")
						.createAlias("roleXrefs.entity", "role").add(
						Restrictions.in("role.id", groupSearchBean.getRoleIdSet()));
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

            if(CollectionUtils.isNotEmpty(groupSearchBean.getOrganizationIdSet())){    
                criteria.createAlias("organizations", "organizationXrefs")
						.createAlias("organizationXrefs.entity", "organization").add(
						Restrictions.in("organization.id", groupSearchBean.getOrganizationIdSet()));
            }
            if(CollectionUtils.isNotEmpty(groupSearchBean.getResourceIdSet())) {
            	criteria.createAlias("resources", "resourceXrefs")
						.createAlias("resourceXrefs.memberEntity", "resource").add(
						Restrictions.in("resource.id", groupSearchBean.getResourceIdSet()));
			}
            if(CollectionUtils.isNotEmpty(groupSearchBean.getUserIdSet())){
            	criteria.createAlias("users", "userXrefs")
						.createAlias("userXrefs.memberEntity", "user").add(
								Restrictions.in("user.id", groupSearchBean.getUserIdSet()));
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
				criteria.add(getStringCriterion("name", group.getName(), sysConfig.isCaseInSensitiveDatabase()));
            }
			
			if(group.getManagedSystem() != null && StringUtils.isNotBlank(group.getManagedSystem().getId())) {
				criteria.add(Restrictions.eq("managedSystem.id", group.getManagedSystem().getId()));
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


