package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.accessmodel.*;
import org.openiam.provision.dto.common.UserSearchKey;
import org.openiam.provision.dto.common.UserSearchKeyEnum;
import org.openiam.provision.dto.srcadapter.SourceAdapterInfoResponse;
import org.openiam.provision.dto.srcadapter.SourceAdapterOperationEnum;
import org.openiam.provision.dto.srcadapter.SourceAdapterRequest;
import org.openiam.provision.dto.srcadapter.SourceAdapterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.*;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(endpointInterface = "org.openiam.provision.service.UserAccessControlService", targetNamespace = "http://www.openiam.org/service/provision", portName = "UserAccessControlServicePort", serviceName = "UserAccessControlService")
@Component("userAccessControlService")
public class UserAccessControlServiceImpl implements UserAccessControlService {
    @Autowired
    private UserDataWebService userDataService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    private AuthorizationManagerAdminService adminService;


    @Override
    public UserAccessControlResponse getAccessControl(UserAccessControlRequest request) {
        UserAccessControlResponse response = new UserAccessControlResponse();
        UserAccessControlBean controlBean = new UserAccessControlBean();

        User user = null;
        try {
            user = this.getUser(request.getKey());
        } catch (Exception e) {
            response.setError("Can't get User" + e);
            response.setStatus(ResponseStatus.FAILURE);
            return response;
        }
        if (user == null) {
            response.setError("No such user");
            response.setStatus(ResponseStatus.FAILURE);
            return response;
        }

        controlBean.setEmployeeId(user.getEmployeeId());
        controlBean.setFirstName(user.getFirstName());
        controlBean.setLastName(user.getLastName());
        if (CollectionUtils.isNotEmpty(user.getPrincipalList())) {
            Set<String> logins = new HashSet<>();
            for (Login l : user.getPrincipalList()) {
                logins.add(l.getLogin());
            }
            controlBean.setLogins(logins);
        }
        UserEntitlementsMatrix matrix = adminService.getUserEntitlementsMatrix(user.getId(), new Date());
        if (matrix != null) {
            Map<String, AuthorizationResource> resourceMap = matrix.getResourceMap();
            Map<String, AuthorizationGroup> groupMap = matrix.getGroupMap();
            Map<String, AuthorizationRole> roleMap = matrix.getRoleMap();
            Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
            Set<UserAccessControlMemberBean> compiledSet = new HashSet<UserAccessControlMemberBean>();

            //process filter
            Set<String> managedSystemFilter = null;
            Set<String> resourceTypeFilter = null;
            if (request.getFilter() != null && request.getFilter().getManagedSystemNames() != null) {
                managedSystemFilter = request.getFilter().getManagedSystemNames();
            }
            if (request.getFilter() != null && request.getFilter().getResourceTypes() != null) {
                resourceTypeFilter = request.getFilter().getResourceTypes();
            }

            //process groups
            if (matrix.getDirectGroupIds() != null) {
                directSet.addAll(this.processGroups(matrix.getDirectGroupIds(), groupMap, managedSystemFilter));
            }
            if (matrix.getCompiledGroupIds() != null) {
                compiledSet.addAll(this.processGroups(matrix.getCompiledGroupIds(), groupMap, managedSystemFilter));
            }
            //process roles
            if (matrix.getDirectRoleIds() != null) {
                directSet.addAll(this.processRoles(matrix.getDirectRoleIds(), roleMap, managedSystemFilter));
            }
            if (matrix.getCompiledRoleIds() != null) {
                compiledSet.addAll(this.processRoles(matrix.getCompiledRoleIds(), roleMap, managedSystemFilter));
            }

            //process resources
            if (matrix.getDirectResourceIds() != null) {
                directSet.addAll(this.processResources(matrix.getDirectResourceIds(), resourceMap, resourceTypeFilter));
            }
            if (matrix.getCompiledResourceIds() != null) {
                compiledSet.addAll(this.processResources(matrix.getCompiledResourceIds(), resourceMap, resourceTypeFilter));
            }

            compiledSet.removeAll(directSet);

            controlBean.setCompiledEntitlements(compiledSet);
            controlBean.setDirectEntitles(directSet);
        }
        response.setBean(controlBean);
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private Set<UserAccessControlMemberBean> processGroups(Map<String, Set<String>> groups,
                                                           Map<String, AuthorizationGroup> groupMap, Set<String> filter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String groupKey : groups.keySet()) {
            if (groups.get(groupKey) != null) {
                AuthorizationGroup group = groupMap.get(groupKey);
                if (group != null &&
                        filtered(filter, group.getManagedSysId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("group");
                    bean.setType(group.getManagedSysId());
                    bean.setName(group.getName());
                    bean.setType(group.getTypeId());
                    bean.setRights(groups.get(groupKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }

    private Set<UserAccessControlMemberBean> processRoles(Map<String, Set<String>> roles,
                                                          Map<String, AuthorizationRole> roleMap, Set<String> filter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String roleKey : roles.keySet()) {
            if (roles.get(roleKey) != null) {
                AuthorizationRole role = roleMap.get(roleKey);
                if (role != null &&
                        filtered(filter, role.getManagedSysId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("role");
                    bean.setType(role.getManagedSysId());
                    bean.setName(role.getName());
                    bean.setType(role.getTypeId());
                    bean.setRights(roles.get(roleKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }


    private Set<UserAccessControlMemberBean> processResources(Map<String, Set<String>> resources,
                                                              Map<String, AuthorizationResource> resourceMap, Set<String> filter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String resourceKey : resources.keySet()) {
            if (resources.get(resourceKey) != null) {
                AuthorizationResource resource = resourceMap.get(resourceKey);
                if (resource != null &&
                        filtered(filter, resource.getResourceTypeId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("resource");
                    bean.setType(resource.getResourceTypeId());
                    bean.setName(resource.getName());
                    bean.setRights(resources.get(resourceKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }


    private boolean filtered(Set<String> rules, String value) {
        if (CollectionUtils.isEmpty(rules)) {
            return true;
        }
        return rules.contains(value);
    }


    private User getUser(UserSearchKey keyPair) throws Exception {
        return this.findByKey(keyPair.getName(), keyPair.getValue());
    }


    private User findByKey(UserSearchKeyEnum matchAttrName, String matchAttrValue) throws Exception {
        UserSearchBean searchBean = new UserSearchBean();
        if (UserSearchKeyEnum.USERID.equals(matchAttrName)) {
            searchBean.setKey(matchAttrValue);
            searchBean.setUserId(matchAttrValue);
        } else if (UserSearchKeyEnum.PRINCIPAL.equals(matchAttrName)) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            searchBean.setPrincipal(lsb);
        } else if (UserSearchKeyEnum.EMAIL.equals(matchAttrName)) {
            searchBean.setEmailAddressMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        } else if (UserSearchKeyEnum.EMPLOYEE_ID.equals(matchAttrName)) {
            searchBean.setEmployeeIdMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
        }
        searchBean.setDeepCopy(true);
        List<User> userList = userDataService.findBeans(searchBean, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(userList)) {
            if (userList.size() > 1) {
                throw new Exception("Identifier not unique=" + matchAttrName + ":" + matchAttrValue);
            }
            return userList.get(0);
        } else {
            return null;
        }
    }

}
