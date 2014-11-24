package org.openiam.access.review.strategy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.authmanager.common.model.AbstractAuthorizationEntity;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.access.review.strategy.entitlements.EntitlementsStrategy;
import org.openiam.access.review.strategy.entitlements.GroupEntitlementStrategy;
import org.openiam.access.review.strategy.entitlements.ResourceEntitlementStrategy;
import org.openiam.access.review.strategy.entitlements.RoleEntitlementStrategy;
import org.openiam.access.review.constant.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.base.TreeNode;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 5/27/14.
 */
public abstract class AccessReviewStrategy {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected AccessReviewData accessReviewData;

    private EntitlementsStrategy roleEntitlementStrategy=null;
    private EntitlementsStrategy groupEntitlementStrategy=null;
    private EntitlementsStrategy resourceEntitlementStrategy=null;


    protected AccessReviewStrategy(AccessReviewData accessReviewData){
        this.accessReviewData=accessReviewData;
    }

    public static AccessReviewStrategy getRoleViewStrategy(AccessReviewData accessReviewData){
        return new RoleViewStrategy(accessReviewData);
    }

    public static AccessReviewStrategy getResourceViewStrategy(AccessReviewData accessReviewData){
        return new ResourceViewStrategy(accessReviewData);
    }
    public static AccessReviewStrategy getGroupViewStrategy(AccessReviewData accessReviewData){
        return new GroupViewStrategy(accessReviewData);
    }

    public List<TreeNode<AccessViewBean>> buildView() {
        return buildView(null);
    }
    public abstract List<TreeNode<AccessViewBean>> buildView(AccessViewBean parent);

    public List<TreeNode<AccessViewBean>> getExceptionsList(){
        return Collections.EMPTY_LIST;
    }


    protected List<TreeNode<AccessViewBean>> getResourceBeanList(Set<AccessViewBean> resourcesList, boolean isOnlyRoot, boolean isParentException) {
        List<TreeNode<AccessViewBean>> result = new ArrayList<TreeNode<AccessViewBean>>();
        if(CollectionUtils.isNotEmpty(resourcesList)){
            for(AccessViewBean bean : resourcesList){
                AuthorizationResource resource = accessReviewData.getMatrix().getResourceMap().get(bean.getId());

                if(skipResource(bean)){
                    continue;
                }


                if(!accessReviewData.isElementInUse(bean)){
                    ResourceType resType = accessReviewData.getResourceTypeMap().get(resource.getResourceTypeId());
                    String icon=null;
                    String iconType=null;
                    String iconDescr = null;
                    if(resType!=null){
                        icon = resType.getUrl();
                        iconType= resType.getImageType();
                        iconDescr=resType.getDescription();
                    }


                    TreeNode<AccessViewBean> node = new TreeNode<AccessViewBean>(bean, icon, iconType);
                    node.setIconDescription(iconDescr);
                    node.setIsDeletable(getResourceEntitlementStrategy().isDirectEntitled(resource));
                    node.setIsException(isParentException || isException(bean.getId()));
                    node.setIsTerminate(isTerminating(bean.getBeanType(), bean.getId()));
                    result.add(node);
                }
            }
        }
        markAsUsedElements(result);
        return result;
    }

    private void markAsUsedElements(List<TreeNode<AccessViewBean>> treeNodes) {
        if(CollectionUtils.isNotEmpty(treeNodes)){
            for(TreeNode<AccessViewBean> node: treeNodes){
                accessReviewData.putDataAsUsedElement(node.getData());
            }
        }
    }

    private boolean skipResource(AccessViewBean bean) {
        AuthorizationResource thisResource = accessReviewData.getMatrix().getResourceMap().get(bean.getId());

        boolean result = false;
        if(accessReviewData.isHasParent(thisResource)){
            // get parent resource
            String resId = accessReviewData.getMatrix().getChildResToParentResMap().get(thisResource.getId()).iterator().next();
            AuthorizationResource parentResource = accessReviewData.getMatrix().getResourceMap().get(resId);
            AccessViewBean parentBean = EntitlementsStrategy.createBean(parentResource);

            result = !accessReviewData.isElementInUse(parentBean);
        }

        return result || checkResourceId(bean.getId());
    }
    private boolean skipGroup(AccessViewBean bean) {
        AuthorizationGroup thisGroup = accessReviewData.getMatrix().getGroupMap().get(bean.getId());

        boolean result = false;
        if(accessReviewData.isHasParent(thisGroup)){
            // get parent group
            String grId = accessReviewData.getMatrix().getChildGroupToParentGroupMap().get(thisGroup.getId()).iterator().next();
            AuthorizationGroup parent = accessReviewData.getMatrix().getGroupMap().get(grId);
            AccessViewBean parentBean = EntitlementsStrategy.createBean(parent);

            result = !accessReviewData.isElementInUse(parentBean);
        }

        return result;
    }

    protected  List<TreeNode<AccessViewBean>> getGroupBeanList(Set<AccessViewBean> groupList,
                                                             boolean isParentException, String parentMngSysId, int level) {
        List<TreeNode<AccessViewBean>> result = new ArrayList<TreeNode<AccessViewBean>>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (AccessViewBean bean : groupList) {
                if(skipGroup(bean))
                    continue;
                if(isParentException)
                    continue;

                AuthorizationGroup group = accessReviewData.getMatrix().getGroupMap().get(bean.getId());

                if(checkMngSys(parentMngSysId, group, level)){
                    if(!accessReviewData.isElementInUse(bean)){
                        TreeNode<AccessViewBean> node = new TreeNode<AccessViewBean>(bean, AccessReviewConstant.GROUP_TYPE);
                        node.setIconDescription(AccessReviewConstant.GROUP_ICON_DESCR);
                        node.setIsDeletable(getGroupEntitlementStrategy().isDirectEntitled(group));
                        node.setIsTerminate(isTerminating(bean.getBeanType(), bean.getId()));
                        result.add(node);
                    }
                }
            }
        }
        markAsUsedElements(result);
        return result;
    }

    protected List<TreeNode<AccessViewBean>> getRoleBeanList(Set<AccessViewBean> roleList,
                                                           boolean isParentException, String parentMngSysId, int level) {
        List<TreeNode<AccessViewBean>> result = new ArrayList<TreeNode<AccessViewBean>>();
        if(CollectionUtils.isNotEmpty(roleList)){
            for(AccessViewBean bean : roleList){
                if(isParentException)
                    continue;

                AuthorizationRole role = accessReviewData.getMatrix().getRoleMap().get(bean.getId());

                if(checkMngSys(parentMngSysId, role, level)){

                    if(!accessReviewData.isElementInUse(bean)){
                        TreeNode<AccessViewBean> node = new TreeNode<AccessViewBean>(bean, AccessReviewConstant.ROLE_TYPE);
                        node.setIconDescription(AccessReviewConstant.ROLE_ICON_DESCR);
                        node.setIsDeletable(getRoleEntitlementStrategy().isDirectEntitled(role));
                        node.setIsTerminate(isTerminating(bean.getBeanType(), bean.getId()));
                        result.add(node);
                    }
                }
            }
        }
        markAsUsedElements(result);
        return result;
    }

    protected List<TreeNode<AccessViewBean>> proceedSubTree(List<TreeNode<AccessViewBean>> beanList, int level){
        if(CollectionUtils.isNotEmpty(beanList)){
            for (TreeNode<AccessViewBean> node: beanList){
                appendToTargetResources(node);
                setChildrenList(node,  level + 1);
            }
        }
        return beanList;
    }

    private void setChildrenList(TreeNode<AccessViewBean> parentElement, int level) {
        List<TreeNode<AccessViewBean>> childrenList = new ArrayList<TreeNode<AccessViewBean>>();

        if(level <= accessReviewData.getMaxHierarchyLevel()){
            if(AccessReviewConstant.ROLE_TYPE.equals(parentElement.getData().getBeanType())){
                childrenList.addAll(getRoles(getRoleEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
                childrenList.addAll(getGroups(getRoleEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
                childrenList.addAll(getResources(getRoleEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
            } else if(AccessReviewConstant.GROUP_TYPE.equals(parentElement.getData().getBeanType())){
                childrenList.addAll(getGroups(getGroupEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
                childrenList.addAll(getRoles(getGroupEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
                childrenList.addAll(getResources(getGroupEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
            } else if(AccessReviewConstant.RESOURCE_TYPE.equals(parentElement.getData().getBeanType())){
                childrenList.addAll(getGroups(getResourceEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
                childrenList.addAll(getRoles(getResourceEntitlementStrategy(), parentElement.getData(), parentElement.getIsException(), level));
                childrenList.addAll(getResources(getResourceEntitlementStrategy(), parentElement.getData(),parentElement.getIsException(), level));
            }
        }
        parentElement.add(childrenList);
    }

    private List<TreeNode<AccessViewBean>> getRoles(EntitlementsStrategy strategy, AccessViewBean parent, boolean isParentException, int level){
        return proceedSubTree(getRoleBeanList(strategy.getRoles(parent), isParentException, parent.getManagedSys(), level), level);
    }
    private List<TreeNode<AccessViewBean>> getGroups(EntitlementsStrategy strategy, AccessViewBean parent, boolean isParentException,  int level){
        return proceedSubTree(getGroupBeanList(strategy.getGroups(parent), isParentException, parent.getManagedSys(), level), level);
    }
    private List<TreeNode<AccessViewBean>> getResources(EntitlementsStrategy strategy, AccessViewBean parent, boolean isParentException,  int level){
        return proceedSubTree(getResourceBeanList(strategy.getResources(parent), true, isParentException), level);

    }

    private void appendToTargetResources(TreeNode<AccessViewBean> node) {
        String parentId = node.getData().getId();
        if(AccessReviewConstant.ROLE_TYPE.equals(node.getData().getBeanType())){
            if(accessReviewData.getMatrix().getRoleToResourceMap().containsKey(parentId)){
                accessReviewData.addTargetResourceIds(toIdList(
                        this.getRoleEntitlementStrategy().getResources(node.getData())));
            }
        } else if(AccessReviewConstant.GROUP_TYPE.equals(node.getData().getBeanType())){
            if(accessReviewData.getMatrix().getGroupToResourceMap().containsKey(parentId)){
                accessReviewData.addTargetResourceIds(toIdList(
                        this.getGroupEntitlementStrategy().getResources(node.getData())));
            }
        } else if(AccessReviewConstant.RESOURCE_TYPE.equals(node.getData().getBeanType())){
            if("resources".equals(accessReviewData.getView())){
                // get all direct entitlements
                accessReviewData.addTargetResourceIds(this.getResourceEntitlementStrategy().getUserEntitlements());
            }
        }
    }

    private Set<String> toIdList(Collection<AccessViewBean> dataList){
        Set<String> retVal = new HashSet<>();
        if(CollectionUtils.isNotEmpty(dataList)){
            for(AccessViewBean bean : dataList){
                retVal.add(bean.getId());
            }
        }
        return retVal;
    }
    protected List<TreeNode<AccessViewBean>> applyFilter(List<TreeNode<AccessViewBean>> dataList) {
        if(!accessReviewData.showAll() || accessReviewData.isFilterSet()){
            if(!accessReviewData.showAll()){
                removeUnnecessaryObjectTypes(dataList);
            }

            if(accessReviewData.isFilterSet()){
                List<TreeNode<AccessViewBean>> result = new ArrayList<TreeNode<AccessViewBean>>();
                for(TreeNode<AccessViewBean> node: dataList){
                    applyFilterToElement(node);
                }
                if(CollectionUtils.isNotEmpty(accessReviewData.getFilteredNodes())){
                    for (TreeNode<AccessViewBean> node : accessReviewData.getFilteredNodes()){
                        result.add(node);
                    }
                }
                return result;
            }
        }
        return dataList;
    }

    private boolean checkResourceId(String resourceId) {
        return !accessReviewData.isInTargetResource(resourceId) && !isException(resourceId);
    }
    private boolean isException(String resourceId){
        Set<String> exceptions = getResourceEntitlementStrategy().getUserExceptionsCache();
        return CollectionUtils.isNotEmpty(exceptions) && exceptions.contains(resourceId);
    }

    private boolean isDeletable(String resourceId){
        Set<String> exceptions = getResourceEntitlementStrategy().getUserExceptionsCache();
        return CollectionUtils.isNotEmpty(exceptions) && exceptions.contains(resourceId);
    }



    private void removeUnnecessaryObjectTypes(List<TreeNode<AccessViewBean>> dataList) {
        if(CollectionUtils.isNotEmpty(dataList)){
            for(int i=0; i < dataList.size();i++){
                TreeNode<AccessViewBean> node = dataList.get(i);

                if(checkObjectType(node, accessReviewData)){
                    removeUnnecessaryObjectTypes(node.getChildren());
                } else {
                    node.bubleChildrenNodes();
                    dataList.remove(i);
                    i--;
                }
            }
        }
    }

    private boolean checkObjectType(TreeNode<AccessViewBean> node, AccessReviewData accessReviewData) {
        if(node!=null){
            if(AccessReviewConstant.ROLE_TYPE.equals(node.getData().getBeanType()) && accessReviewData.showRoles())
                return true;
            else if(AccessReviewConstant.GROUP_TYPE.equals(node.getData().getBeanType()) && accessReviewData.showGroups() )
                return true;
            else if(AccessReviewConstant.RESOURCE_TYPE.equals(node.getData().getBeanType()) && accessReviewData.showAll() )
                return true;
            else if("MANAGED_SYS".toLowerCase().equals(node.getIcon()) && accessReviewData.showManagedSys())
                return true;
        }
        return false;
    }


    private void applyFilterToElement(TreeNode<AccessViewBean> element) {
        if(compareNode(element, accessReviewData.getFilter())){
            accessReviewData.getFilteredNodes().add(element);
        } else {
            if(CollectionUtils.isNotEmpty(element.getChildren())){
                for(TreeNode<AccessViewBean> child: element.getChildren()){
                    applyFilterToElement(child);
                }
            }
        }
    }

    private boolean compareNode(TreeNode<AccessViewBean> parentElement, AccessViewFilterBean filter) {
        boolean result = false;
        boolean isMatch = false;
        int flag = 0;

        AccessViewBean bean = parentElement.getData();

        if(StringUtils.isNotBlank(filter.getName())){
            flag += AccessReviewConstant.NAME_FILTER_SET;
            result = (bean.getName()!=null) ? bean.getName().toLowerCase().contains(filter.getName().toLowerCase()):false;
        }
        if(StringUtils.isNotBlank(filter.getDescription())){
            flag += AccessReviewConstant.DESCRIPTION_FILTER_SET;
            isMatch = (bean.getDescription()!=null) ? bean.getDescription().toLowerCase().contains(filter.getDescription().toLowerCase()):false;
            result = ((flag & AccessReviewConstant.NAME_FILTER_SET) == AccessReviewConstant.NAME_FILTER_SET) ? result && isMatch : isMatch;
        }
        if(StringUtils.isNotBlank(filter.getRisk())){
            flag += AccessReviewConstant.RISK_FILTER_SET;
            isMatch = (bean.getRisk()!=null) ? bean.getRisk().toLowerCase().equals(filter.getRisk().toLowerCase()) : false;
            result =  (((flag & AccessReviewConstant.NAME_FILTER_SET) == AccessReviewConstant.NAME_FILTER_SET)
                       || ((flag & AccessReviewConstant.DESCRIPTION_FILTER_SET) == AccessReviewConstant.DESCRIPTION_FILTER_SET)) ? result && isMatch : isMatch;
        }

        if(filter.getShowExceptionsFlag()!=null && filter.getShowExceptionsFlag()){
            result =  (((flag & AccessReviewConstant.NAME_FILTER_SET) == AccessReviewConstant.NAME_FILTER_SET)
                       || ((flag & AccessReviewConstant.DESCRIPTION_FILTER_SET) == AccessReviewConstant.DESCRIPTION_FILTER_SET)
                       || ((flag & AccessReviewConstant.RISK_FILTER_SET) == AccessReviewConstant.RISK_FILTER_SET)) ? result && parentElement.getIsException() : parentElement.getIsException();
        }


        return result;
    }


    protected EntitlementsStrategy getRoleEntitlementStrategy(){
        if(this.roleEntitlementStrategy==null)
            this.roleEntitlementStrategy = new RoleEntitlementStrategy(accessReviewData);
        return roleEntitlementStrategy;
    }
    protected EntitlementsStrategy getGroupEntitlementStrategy(){
        if(this.groupEntitlementStrategy==null)
            this.groupEntitlementStrategy = new GroupEntitlementStrategy(accessReviewData);
        return groupEntitlementStrategy;
    }
    protected EntitlementsStrategy getResourceEntitlementStrategy(){
        if(this.resourceEntitlementStrategy==null)
            this.resourceEntitlementStrategy = new ResourceEntitlementStrategy(accessReviewData);
        return resourceEntitlementStrategy;
    }

    private boolean checkMngSys(String parentMngSysId, AbstractAuthorizationEntity bean, int level){
        if(StringUtils.isBlank(parentMngSysId))
            return (StringUtils.isBlank(bean.getManagedSysId())
                    || accessReviewData.getDefaultManagedSysId().equals(bean.getManagedSysId())
                    || (level==0 && accessReviewData.isRoot(bean)));
        else
            return parentMngSysId.equals(bean.getManagedSysId());
    }

    protected boolean isTerminating(String type, String id) {
        Map<String, TaskWrapper> taskMap=null;
        switch (type){
            case AccessReviewConstant.RESOURCE_TYPE:
                taskMap = accessReviewData.getResourceWorkflowMap();
                break;
            case AccessReviewConstant.GROUP_TYPE:
                taskMap = accessReviewData.getGroupWorkflowMap();
                break;
            case AccessReviewConstant.ROLE_TYPE:
                taskMap = accessReviewData.getRoleWorkflowMap();
                break;
        }
        if(taskMap!=null && taskMap.containsKey(id)){
            TaskWrapper task=taskMap.get(id);
            return task!=null && (task.getWorkflowName().equals(ActivitiRequestType.DISENTITLE_USR_FROM_RESOURCE.getKey())
                    || task.getWorkflowName().equals(ActivitiRequestType.REMOVE_USER_FROM_GROUP.getKey())
                    || task.getWorkflowName().equals(ActivitiRequestType.REMOVE_USER_FROM_ROLE.getKey()));
        }
        return false;
    }

}
