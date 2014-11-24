package org.openiam.access.review.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.access.review.constant.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.access.review.strategy.AccessReviewStrategy;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeNode;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ManagedSystemWebService managedSystemService;
    @Autowired
    private ResourceDataService resourceService;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Override
    public AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType,final Language language) {
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
        return new AccessViewResponse(dataList, dataList.size(), exceptionList);
    }

    @Override
    public AccessViewResponse getAccessReviewSubTree(AccessViewFilterBean filter, String viewType, Language language) {
        return null;
    }

    private AccessReviewStrategy getAccessReviewStrategy(AccessViewFilterBean filter, String viewType, Language language) {
        final List<LoginEntity> loginList = loginDS.getLoginByUser(filter.getUserId());
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
        accessReviewData.setWorkflowsMaps(activitiService.getTasksForMemberAssociation(filter.getUserId()));

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
        Map<String, ManagedSysDto> managedSysMap = new HashMap<String, ManagedSysDto>();
        ManagedSysSearchBean searchBean = new ManagedSysSearchBean();
        List<ManagedSysDto> results = managedSystemService.getManagedSystems(searchBean, Integer.MAX_VALUE, 0);
        if (CollectionUtils.isNotEmpty(results)) {
            for(ManagedSysDto mngsys : results){
                managedSysMap.put(mngsys.getResourceId(), mngsys);
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
