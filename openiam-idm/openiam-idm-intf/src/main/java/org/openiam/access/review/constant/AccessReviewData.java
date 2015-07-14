package org.openiam.access.review.constant;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.base.TreeNode;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.ResourceType;

import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 5/27/14.
 */
public class AccessReviewData {
    private int maxHierarchyLevel;
    private String defaultManagedSysId;

    private final  String roleClass = AuthorizationRole.class.getSimpleName();
    private final  String groupClass = AuthorizationGroup.class.getSimpleName();
    private final  String resourceClass = AuthorizationResource.class.getSimpleName();

    private String view=AccessReviewConstant.RESOURCE_VIEW;
    private UserEntitlementsMatrix matrix;
    private Set<String> targetResourceIds;
    private Map<String, ManagedSysEntity> mngsysMap;
    private Map<String, ResourceType>  resourceTypeMap;
    private List<LoginEntity> loginList;
    private AccessViewFilterBean filter;
    private List<TreeNode<AccessViewBean>> filteredNodes=new ArrayList<TreeNode<AccessViewBean>>();
    private Set<AccessViewBean> addedElements = new HashSet<AccessViewBean>();

    private Map<String, TaskWrapper> resourceWorkflowMap;
    private Map<String, TaskWrapper> roleWorkflowMap;
    private Map<String, TaskWrapper> groupWorkflowMap;

    public UserEntitlementsMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(UserEntitlementsMatrix matrix) {
        this.matrix = matrix;
    }

    public Map<String, ManagedSysEntity> getMngsysMap() {
        return mngsysMap;
    }

    public void setMngsysMap(Map<String, ManagedSysEntity> mngsysMap) {
        this.mngsysMap = mngsysMap;
    }

    public Map<String, ResourceType> getResourceTypeMap() {
        return resourceTypeMap;
    }

    public void setResourceTypeMap(Map<String, ResourceType> resourceTypeMap) {
        this.resourceTypeMap = resourceTypeMap;
    }

    public List<LoginEntity> getLoginList() {
        return loginList;
    }

    public void setLoginList(List<LoginEntity> loginList) {
        this.loginList = loginList;
    }

    public void putDataAsUsedElement(AccessViewBean bean){
        addedElements.add(bean);
    }
    public void putDataAsUsedElement(List<AccessViewBean> beans){
        addedElements.addAll(beans);
    }

    public AccessViewFilterBean getFilter() {
        return filter;
    }

    public void setFilter(AccessViewFilterBean filter) {
        this.filter = filter;
        this.filter.computeCompiledFlag();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public List<TreeNode<AccessViewBean>> getFilteredNodes() {
        return filteredNodes;
    }

    public void setFilteredNodes(List<TreeNode<AccessViewBean>> filteredNodes) {
        this.filteredNodes = filteredNodes;
    }

    public boolean isElementInUse(AccessViewBean bean){
        return addedElements!=null && addedElements.contains(bean);
    }

    public void setTargetResourceIds(Set<String> targetResourceIds) {
        this.targetResourceIds = targetResourceIds;
    }



    public void addTargetResourceIds(Set<String> targetResourcesIds){
        if(this.targetResourceIds==null){
            this.targetResourceIds=new HashSet<String>();
        }
        if(targetResourcesIds!=null)
            this.targetResourceIds.addAll(targetResourcesIds);
    }

    public boolean isInTargetResource(String resourceId){
        if(this.targetResourceIds==null)
            return true; // show all data
        return this.targetResourceIds.contains(resourceId);
    }

    public boolean isHasParent(AbstractAuthorizationEntity bean){
        if(bean!=null){
            Map<String, Set<String>> child2ParentMap = getChild2ParentMap(bean.getClass().getSimpleName());
            if(child2ParentMap!=null){
                return CollectionUtils.isNotEmpty(child2ParentMap.get(bean.getId()));
            }
        }
        return false;
    }

    public boolean isRoot(AbstractAuthorizationEntity bean){
        return !isHasParent(bean);
    }

    private Map<String, Set<String>> getChild2ParentMap(String className){
    	/*
        if(this.roleClass.equals(className)){
            return this.matrix.getChildRoleToParentRoleMap();
        } else if(this.groupClass.equals(className)){
            return this.matrix.getChildGroupToParentGroupMap();
        } else if(this.resourceClass.equals(className)){
            return this.matrix.getChildResToParentResMap();
        }else{
            return null;
        }
        */ return null;
    }

    public boolean isFilterSet(){
        return this.filter!=null
               && (StringUtils.isNotBlank(this.filter.getName())
                   || StringUtils.isNotBlank(this.filter.getDescription())
                   || StringUtils.isNotBlank(this.filter.getRisk())
                   || (this.filter.getShowExceptionsFlag()!=null && this.filter.getShowExceptionsFlag()));
    }
    public boolean isFilterNotSet(){
        return !this.isFilterSet();
    }

    public boolean showRoles(){
        return this.showAll()
               || (this.filter.getCompiledFlag() & AccessReviewConstant.SHOW_ROLE_ONLY) == AccessReviewConstant.SHOW_ROLE_ONLY;
    }
    public boolean showGroups(){
        return this.showAll()
               || (this.filter.getCompiledFlag() & AccessReviewConstant.SHOW_GROUP_ONLY) == AccessReviewConstant.SHOW_GROUP_ONLY;
    }
    public boolean showManagedSys(){
        return !this.showAll()
               && (this.filter.getCompiledFlag() & AccessReviewConstant.SHOW_MNGSYS_ONLY) == AccessReviewConstant.SHOW_MNGSYS_ONLY;
    }

    public boolean showAll(){
        return this.filter==null || this.filter.getCompiledFlag()==AccessReviewConstant.SHOW_ALL;
    }

    public int getMaxHierarchyLevel() {
        return maxHierarchyLevel;
    }

    public void setMaxHierarchyLevel(int maxHierarchyLevel) {
        this.maxHierarchyLevel = maxHierarchyLevel;
    }

    public String getDefaultManagedSysId() {
        return defaultManagedSysId;
    }

    public void setDefaultManagedSysId(String defaultManagedSysId) {
        this.defaultManagedSysId = defaultManagedSysId;
    }

    public Map<String, TaskWrapper> getResourceWorkflowMap() {
        return resourceWorkflowMap;
    }

    public Map<String, TaskWrapper> getRoleWorkflowMap() {
        return roleWorkflowMap;
    }

    public Map<String, TaskWrapper> getGroupWorkflowMap() {
        return groupWorkflowMap;
    }

    public void setWorkflowsMaps(List<TaskWrapper> userTasks){
        if(CollectionUtils.isNotEmpty(userTasks)){
            for(TaskWrapper task: userTasks){
                AssociationType taskAssociationType = AssociationType.getByValue(task.getAssociationType());
                switch (taskAssociationType){
                    case RESOURCE:
                        addResourceTask(task);
                        break;
                    case GROUP:
                        addGroupTask(task);
                        break;
                    case ROLE:
                        addRoleTask(task);
                        break;
                }
            }
        }
    }

    private void addResourceTask(TaskWrapper task) {
        if(resourceWorkflowMap==null){
            resourceWorkflowMap = new HashMap<>();
        }
        addTask(resourceWorkflowMap, task);
    }
    private void addGroupTask(TaskWrapper task) {
        if(groupWorkflowMap==null){
            groupWorkflowMap = new HashMap<>();
        }
        addTask(groupWorkflowMap, task);
    }
    private void addRoleTask(TaskWrapper task) {
        if(roleWorkflowMap==null){
            roleWorkflowMap = new HashMap<>();
        }
        addTask(roleWorkflowMap, task);
    }

    private void addTask(Map<String, TaskWrapper> workflowMap, TaskWrapper task) {
        workflowMap.put(task.getAssociationId(), task);
    }
}
