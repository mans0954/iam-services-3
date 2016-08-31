package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.am.srvc.dto.jdbc.AuthorizationResource;
import org.openiam.am.srvc.dto.jdbc.AuthorizationRole;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.model.UserEntitlementsMatrix;
import org.openiam.provision.dto.accessmodel.UserAccessControlBean;
import org.openiam.provision.dto.accessmodel.UserAccessControlMemberBean;
import org.openiam.provision.dto.accessmodel.UserAccessControlRequest;
import org.openiam.provision.dto.accessmodel.UserAccessControlResponse;
import org.openiam.provision.dto.common.UserSearchKey;
import org.openiam.provision.dto.common.UserSearchKeyEnum;
import org.openiam.srvc.am.AccessRightDataService;
import org.openiam.srvc.am.ResourceDataService;
import org.openiam.srvc.user.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected MetadataService metadataService;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    private AuthorizationManagerAdminService adminService;
    @Autowired
    private AccessRightDataService accessRightDataService;


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
            List<String> managedSystemFilter = null;
            List<String> resourceTypeFilter = null;
            List<String> groupsMdTypeFilter = null;
            List<String> rolesMdTypeFilter = null;
            List<String> resourceMdTypeFilter = null;
            if (request.getFilter() != null && request.getFilter().getManagedSystemNames() != null) {
                managedSystemFilter = request.getFilter().getManagedSystemNames();
            }
            if (request.getFilter() != null && request.getFilter().getResourceTypes() != null) {
                resourceTypeFilter = request.getFilter().getResourceTypes();
            }
            if (request.getFilter() != null && request.getFilter().getRoleMetadataTypes() != null) {
                rolesMdTypeFilter = request.getFilter().getRoleMetadataTypes();
            }
            if (request.getFilter() != null && request.getFilter().getGroupMetadataTypes() != null) {
                groupsMdTypeFilter = request.getFilter().getGroupMetadataTypes();
            }
            if (request.getFilter() != null && request.getFilter().getResourceMetadataTypes() != null) {
                resourceMdTypeFilter = request.getFilter().getResourceMetadataTypes();
            }

            //process groups
            if (matrix.getDirectGroupIds() != null) {
                directSet.addAll(this.processGroups(matrix.getDirectGroupIds(), groupMap, managedSystemFilter, groupsMdTypeFilter));
            }
            if (matrix.getCompiledGroupIds() != null) {
                compiledSet.addAll(this.processGroups(matrix.getCompiledGroupIds(), groupMap, managedSystemFilter, groupsMdTypeFilter));
            }
            //process roles
            if (matrix.getDirectRoleIds() != null) {
                directSet.addAll(this.processRoles(matrix.getDirectRoleIds(), roleMap, managedSystemFilter, rolesMdTypeFilter));
            }
            if (matrix.getCompiledRoleIds() != null) {
                compiledSet.addAll(this.processRoles(matrix.getCompiledRoleIds(), roleMap, managedSystemFilter, rolesMdTypeFilter));
            }

            //process resources
            if (matrix.getDirectResourceIds() != null) {
                directSet.addAll(this.processResources(matrix.getDirectResourceIds(), resourceMap, resourceTypeFilter, resourceMdTypeFilter));
            }
            if (matrix.getCompiledResourceIds() != null) {
                compiledSet.addAll(this.processResources(matrix.getCompiledResourceIds(), resourceMap, resourceTypeFilter, resourceMdTypeFilter));
            }
            //remove direct set from compiled (prevent duplicates)
            compiledSet.removeAll(directSet);

            // if named types is true we should change managed system id and metadata type id to it's names

            if (request.getNamedTypes()) {
                AccessRightSearchBean accessRightSearchBean = new AccessRightSearchBean();
                accessRightSearchBean.setFindInCache(true);
                accessRightSearchBean.setDeepCopy(false);
                List<AccessRight> accessRights = accessRightDataService.findBeans(accessRightSearchBean, -1, -1, null);
                List<ManagedSysEntity> managedSysEntities = managedSystemService.getAllManagedSys();
                MetadataTypeSearchBean metadataTypeSearchBean = new MetadataTypeSearchBean();
                metadataTypeSearchBean.setDeepCopy(false);
                metadataTypeSearchBean.setFindInCache(true);
                metadataTypeSearchBean.setGrouping(MetadataTypeGrouping.ROLE_TYPE);
                List<MetadataType> roleMetadataTypes = metadataService.findBeans(metadataTypeSearchBean, -1, -1, null);
                metadataTypeSearchBean.setGrouping(MetadataTypeGrouping.GROUP_TYPE);
                List<MetadataType> groupMetadataTypes = metadataService.findBeans(metadataTypeSearchBean, -1, -1, null);
                metadataTypeSearchBean.setGrouping(MetadataTypeGrouping.RESOURCE_TYPE);
                List<MetadataType> resourceMetadataTypes = metadataService.findBeans(metadataTypeSearchBean, -1, -1, null);
                //fill managed systems
                List<ResourceType> resourceTypes = resourceDataService.getAllResourceTypes(null);
                this.fillNamedTypes(compiledSet, managedSysEntities, roleMetadataTypes, groupMetadataTypes, resourceMetadataTypes, resourceTypes, accessRights);
                this.fillNamedTypes(directSet, managedSysEntities, roleMetadataTypes, groupMetadataTypes, resourceMetadataTypes, resourceTypes, accessRights);
            }

            // save data to control bean;
            controlBean.setCompiledEntitlements(compiledSet);
            controlBean.setDirectEntitles(directSet);
        }
        response.setBean(controlBean);
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    private void fillNamedTypes(Set<UserAccessControlMemberBean> entitlements,
                                List<ManagedSysEntity> managedSysEntities,
                                List<MetadataType> roleMetadataTypes,
                                List<MetadataType> groupMetadataTypes,
                                List<MetadataType> resourceMetadataTypes,
                                List<ResourceType> resourceTypes,
                                List<AccessRight> accessRights) {
        if (CollectionUtils.isNotEmpty(entitlements)) {
            for (UserAccessControlMemberBean bean : entitlements) {
                if (StringUtils.isNotBlank(bean.getManagedSystem())) {
                    if (CollectionUtils.isNotEmpty(managedSysEntities)) {
                        bean.setManagedSystem(managedSysEntities.stream().filter(it -> bean.getManagedSystem().equals(it.getId())).findFirst().get().getName());
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "role".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(roleMetadataTypes)) {
                        bean.setMetadataType(roleMetadataTypes.stream().filter(it -> bean.getMetadataType().equals(it.getId())).findFirst().get().getName());
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "group".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(groupMetadataTypes)) {
                        bean.setMetadataType(groupMetadataTypes.stream().filter(it -> bean.getMetadataType().equals(it.getId())).findFirst().get().getName());
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "resource".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(resourceMetadataTypes)) {
                        bean.setMetadataType(resourceMetadataTypes.stream().filter(it -> bean.getMetadataType().equals(it.getId())).findFirst().get().getName());
                    }
                }
                if (StringUtils.isNotBlank(bean.getType()) && "resource".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(resourceTypes)) {
                        bean.setType(resourceTypes.stream().filter(it -> bean.getType().equals(it.getId())).findFirst().get().getDescription());
                    }
                }
                if (CollectionUtils.isNotEmpty(bean.getRights())) {
                    Set<String> namedRigths = new HashSet<>();
                    for (String accessRight : bean.getRights()) {
                        namedRigths.add(accessRights.stream().filter(it -> accessRight.equalsIgnoreCase(it.getId())).findFirst().get().getName());
                    }
                    bean.setRights(namedRigths);
                } else {
                    bean.setBinaryLink(true);
                }

            }
        }
    }

    private Set<UserAccessControlMemberBean> processGroups(Map<String, Set<String>> groups,
                                                           Map<String, AuthorizationGroup> groupMap, List<String> managedSystemFilter, List<String> metadataTypeFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String groupKey : groups.keySet()) {
            if (groups.get(groupKey) != null) {
                AuthorizationGroup group = groupMap.get(groupKey);
                if (group != null
                        && filtered(managedSystemFilter, group.getManagedSysId())
                        && filtered(metadataTypeFilter, group.getTypeId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("group");
                    bean.setManagedSystem(group.getManagedSysId());
                    bean.setName(group.getName());
                    bean.setMetadataType(group.getTypeId());
                    bean.setRights(groups.get(groupKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }

    private Set<UserAccessControlMemberBean> processRoles(Map<String, Set<String>> roles,
                                                          Map<String, AuthorizationRole> roleMap, List<String> managedSystemFilter, List<String> metadataTypeFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String roleKey : roles.keySet()) {
            if (roles.get(roleKey) != null) {
                AuthorizationRole role = roleMap.get(roleKey);
                if (role != null && filtered(managedSystemFilter, role.getManagedSysId())
                        && filtered(metadataTypeFilter, role.getTypeId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("role");
                    bean.setManagedSystem(role.getManagedSysId());
                    bean.setName(role.getName());
                    bean.setMetadataType(role.getTypeId());
                    bean.setRights(roles.get(roleKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }


    private Set<UserAccessControlMemberBean> processResources(Map<String, Set<String>> resources,
                                                              Map<String, AuthorizationResource> resourceMap, List<String> resourceTypeFilter, List<String> metadataTypeFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String resourceKey : resources.keySet()) {
            if (resources.get(resourceKey) != null) {
                AuthorizationResource resource = resourceMap.get(resourceKey);
                if (resource != null &&
                        filtered(resourceTypeFilter, resource.getResourceTypeId())
                        && filtered(metadataTypeFilter, resource.getMetadataTypeId())) {
                    UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                    bean.setObjectType("resource");
                    bean.setMetadataType(resource.getMetadataTypeId());
                    bean.setType(resource.getResourceTypeId());
                    bean.setName(resource.getName());
                    bean.setRights(resources.get(resourceKey));
                    directSet.add(bean);
                }

            }
        }
        return directSet;
    }


    private boolean filtered(List<String> rules, String value) {
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
