package org.openiam.idm.srvc.role.service;

// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeObjectId;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("roleDAO")
public class RoleDAOImpl extends BaseDaoImpl<RoleEntity, String> implements RoleDAO {
	
	@Autowired
	private SysConfiguration sysConfig;

	private static final Log log = LogFactory.getLog(RoleDAOImpl.class);
    
    /*
    @Override
    protected boolean isCachable() {
    	return false;
    }
    */

    @Override
    protected String getPKfieldName() {
        return "id";
    }


    private final ConcurrentHashMap<String, TreeObjectId> rolesHierarchyIds = new ConcurrentHashMap<String, TreeObjectId>();


    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof RoleSearchBean) {
            final RoleSearchBean roleSearchBean = (RoleSearchBean)searchBean;

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
            
            Criterion c = getStringCriterion("name", roleSearchBean.getName(), sysConfig.isCaseInSensitiveDatabase());
			if(c != null) {
				criteria.add(c);
			}
            if(StringUtils.isNotBlank(roleSearchBean.getManagedSysId())){
                criteria.add(Restrictions.eq("managedSystem.id", roleSearchBean.getManagedSysId()));
            }
            c = getStringCriterion("description", roleSearchBean.getDescription(), false);
            if(c != null) {
				criteria.add(c);
			}

            if(StringUtils.isNotBlank(roleSearchBean.getType())){
                criteria.add(Restrictions.eq("type.id", roleSearchBean.getType()));
            }

            if(CollectionUtils.isNotEmpty(roleSearchBean.getGroupIdSet())) {
            	criteria.createAlias("groups", "groupXrefs")
						.createAlias("groupXrefs.memberEntity", "group").add(
						Restrictions.in("group.id", roleSearchBean.getGroupIdSet()));
			}
            
            if(CollectionUtils.isNotEmpty(roleSearchBean.getResourceIdSet())) {
            	criteria.createAlias("resources", "resourceXrefs")
						.createAlias("resourceXrefs.memberEntity", "resource").add(
						Restrictions.in("resource.id", roleSearchBean.getResourceIdSet()));
			}
            
            if(CollectionUtils.isNotEmpty(roleSearchBean.getChildIdSet())) {
            	criteria.createAlias("childRoles", "childXrefs")
						.createAlias("childXrefs.memberEntity", "child").add(
						Restrictions.in("child.id", roleSearchBean.getChildIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(roleSearchBean.getParentIdSet())) {
				criteria.createAlias("parentRoles", "parentXrefs")
						.createAlias("parentXrefs.entity", "parent").add(
						Restrictions.in("parent.id", roleSearchBean.getParentIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(roleSearchBean.getOrganizationIdSet())){    
                criteria.createAlias("organizations", "organizationXrefs")
						.createAlias("organizationXrefs.entity", "organization").add(
						Restrictions.in("organization.id", roleSearchBean.getOrganizationIdSet()));
            }

			if(CollectionUtils.isNotEmpty(roleSearchBean.getUserIdSet())){
            	criteria.createAlias("users", "userXrefs")
						.createAlias("userXrefs.memberEntity", "user").add(
								Restrictions.in("user.id", roleSearchBean.getUserIdSet()));
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

    @Override
    public List<TreeObjectId> findRolesWithSubRolesIds(List<String> initialRoleIds, final Set<String> filter) {
        List<TreeObjectId> result = new LinkedList<TreeObjectId>();
        if(initialRoleIds != null) {
            for (String roleId : initialRoleIds) {
                if(!rolesHierarchyIds.containsKey(roleId)) {
                    rolesHierarchyIds.putIfAbsent(roleId, populateTreeObjectId(new TreeObjectId(roleId), filter, new HashSet<String>()));
                }
                result.add(rolesHierarchyIds.get(roleId));
            }
        }
        return result;
    }

    @Override
    public void rolesHierarchyRebuild() {
        rolesHierarchyIds.clear();
        List<String> allParentIds = findAllParentsIds();
        if(allParentIds != null) {
            for(String parentRoleId : allParentIds) {
                TreeObjectId treeObjectId = populateTreeObjectId(new TreeObjectId(parentRoleId), null, new HashSet<String>());
                rolesHierarchyIds.putIfAbsent(parentRoleId, treeObjectId);
            }
        }
    }
    
    private Criteria getChildRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("parentRoles", "parentXrefs")
					 						   .createAlias("parentXrefs.entity", "parent").add(
					 						    Restrictions.eq("parent.id", roleId));
        
        if(filter!=null && !filter.isEmpty()){
            criteria.add( Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private TreeObjectId populateTreeObjectId(final TreeObjectId root, final Set<String> filter, final Set<String> visitedSet){
        List<String> ids = (List<String>)getChildRolesCriteria(root.getValue(), filter).setProjection(Projections.id()).list();
        if(ids != null) {
            for(final String id : ids) {
            	if(!visitedSet.contains(id)) {
            		visitedSet.add(id);
            		final TreeObjectId objectId = new TreeObjectId(id);
            		root.addChild(populateTreeObjectId(objectId, filter, visitedSet));
            	}
            }
        }
        return root;
    }

    @Override
    public List<String> findAllParentsIds() {
        return getCriteria().add(Restrictions.isEmpty("parentRoles")).setProjection(Projections.id()).list();
    }
    
}
