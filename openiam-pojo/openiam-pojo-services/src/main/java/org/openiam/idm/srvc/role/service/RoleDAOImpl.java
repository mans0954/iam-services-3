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
import org.hibernate.criterion.*;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeObjectId;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.base.TreeObjectId;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.openiam.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.hibernate.criterion.Projections.rowCount;

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
    protected boolean cachable() {
        return true;
    }

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

    @Override
    protected Criteria getExampleCriteria(final RoleEntity entity) {
        final Criteria criteria = super.getCriteria();
        if (entity != null) {
            if (StringUtils.isNotBlank(entity.getId())) {
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

                if (entity.getManagedSystem() != null && StringUtils.isNotBlank(entity.getManagedSystem().getId())) {
                    criteria.add(Restrictions.eq("managedSystem.id", entity.getManagedSystem().getId()));
                }

                if (StringUtils.isNotEmpty(entity.getDescription())) {
                    String description = entity.getDescription();
                    MatchMode descMatchMode = null;
                    if (StringUtils.indexOf(description, "*") == 0) {
                        descMatchMode = MatchMode.END;
                        description = description.substring(1);
                    }
                    if (StringUtils.isNotBlank(description) && StringUtils.indexOf(description, "*") == description.length() - 1) {
                        description = description.substring(0, description.length() - 1);
                        descMatchMode = (descMatchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                    }

                    if (StringUtils.isNotBlank(description)) {
                        if (descMatchMode != null) {
                            criteria.add(Restrictions.ilike("description", description, descMatchMode));
                        } else {
                            criteria.add(Restrictions.eq("description", description));
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(entity.getResources())) {
                    final Set<String> resourceIds = new HashSet<String>();
                    for (final RoleToResourceMembershipXrefEntity resourceRole : entity.getResources()) {
                        if (resourceRole != null && StringUtils.isNotBlank(resourceRole.getMemberEntity().getId())) {
                            resourceIds.add(resourceRole.getMemberEntity().getId());
                        }
                    }

                    if (CollectionUtils.isNotEmpty(resourceIds)) {
                        criteria.createAlias("resources", "rr").add(Restrictions.in("rr.id", resourceIds));
                    }
                }
            }
        }
        criteria.setCacheable(this.cachable());
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
        criteria.setCacheable(this.cachable());
    }

    public List<RoleEntity> getByExample(final SearchBean searchBean) {
        return getByExample(searchBean, -1, -1);
    }

    public List<RoleEntity> getByExample(final SearchBean searchBean, int from, int size) {
        final Criteria criteria = getExampleCriteria(searchBean);
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        if (searchBean instanceof AbstractSearchBean) {
            AbstractSearchBean sb = (AbstractSearchBean) searchBean;
//            if (StringUtils.isNotBlank(sb.getSortBy())) {
//                criteria.addOrder(sb.getOrderBy().equals(OrderConstants.DESC) ?
//                        Order.desc(sb.getSortBy()) :
//                        Order.asc(sb.getSortBy()));
//            }

            if (CollectionUtils.isNotEmpty(sb.getSortBy())) {
                this.setOderByCriteria(criteria, sb);
            }
        }
        return (List<RoleEntity>) criteria.setCacheable(this.cachable()).list();
    }

/*    @Override
    @Deprecated
    public List<RoleEntity> getByExample(RoleEntity t, int startAt, int size) {
        final Criteria criteria = getExampleCriteria(t);
        if (startAt > -1) {
            criteria.setFirstResult(startAt);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return (List<RoleEntity>) criteria.setCacheable(this.cachable()).list();
    }*/

    public List<RoleEntity> findAll() {
        return (List<RoleEntity>) getCriteria().setCacheable(this.cachable()).list();
    }

    @Override
    public RoleEntity findRoleByName(String roleName) {
        return (RoleEntity) getCriteria().add(Restrictions.eq("name", roleName)).setCacheable(this.cachable()).uniqueResult();
    }

    @Override
    public List<RoleEntity> getRolesForGroup(final String groupId, final Set<String> filter, final int from, final int size) {
        final Criteria criteria = getEntitlementRolesCriteria(null, groupId, null, filter);
        return getList(criteria, from, size);
    }

    @Override
    public int getNumOfRolesForGroup(String groupId, final Set<String> filter) {
        final Criteria criteria = getEntitlementRolesCriteria(null, groupId, null, filter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public int getNumOfRolesForResource(final String resourceId, final Set<String> filter) {
        final Criteria criteria = getEntitlementRolesCriteria(null, null, resourceId, filter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
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
        return ((Number) criteria.uniqueResult()).intValue();
    }


    private Criteria getEntitlementRolesCriteria(String userId, String groupId, String resourceId, final Set<String> filter) {
        final Criteria criteria = super.getCriteria();

        if (StringUtils.isNotBlank(userId)) {
            criteria.createAlias("users", "u")
                    .add(Restrictions.eq("u.id", userId));
        }

        if (StringUtils.isNotBlank(groupId)) {
            criteria.createAlias("groups", "groups").add(Restrictions.eq("groups.id", groupId));
        }

        if (StringUtils.isNotBlank(resourceId)) {
            criteria.createAlias("resources", "resources").add(Restrictions.eq("resources.id", resourceId));
        }

        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        criteria.setCacheable(this.cachable());
        return criteria;
    }

    @Override
    public List<RoleEntity> getChildRoles(final String roleId, final Set<String> filter, int from, int size) {
        final Criteria criteria = getChildRolesCriteria(roleId, filter);
        return getList(criteria, from, size);
    }

    @Override
    public List<RoleEntity> getParentRoles(final String roleId, final Set<String> filter, int from, int size) {
        final Criteria criteria = getParentRolesCriteria(roleId, filter);
        return getList(criteria, from, size);
    }

    @Override
    public int getNumOfChildRoles(final String roleId, final Set<String> filter) {
        final Criteria criteria = getChildRolesCriteria(roleId, filter);
        criteria.setProjection(rowCount());
        return ((Number) criteria.setCacheable(this.cachable()).uniqueResult()).intValue();
    }

    @Override
    public int getNumOfParentRoles(final String roleId, final Set<String> filter) {
        final Criteria criteria = getParentRolesCriteria(roleId, filter);
        criteria.setProjection(rowCount());
        return ((Number) criteria.setCacheable(this.cachable()).uniqueResult()).intValue();
    }

    private Criteria getParentRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("childRoles", "role").add(Restrictions.eq("role.id", roleId));
        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        criteria.setCacheable(this.cachable());
        return criteria;
    }

/*    private Criteria getChildRolesCriteria(final String roleId, final Set<String> filter) {
        final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add(Restrictions.eq("role.id", roleId));
        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        criteria.setCacheable(this.cachable());
        return criteria;
    }*/

    private Criteria getRolesForUserCriteria(final String userId, final Set<String> filter) {
        return getCriteria().setCacheable(this.cachable())
                .createAlias("users", "u")
                .add(Restrictions.eq("u.id", userId));
    }

    private List<RoleEntity> getList(Criteria criteria, int from, int size) {
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        criteria.setCacheable(this.cachable());
        return criteria.list();
    }

  /*  public List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue) {
        List ret = new ArrayList<RoleEntity>();
        if (StringUtils.isNotBlank(attrName)) {
            // Can't use Criteria for @ElementCollection due to Hibernate bug
            // (org.hibernate.MappingException: collection was not an association)
            HibernateTemplate template = getHibernateTemplate();
            template.setCacheQueries(true);
            ret = template.find("select ra.role from RoleAttributeEntity ra left join ra.values av where ra.name = ? and ((ra.isMultivalued = false and ra.value = ?) or (ra.isMultivalued = true and av in ?))", attrName, attrValue, attrValue);
        }
        return ret;
    }*/

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
        return getCriteria().setCacheable(this.cachable()).add(Restrictions.isEmpty("parentRoles")).setProjection(Projections.id()).list();
    }
    
}
