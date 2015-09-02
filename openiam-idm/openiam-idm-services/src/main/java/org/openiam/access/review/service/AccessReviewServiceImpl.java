package org.openiam.access.review.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.openiam.access.review.constant.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.access.review.strategy.AccessReviewStrategy;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeNode;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.ws.AccessRightDataService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by alexander on 21.11.14.
 */
@Service
public class AccessReviewServiceImpl implements AccessReviewService {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthorizationManagerAdminService adminService;
    @Autowired
    private LoginDataService loginDS;
    @Autowired
    private ManagedSystemService managedSystemService;
    @Autowired
    private ResourceDataService resourceService;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    private AccessRightDataService accessRightDataService;

    @Override
    public AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType,final Language language) {
        final StopWatch sw = new StopWatch();
        sw.start();
        AccessReviewStrategy strategy = getAccessReviewStrategy(filter, viewType, language);
        List<TreeNode<AccessViewBean>> dataList = new ArrayList<>();
        List<TreeNode<AccessViewBean>> exceptionList = null;
        if(strategy!=null) {
            dataList = strategy.buildView();
            exceptionList = strategy.getExceptionsList();

            log.debug("========ACCESS VIEW TREE============");
            TreeNode<AccessViewBean> rootElement = new TreeNode<>(new AccessViewBean());
            rootElement.add(dataList);
            log.debug(rootElement.toString());
        }
        sw.stop();
        log.info(String.format("Done building access review tree. Took: %s ms", sw.getTime()));
        return new AccessViewResponse(dataList, dataList.size(), exceptionList);
    }

    @Override
    public AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, AccessViewFilterBean filter, String viewType, Language language) {
        AccessViewResponse response = this.getAccessReviewTree(filter, viewType, language);
        List<TreeNode<AccessViewBean>> childrenList = new ArrayList<>();
        if(response==null)
            return AccessViewResponse.EMPTY_RESPONSE;
        if(CollectionUtils.isNotEmpty(response.getBeans())){
            List<TreeNode<AccessViewBean>> treeNodes = response.getBeans();
//            Iterator<TreeNode<AccessViewBean>> iter = treeNodes.iterator();

            for(int i=0; i<treeNodes.size();i++){
                TreeNode<AccessViewBean> node = treeNodes.get(i);
                AccessViewBean data = node.getData();
                if(data.getBeanType().equals(parentBeanType)
                        && data.getId().equals(parentId)
                        && CollectionUtils.isNotEmpty(node.getChildren())){
                    childrenList = node.getChildren();
                    break;
                } else if(CollectionUtils.isNotEmpty(node.getChildren())){
                    treeNodes.addAll(node.getChildren());
                }
            }
        }
        return new AccessViewResponse(childrenList, childrenList.size(), null);
    }

    private AccessReviewStrategy getAccessReviewStrategy(AccessViewFilterBean filter, String viewType, Language language) {
        final List<LoginEntity> loginList = loginDS.getLoginByUser(filter.getUserId());
        final List<AccessRight> accessRights = accessRightDataService.findBeans(new AccessRightSearchBean(), 0, Integer.MAX_VALUE, language);
        UserEntitlementsMatrix userEntitlementsMatrix = adminService.getUserEntitlementsMatrix(filter.getUserId());

        AccessReviewData accessReviewData = new AccessReviewData();
        accessReviewData.setMatrix(userEntitlementsMatrix);
        accessReviewData.setFilter(filter);
        accessReviewData.setMngsysMap(getManagedSysMap());
        accessReviewData.setResourceTypeMap(getResourceTypeMap(language));
        accessReviewData.setView(viewType);
        accessReviewData.setLoginList((CollectionUtils.isNotEmpty(loginList)) ? loginList : null);
        accessReviewData.setDefaultManagedSysId(sysConfiguration.getDefaultManagedSysId());
        accessReviewData.setMaxHierarchyLevel(filter.getMaxHierarchyLevel());
        accessReviewData.populateAccessRightMap(accessRights);

        TaskSearchBean searchBean = new TaskSearchBean();
        searchBean.setMemberAssociationId(filter.getUserId());

        accessReviewData.setWorkflowsMaps(activitiService.findTasks(searchBean, 0, Integer.MAX_VALUE));

        AccessReviewStrategy strategy = null;
        if(AccessReviewConstant.ROLE_VIEW.equals(viewType)){
            strategy = AccessReviewStrategy.getRoleViewStrategy(accessReviewData);
        } else if(AccessReviewConstant.RESOURCE_VIEW.equals(viewType)){
            strategy = AccessReviewStrategy.getResourceViewStrategy(accessReviewData);
        } else if(AccessReviewConstant.GROUP_VIEW.equals(viewType)){
            strategy = AccessReviewStrategy.getGroupViewStrategy(accessReviewData);
        }
        return strategy;
    }


    private Map<String, ManagedSysEntity> getManagedSysMap() {
        Map<String, ManagedSysEntity> managedSysMap = new HashMap<>();
//        ManagedSysSearchBean searchBean = new ManagedSysSearchBean();
        List<ManagedSysEntity> results = managedSystemService.getAllManagedSys();
        if (CollectionUtils.isNotEmpty(results)) {
            for(ManagedSysEntity mngsys : results){
                managedSysMap.put(mngsys.getResource().getId(), mngsys);
            }
        }
        return managedSysMap;
    }

    private Map<String, ResourceType> getResourceTypeMap(final Language language){
        Map<String, ResourceType> resourceTypeMap = new HashMap<String, ResourceType>();
        final List<org.openiam.idm.srvc.res.dto.ResourceType> resourceTypeList = resourceService.getAllResourceTypes(language);

        if (CollectionUtils.isNotEmpty(resourceTypeList)) {
            for(ResourceType resourceType : resourceTypeList){
                resourceTypeMap.put(resourceType.getId(), resourceType);
            }
        }
        return resourceTypeMap;
    }
}
