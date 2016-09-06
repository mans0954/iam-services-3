package org.openiam.am.srvc.service;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.openiam.constants.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.TaskSearchRequest;
import org.openiam.base.response.ManagedSysListResponse;
import org.openiam.base.response.TaskListResponse;
import org.openiam.base.response.TaskWrapperResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.model.AccessViewBean;
import org.openiam.model.AccessViewFilterBean;
import org.openiam.model.AccessViewResponse;
import org.openiam.access.review.strategy.AccessReviewStrategy;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeNode;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.constants.ManagedSystemAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.openiam.base.response.TaskWrapper;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ResourceService resourceService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    private AccessRightService accessRightDataService;
    @Autowired
    private PropertyValueSweeper propertyValueSweeper;

    @Autowired
    protected RabbitMQSender rabbitMQSender;

/*    @Value("${org.openiam.attestation.exclude.menus}")
    private Boolean excludeMenus;*/

    private Boolean isExcludeMenus() {
        return propertyValueSweeper.getBoolean("org.openiam.attestation.exclude.menus");
    }

    @Override
    public AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType, final Date date, final Language language) {
        final StopWatch sw = new StopWatch();
        sw.start();
        AccessReviewStrategy strategy = getAccessReviewStrategy(filter, viewType, date, language);
        List<TreeNode<AccessViewBean>> dataList = new ArrayList<>();
        List<TreeNode<AccessViewBean>> exceptionList = null;
        if(strategy!=null) {
            dataList = strategy.buildView();
            exceptionList = strategy.getExceptionsList();

            if(log.isDebugEnabled()) {
            	log.debug("========ACCESS VIEW TREE============");
            }
            TreeNode<AccessViewBean> rootElement = new TreeNode<>(new AccessViewBean(), null);
            rootElement.add(dataList);
            if(log.isDebugEnabled()) {
            	log.debug(rootElement.toString());
            }
        }
        sw.stop();
        log.info(String.format("Done building access review tree. Took: %s ms", sw.getTime()));
        return new AccessViewResponse(dataList, dataList.size(), exceptionList);
    }

    @Override
    public AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, boolean isRootOnly, AccessViewFilterBean filter, String viewType,  final Date date, Language language) {
        AccessViewResponse response = this.getAccessReviewTree(filter, viewType, date, language);
        List<TreeNode<AccessViewBean>> childrenList = new ArrayList<>();
        if(response==null)
            return AccessViewResponse.EMPTY_RESPONSE;
        if(CollectionUtils.isNotEmpty(response.getBeans())){
            List<TreeNode<AccessViewBean>> treeNodes = response.getBeans();
//            Iterator<TreeNode<AccessViewBean>> iter = treeNodes.iterator();
            if(parentId==null){
                // get roots elements
                childrenList = treeNodes;
            } else {
                for (int i = 0; i < treeNodes.size(); i++) {
                    TreeNode<AccessViewBean> node = treeNodes.get(i);
                    AccessViewBean data = node.getData();
                    if (data.getBeanType().equals(parentBeanType)
                            && data.getId().equals(parentId)
                            && CollectionUtils.isNotEmpty(node.getChildren())) {
                        childrenList = node.getChildren();
                        break;
                    } else if (CollectionUtils.isNotEmpty(node.getChildren())) {
                        treeNodes.addAll(node.getChildren());
                    }
                }
            }
            if(isRootOnly && CollectionUtils.isNotEmpty(childrenList)){
                childrenList.forEach(node ->{
                    node.hideChildren();
                });
            }
        }
        return new AccessViewResponse(childrenList, childrenList.size(), response.getExceptions());
    }

    private AccessReviewStrategy getAccessReviewStrategy(AccessViewFilterBean filter, String viewType, Date date, Language language) {
        final List<LoginEntity> loginList = loginDS.getLoginByUser(filter.getUserId());
        final List<AccessRight> accessRights = accessRightDataService.findBeansDTO(new AccessRightSearchBean(), 0, Integer.MAX_VALUE, language);
        UserEntitlementsMatrix userEntitlementsMatrix = adminService.getUserEntitlementsMatrix(filter.getUserId(), date);

        if(StringUtils.isNotBlank(filter.getAttestationTaskId())){
            TaskWrapper attestationTask = null;
            IdServiceRequest request = new IdServiceRequest();
            request.setId(filter.getAttestationTaskId());
            TaskWrapperResponse resp = rabbitMQSender.sendAndReceive(OpenIAMQueue.ActivitiQueue, ActivitiAPI.GetTask, request, TaskWrapperResponse.class);
            if(resp.isSuccess()){
                attestationTask = resp.getTask();
            }
            if(attestationTask!=null){
                filter.setAttestationManagedSysFilter(new HashSet<String>(attestationTask.getAttestationManagedSysFilter()));
            }
        }

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

        TaskSearchRequest request = new TaskSearchRequest();
        request.setSearchBean(searchBean);
        request.setFrom(0);
        request.setSize(Integer.MAX_VALUE);

        TaskListResponse response = rabbitMQSender.sendAndReceive(OpenIAMQueue.ActivitiQueue, ActivitiAPI.FindTasks, request, TaskListResponse.class);
        if(response.isSuccess()){
            accessReviewData.setWorkflowsMaps(response.getTaskList());
        }
        accessReviewData.setExcludeMenus(this.isExcludeMenus());

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


    private Map<String, ManagedSysDto> getManagedSysMap() {
        Map<String, ManagedSysDto> managedSysMap = new HashMap<>();
//        ManagedSysSearchBean searchBean = new ManagedSysSearchBean();

        ManagedSysListResponse response = rabbitMQSender.sendAndReceive(OpenIAMQueue.ManagedSysQueue, ManagedSystemAPI.GetAllManagedSys, new BaseServiceRequest(), ManagedSysListResponse.class);
        List<ManagedSysDto> results = null;
        if(response.isSuccess()){
            results = response.getManagedSysList();
        }
        if (CollectionUtils.isNotEmpty(results)) {
            for(ManagedSysDto mngsys : results){
                managedSysMap.put(mngsys.getResource().getId(), mngsys);
            }
        }
        return managedSysMap;
    }

    private Map<String, ResourceType> getResourceTypeMap(final Language language){
        Map<String, ResourceType> resourceTypeMap = new HashMap<String, ResourceType>();
        final List<org.openiam.idm.srvc.res.dto.ResourceType> resourceTypeList = resourceService.getAllResourceTypesDto(language);

        if (CollectionUtils.isNotEmpty(resourceTypeList)) {
            for(ResourceType resourceType : resourceTypeList){
                resourceTypeMap.put(resourceType.getId(), resourceType);
            }
        }
        return resourceTypeMap;
    }
}
