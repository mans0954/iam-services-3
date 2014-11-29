package org.openiam.access.review.strategy.entitlements;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.access.review.constant.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 5/29/14.
 */
public abstract class EntitlementsStrategy {
    protected AccessReviewData accessReviewData;

    private Map<String, Set<String>> role2ResCache = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> group2ResCache = new HashMap<String, Set<String>>();
    private Set<String> userEntitlementCache = null;
    private Set<String> userExceptionsCache = null;

    public EntitlementsStrategy(AccessReviewData accessReviewData){
        this.accessReviewData=accessReviewData;
    }

    protected void setIdentityForResource(AccessViewBean bean, ManagedSysEntity mngsys, List<LoginEntity> loginList){
        if(CollectionUtils.isNotEmpty(loginList) && mngsys!=null) {
            for(final LoginEntity login : loginList) {
                if(mngsys.getId().equals(login.getManagedSysId())){
                    bean.setIdentity(login.getLogin());
                    bean.setLoginId(login.getLoginId());
                    break;
                }
            }
        }
    }

    protected Set<AccessViewBean> getRoleBeans(Set<String> roleIds){
        Set<AccessViewBean> retVal=new HashSet<>();
        if(CollectionUtils.isNotEmpty(roleIds)){
            for(String roleId : roleIds){
                AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(roleId);
                if(role!=null){
                    AccessViewBean bean = this.createBean(role);
                    retVal.add(bean);
                }
            }
        }
        return retVal;
    }


    protected Set<AccessViewBean> getGroupBeans(Set<String> groupIds){
        Set<AccessViewBean> retVal=new HashSet<AccessViewBean>();
        if(CollectionUtils.isNotEmpty(groupIds)){
            for(String groupId : groupIds){
                AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(groupId);
                if(group!=null){
                    AccessViewBean bean = this.createBean(group);
                    retVal.add(bean);
                }
            }
        }
        return retVal;
    }

    protected Set<AccessViewBean> getResourceBeans(Set<String> resourceIds){
        Set<AccessViewBean> retVal=new HashSet<AccessViewBean>();
        if(CollectionUtils.isNotEmpty(resourceIds)){
            for(String resourceId : resourceIds){
                AuthorizationResource resource = accessReviewData.getMatrix().getResourceMap().get(resourceId);
                if(resource!=null){
                    AccessViewBean bean = this.createBean(resource);

                    if("MANAGED_SYS".equals(resource.getResourceTypeId())){
                        bean.setManagedSys(accessReviewData.getMngsysMap().get(resource.getId()).getId());
                        setIdentityForResource(bean, accessReviewData.getMngsysMap().get(resource.getId()),
                                               accessReviewData.getLoginList());
                    }
                    retVal.add(bean);
                }

            }
        }
        return retVal;
    }

    public Set<String> getUserExceptionsCache(){
        if(this.userExceptionsCache==null){
            this.userExceptionsCache = new HashSet<>(getUserEntitlements());
            Set<String> groupsEntitlements = getCompiledResourcesForGroups();
            Set<String> rolesEntitlements = getCompiledResourcesForRoles();
            this.userExceptionsCache.removeAll(groupsEntitlements);
            this.userExceptionsCache.removeAll(rolesEntitlements);
        }
        return this.userExceptionsCache;
    }

    public Set<String> getUserEntitlements(){
        if(CollectionUtils.isEmpty(userEntitlementCache)){
            userEntitlementCache = new HashSet<String>();

            /* compile groups */
            final Set<String> compiledGroups = getCompiledGroups();

            /* compile roles */
            final Set<String> compiledRoles = getCompiledRoles(compiledGroups);

            /* get all resources for the compiled roles */
            final Set<String> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, accessReviewData.getMatrix().getRoleToResourceMap());

            /* get all resources for the compiled groups */
            final Set<String> resourcesForCompiledGroups = getResourcesForGroups(compiledGroups, accessReviewData.getMatrix().getGroupToResourceMap());

            /* compiles resources for groups and roles */
            final Set<String> compiledResources = new HashSet<String>();
            userEntitlementCache.addAll(getCompiledResources(resourcesForCompiledRoles, accessReviewData.getMatrix().getChildResToParentResMap()));
            userEntitlementCache.addAll(getCompiledResources(resourcesForCompiledGroups, accessReviewData.getMatrix().getChildResToParentResMap()));
            userEntitlementCache.addAll(resourcesForCompiledGroups);
            userEntitlementCache.addAll(resourcesForCompiledRoles);

            /* set the direct resources, and add any indirect resources to the compiled set */
            if(CollectionUtils.isNotEmpty(accessReviewData.getMatrix().getResourceIds())) {
                final Set<String> directResources = accessReviewData.getMatrix().getResourceIds();
                userEntitlementCache.addAll(directResources);
                userEntitlementCache.addAll(getCompiledResources(directResources, accessReviewData.getMatrix().getChildResToParentResMap()));
            }
        }
        return userEntitlementCache;
    }

    protected Set<String> getCompiledResourcesForGroups(){
        Set<String> retVal = new HashSet<>();
        Set<String> groups = this.getCompiledGroups();
        if(CollectionUtils.isNotEmpty(groups)){
            for(String groupId : groups){
                retVal.addAll(getCompiledResourcesForGroup(groupId));
            }
        }
        return retVal;
    }

    protected Set<String> getCompiledResourcesForRoles(){
        Set<String> retVal = new HashSet<>();
        Set<String> roles = this.getCompiledRoles();
        if(CollectionUtils.isNotEmpty(roles)){
            for(String roleId : roles){
                retVal.addAll(getCompiledResourcesForRole(roleId));
            }
        }
        return retVal;
    }

    protected Set<String> getCompiledResourcesForGroup(String groupId){
        if(!group2ResCache.containsKey(groupId)){
            Set<String> retVal = new HashSet<String>();
            /* compile roles */
            final Set<String> compiledRoles = new HashSet<String>();
            if(this.accessReviewData.getMatrix().getGroupToRoleMap().containsKey(groupId)) {
                final Set<String> visitedSet = new HashSet<String>();
                for(final String roleId : this.accessReviewData.getMatrix().getGroupToRoleMap().get(groupId)) {
                    compiledRoles.add(roleId);
                    compiledRoles.addAll(compileRoles(roleId, this.accessReviewData.getMatrix().getChildRoleToParentRoleMap(), visitedSet));
                }
            }
            /* compile the groups for this group */
            final Set<String> compiledGroups = compileGroups(groupId, this.accessReviewData.getMatrix().getChildGroupToParentGroupMap(), new HashSet<String>());
            /* compiles the roles for compiled groups */
            compiledRoles.addAll(getRolesForGroups(compiledGroups, this.accessReviewData.getMatrix().getGroupToRoleMap()));
            /* get all resources for the compiled roles */
            final Set<String> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, this.accessReviewData.getMatrix().getRoleToResourceMap());
            /* get all resources for the compiled groups */
            final Set<String> resourcesForCompiledGroups = getResourcesForGroups(compiledGroups, this.accessReviewData.getMatrix().getGroupToResourceMap());

            /* compiles resources for groups and roles */
            retVal.addAll(getCompiledResources(resourcesForCompiledRoles, this.accessReviewData.getMatrix().getChildResToParentResMap()));
            retVal.addAll(getCompiledResources(resourcesForCompiledGroups, this.accessReviewData.getMatrix().getChildResToParentResMap()));
            retVal.addAll(resourcesForCompiledGroups);
            retVal.addAll(resourcesForCompiledRoles);

            /* set the direct resources, and add any indirect resources to the compiled set */
            if(this.accessReviewData.getMatrix().getGroupToResourceMap().containsKey(groupId)) {
                final Set<String> directResources = this.accessReviewData.getMatrix().getGroupToResourceMap().get(groupId);
                retVal.addAll(directResources);
                retVal.addAll(getCompiledResources(directResources, this.accessReviewData.getMatrix().getChildResToParentResMap()));
            }
            group2ResCache.put(groupId, retVal);
        }
        return group2ResCache.get(groupId);
    }

    protected Set<String> getCompiledResourcesForRole(String roleId){
        if(!role2ResCache.containsKey(roleId)){
            Set<String> retVal = new HashSet<String>();
            /* compile the roles for this role */
            final Set<String> compiledRoles = compileRoles(roleId, this.accessReviewData.getMatrix().getChildRoleToParentRoleMap(), new HashSet<String>());
            /* get all resources for the compiled roles */
            final Set<String> resourcesForCompiledRoles = getResourcesForRoles(compiledRoles, this.accessReviewData.getMatrix().getRoleToResourceMap());

            /*get all child resources for the already-compiled resoruces */
            final Set<String> compiledResources = getCompiledResources(resourcesForCompiledRoles, this.accessReviewData.getMatrix().getChildResToParentResMap());
            retVal.addAll(compiledResources);

            /* set the direct resources, and add any indirect resources to the compiled set */
            if(this.accessReviewData.getMatrix().getRoleToResourceMap().containsKey(roleId)) {
                final Set<String> directResources = this.accessReviewData.getMatrix().getRoleToResourceMap().get(roleId);
                retVal.addAll(directResources);
                final Set<String> compilesResourcesFromResources = getCompiledResources(directResources, this.accessReviewData.getMatrix().getChildResToParentResMap());
                retVal.addAll(compilesResourcesFromResources);
            }
            role2ResCache.put(roleId, retVal);
        }
        return role2ResCache.get(roleId);
    }

    protected Set<String> getCompiledGroups(){
        final Set<String> compiledGroups = new HashSet<String>();
        final Set<String> visitedGroupSet = new HashSet<String>();

        if(CollectionUtils.isNotEmpty(accessReviewData.getMatrix().getGroupIds())){
            for(final String groupId : accessReviewData.getMatrix().getGroupIds()) {
                final Set<String> tempCompiledGroups = compileGroups(groupId,  accessReviewData.getMatrix().getChildGroupToParentGroupMap(), visitedGroupSet);
                if(CollectionUtils.isNotEmpty(tempCompiledGroups)) {
                    compiledGroups.addAll(tempCompiledGroups);
                }
            }
            compiledGroups.addAll(accessReviewData.getMatrix().getGroupIds());
        }
        return compiledGroups;
    }

    protected Set<String> getCompiledGroupsForResource(String resourceId){
        final Set<String> compiledGroups = new HashSet<String>();
        final Set<String> visitedGroupSet = new HashSet<String>();
        if(accessReviewData.getMatrix().getResourceToGroupMap().containsKey(resourceId)){
            for(final String groupId : accessReviewData.getMatrix().getResourceToGroupMap().get(resourceId)) {
                final Set<String> tempCompiledGroups = compileGroups(groupId,  accessReviewData.getMatrix().getChildGroupToParentGroupMap(), visitedGroupSet);
                if(CollectionUtils.isNotEmpty(tempCompiledGroups)) {
                    compiledGroups.addAll(tempCompiledGroups);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(accessReviewData.getMatrix().getGroupIds()))
            compiledGroups.addAll(accessReviewData.getMatrix().getGroupIds());
        return compiledGroups;
    }

    protected Set<String> getCompiledRoles(){
        return getCompiledRoles(null);
    }
    protected Set<String> getCompiledRoles(Set<String> compiledGroups){
        if(CollectionUtils.isEmpty(compiledGroups)){
            compiledGroups = getCompiledGroups();
        }
        final Set<String> compiledRoles = new HashSet<String>();

        final Set<String> visitedRoleSet = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(accessReviewData.getMatrix().getRoleIds())){
            for(final String roleId : accessReviewData.getMatrix().getRoleIds()) {
                final Set<String> tempCompiledRoles = compileRoles(roleId, accessReviewData.getMatrix().getChildRoleToParentRoleMap(), visitedRoleSet);
                if(CollectionUtils.isNotEmpty(tempCompiledRoles)) {
                    compiledRoles.addAll(tempCompiledRoles);
                }
            }

            compiledRoles.addAll(accessReviewData.getMatrix().getRoleIds());
        }
        final Set<String> visitedSet = new HashSet<String>();
        for(final String groupId : compiledGroups) {
            if(accessReviewData.getMatrix().getGroupToRoleMap().containsKey(groupId)) {
                for(final String roleId : accessReviewData.getMatrix().getGroupToRoleMap().get(groupId)) {
                    compiledRoles.add(roleId);
                    compiledRoles.addAll(compileRoles(roleId, accessReviewData.getMatrix().getChildRoleToParentRoleMap(), visitedSet));
                }
            }
        }
            /* compiles the roles for compiled groups */
        compiledRoles.addAll(getRolesForGroups(compiledGroups, accessReviewData.getMatrix().getGroupToRoleMap()));

        return compiledRoles;
    }

    protected Set<String> getCompiledRolesForResource(String resourceId){
         final Set<String> compiledRoles = new HashSet<String>();
         final Set<String> visitedRoleSet = new HashSet<String>();
         if(accessReviewData.getMatrix().getResourceToRoleMap().containsKey(resourceId)){
             for(final String roleId : accessReviewData.getMatrix().getResourceToRoleMap().get(resourceId)) {
                 final Set<String> tempCompiledRoles = compileRoles(roleId, accessReviewData.getMatrix().getChildRoleToParentRoleMap(), visitedRoleSet);
                 if(CollectionUtils.isNotEmpty(tempCompiledRoles)) {
                     compiledRoles.addAll(tempCompiledRoles);
                 }
             }
         }
        return compiledRoles;
    }

    private Set<String> compileGroups(String groupId, Map<String, Set<String>> groupToGroupMap,
                                      Set<String> visitedSet) {
        final Set<String> retval = new HashSet<String>();
        if(groupId != null && groupToGroupMap != null && visitedSet != null) {
            if(!visitedSet.contains(groupId)) {
                visitedSet.add(groupId);
                if(groupToGroupMap.containsKey(groupId)) {
                    for(final String child : groupToGroupMap.get(groupId)) {
                        retval.add(child);
                        retval.addAll(compileGroups(child, groupToGroupMap, visitedSet));
                    }
                }
            }
        }
        return retval;
    }
    private Set<String> getRolesForGroups(final Set<String> compiledGroups,
                                          final Map<String, Set<String>> group2RoleMap) {
        final Set<String> retval = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(compiledGroups)) {
            for(final String groupId : compiledGroups) {
                if(group2RoleMap.containsKey(groupId)) {
                    retval.addAll(group2RoleMap.get(groupId));
                }
            }
        }
        return retval;
    }
    private Set<String> getResourcesForGroups(final Set<String> compiledGroups,
                                              final Map<String, Set<String>> group2ResourceMap) {
        final Set<String> retval = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(compiledGroups)) {
            for(final String groupId : compiledGroups) {
                if(group2ResourceMap.containsKey(groupId)) {
                    retval.addAll(group2ResourceMap.get(groupId));
                }
            }
        }
        return retval;
    }

    private Set<String> getCompiledResources(final Set<String> resourceSet,
                                             final Map<String, Set<String>> resource2ResourceMap) {
        final Set<String> retval = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(resourceSet)) {
            final Set<String> visitedSet = new HashSet<String>();
            for(final String resourceId : resourceSet) {
                retval.addAll(getCompiledResources(resourceId, resource2ResourceMap, visitedSet));
            }
        }
        return retval;
    }
    private Set<String> getCompiledResources(final String resourceId,
                                             final Map<String, Set<String>> resource2ResourceMap,
                                             final Set<String> visitedSet) {
        final Set<String> retval = new HashSet<String>();
        if(resourceId != null && resource2ResourceMap != null && visitedSet != null) {
            if(!visitedSet.contains(resourceId)) {
                visitedSet.add(resourceId);
                    if(resource2ResourceMap.containsKey(resourceId)) {
                        for(final String child : resource2ResourceMap.get(resourceId)) {
                            retval.add(child);
                            retval.addAll(getCompiledResources(child, resource2ResourceMap, visitedSet));
                        }
                    }
                }
        }
        return retval;
    }

    private Set<String> compileRoles(String roleId, Map<String, Set<String>> roleToRoleMap,
                                     Set<String> visitedSet) {
        final Set<String> retval = new HashSet<String>();
        if(roleId != null && roleToRoleMap != null && visitedSet != null) {
            if(!visitedSet.contains(roleId)) {
                visitedSet.add(roleId);
                if(roleToRoleMap.containsKey(roleId)) {
                    for(final String child : roleToRoleMap.get(roleId)) {
                        retval.add(child);
                        retval.addAll(compileRoles(child, roleToRoleMap, visitedSet));
                    }
                }
            }
        }
        return retval;
    }
    private Set<String> getResourcesForRoles(final Set<String> compiledRoles,
                                             final Map<String, Set<String>> role2ResourceMap) {
        final Set<String> retval = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(compiledRoles)) {
            for(final String roleId : compiledRoles) {
                if(role2ResourceMap.containsKey(roleId)) {
                    retval.addAll(role2ResourceMap.get(roleId));
                }
            }
        }
        return retval;
    }

    public static AccessViewBean createBean(AuthorizationRole entity){
        return createBean(entity.getId(), entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                          AccessReviewConstant.ROLE_TYPE, null);
    }

    public static  AccessViewBean createBean(AuthorizationGroup entity){
        return createBean(entity.getId(), entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                          AccessReviewConstant.GROUP_TYPE, null);
    }

    public static  AccessViewBean createBean(AuthorizationResource entity){
        return createBean(entity.getId(), (StringUtils.isNotBlank(entity.getCoorelatedName())) ? entity.getCoorelatedName() : entity.getName(), entity.getDescription(), entity.getStatus(), entity.getManagedSysId(),
                          AccessReviewConstant.RESOURCE_TYPE, entity.getRisk());
    }

    public static  AccessViewBean createBean(String id, String name, String description, String status, String managedSys, String type, String risk){
        AccessViewBean bean =  new AccessViewBean(id, name, description);
        bean.setStatus(status);
        bean.setManagedSys(managedSys);
        bean.setBeanType(type);
        bean.setRisk(risk);
        return bean;
    }

    public abstract boolean isDirectEntitled(AbstractAuthorizationEntity entity);


    public abstract Set<AccessViewBean> getRoles(AccessViewBean parent);
    public abstract Set<AccessViewBean> getGroups(AccessViewBean parent);
    public abstract Set<AccessViewBean> getResources(AccessViewBean parent);
}
