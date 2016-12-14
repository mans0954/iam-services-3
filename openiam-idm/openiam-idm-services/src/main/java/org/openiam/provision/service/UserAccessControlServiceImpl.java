package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.model.UserEntitlementsMatrix;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.service.AccessRightDataService;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.accessmodel.UserAccessControlBean;
import org.openiam.provision.dto.accessmodel.UserAccessControlMemberBean;
import org.openiam.provision.dto.accessmodel.UserAccessControlRequest;
import org.openiam.provision.dto.accessmodel.UserAccessControlResponse;
import org.openiam.provision.dto.srcadapter.UserSearchKey;
import org.openiam.provision.dto.srcadapter.UserSearchKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zaporozhec on 10/29/15.
 */
@WebService(endpointInterface = "org.openiam.provision.service.UserAccessControlService", targetNamespace = "http://www.openiam.org/service/provision", portName = "UserAccessControlServicePort", serviceName = "UserAccessControlService")
@Component("userAccessControlWS")
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
    protected GroupDataWebService groupDataWebService;

    @Autowired
    protected RoleDataService roleDataService;

    @Autowired
    private AuthorizationManagerAdminService adminService;
    @Autowired
    private AccessRightDataService accessRightDataService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH mm ss");

    @Override
    public UserAccessControlResponse getAccessControl(UserAccessControlRequest request) {
        IdmAuditLog log = new IdmAuditLog();
        log.setRequestorPrincipal(request.getRequesterLogin());
        log.setRequestorUserId(request.getRequesterId());
        log.setNodeIP(request.getIpAdress());

        log.setAction(AuditAction.ACCESS_CONTROL_REQUEST.value());
        try {
            log.addCustomRecord("REQUEST", mapper.writeValueAsString(request));
        } catch (Exception e) {
            log.addWarning("Cound not parse request.");
        }

        UserAccessControlResponse response = new UserAccessControlResponse();
        UserAccessControlBean controlBean = new UserAccessControlBean();

        User user = null;
        try {
            user = this.getUser(request.getKey());
        } catch (Exception e) {
            response.setError("Can't get User" + e);
            response.setStatus(ResponseStatus.FAILURE);
            log.setFailureReason("Can't find user:" + e.getMessage());
            log.setResult(AuditResult.FAILURE.value());
            auditLogService.enqueue(log);
            return response;
        }
        if (user == null) {
            response.setError("No such user");
            response.setStatus(ResponseStatus.FAILURE);
            log.setFailureReason("No such user");
            log.setResult(AuditResult.FAILURE.value());
            auditLogService.enqueue(log);
            return response;
        }

        controlBean.setEmployeeId(user.getEmployeeId());
        controlBean.setFirstName(user.getFirstName());
        controlBean.setLastName(user.getLastName());
        controlBean.setStatus(user.getStatus());
        controlBean.setSecondaryStatus(user.getSecondaryStatus());
        if (user.getStartDate() != null) {
            controlBean.setStartDate(sdf.format(user.getStartDate()));
        }
        if (user.getLastDate() != null) {
            controlBean.setLastDate(sdf.format(user.getLastDate()));
        }
        if (CollectionUtils.isNotEmpty(user.getPrincipalList())) {
            for (Login l : user.getPrincipalList()) {
                if (sysConfiguration.getDefaultManagedSysId().equalsIgnoreCase(l.getManagedSysId())) {
                    log.setTargetUser(user.getId(), l.getLogin());
                    controlBean.setLogin(l.getLogin());
                    controlBean.setLocked(l.getIsLocked() != 0);
                    if (l.getLastLogin() != null) {
                        controlBean.setLastLoginTime(sdf.format(l.getLastLogin()));
                    }
                }
            }
        }
        Map<String, Set<String>> groups = new HashMap<>();
        Map<String, Set<String>> resources = new HashMap<>();
        Map<String, Set<String>> roles = new HashMap<>();
        this.getResources(user.getResources(), user.getGroups(), user.getRoles(), resources);
        this.getGroups(user.getGroups(), resources, groups);
        this.getRoles(user.getRoles(), resources, roles);
        UserEntitlementsMatrix matrix = adminService.getUserEntitlementsMatrix(user.getId());
        if (matrix != null) {
            Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
            //process filter
            List<String> managedSystemFilter = null;
            List<String> resourceTypeFilter = null;
            List<String> groupsMdTypeFilter = null;
            List<String> rolesMdTypeFilter = null;
            List<String> resourceMdTypeFilter = null;
            String roleFilter = null;
            String groupFilter = null;
            String resourceFilter = null;
            String commonNameFilter = null;
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
            if (request.getFilter() != null && request.getFilter().getCommonNameFilter() != null) {
                roleFilter = request.getFilter().getCommonNameFilter();
                groupFilter = request.getFilter().getCommonNameFilter();
                resourceFilter = request.getFilter().getCommonNameFilter();
            } else {
                if (request.getFilter() != null && request.getFilter().getRoleFilter() != null) {
                    roleFilter = request.getFilter().getRoleFilter();
                }
                if (request.getFilter() != null && request.getFilter().getGroupFIlter() != null) {
                    groupFilter = request.getFilter().getGroupFIlter();
                }
                if (request.getFilter() != null && request.getFilter().getResourceFilter() != null) {
                    resourceFilter = request.getFilter().getResourceFilter();
                }
            }

            //process groups
            directSet.addAll(this.processGroups(groups, matrix.getGroupMap(), managedSystemFilter, groupsMdTypeFilter, groupFilter));
//            //process roles
            directSet.addAll(this.processRoles(roles, matrix.getRoleMap(), managedSystemFilter, rolesMdTypeFilter, roleFilter));
            //process resources
            directSet.addAll(this.processResources(resources, matrix.getResourceMap(), resourceTypeFilter, resourceMdTypeFilter, resourceFilter));
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
//                this.fillNamedTypes(compiledSet, managedSysEntities, roleMetadataTypes, groupMetadataTypes, resourceMetadataTypes, resourceTypes, accessRights);
                this.fillNamedTypes(directSet, managedSysEntities, roleMetadataTypes, groupMetadataTypes, resourceMetadataTypes, resourceTypes, accessRights);
            }

            // save data to control bean;
            controlBean.setEntitlements(directSet);
        }
        response.setBean(controlBean);
        response.setStatus(ResponseStatus.SUCCESS);
        try {
            log.addCustomRecord("RESPONSE", mapper.writeValueAsString(response));
        } catch (Exception e) {
            log.addWarning("Couldn't parse response.");
        }
        auditLogService.enqueue(log);
        return response;
    }


    private void getResources(Set<Resource> resources, Set<Group> groups, Set<Role> roles, Map<String, Set<String>> resultResources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groups)) {
            ResourceSearchBean resourceSearchBean = new ResourceSearchBean();
            resourceSearchBean.setDeepCopy(true);
            resourceSearchBean.setIncludeAccessRights(true);
            //collect group ids
            for (Group g : groups) {
                resourceSearchBean.setGroupIdSet(null);
                resourceSearchBean.addGroupId(g.getId());
                retVal.putAll(this.getResourceIds(resourceDataService.findBeans(resourceSearchBean, -1, -1, null)));
            }
        }

        if (CollectionUtils.isNotEmpty(roles)) {
            ResourceSearchBean resourceSearchBean = new ResourceSearchBean();
            resourceSearchBean.setDeepCopy(true);
            resourceSearchBean.setIncludeAccessRights(true);
            //collect group ids
            for (Role r : roles) {
                resourceSearchBean.setRoleIdSet(null);
                resourceSearchBean.addRoleId(r.getId());
                retVal.putAll(this.getResourceIds(resourceDataService.findBeans(resourceSearchBean, -1, -1, null)));
            }

        }

        if (CollectionUtils.isNotEmpty(resources)) {
            //collect group ids
            for (Resource r : resources) {
                retVal.put(r.getId(), null);
            }
        }
        retVal.putAll(processParentResources(retVal));
        resultResources.putAll(retVal);
    }

    private Map<String, Set<String>> processParentGroups(Map<String, Set<String>> childIds, Map<String, Set<String>> resultResources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (MapUtils.isNotEmpty(childIds)) {
            GroupSearchBean resourceSearchBean = new GroupSearchBean();
            resourceSearchBean.setIncludeAccessRights(true);
            resourceSearchBean.setDeepCopy(true);
            for (String s : childIds.keySet()) {
                resourceSearchBean.setChildIdSet(null);
                resourceSearchBean.addChildId(s);
                List<Group> groupList = groupDataWebService.findBeans(resourceSearchBean, null, -1, -1);
                retVal.putAll(this.getGroupIds(groupList));
                if (CollectionUtils.isNotEmpty(groupList)) {
                    this.getResources(null, new HashSet<Group>(groupList), null, resultResources);
                }
            }
            retVal.putAll(this.processParentGroups(retVal, resultResources));
        }
        return retVal;
    }

    private Map<String, Set<String>> processParentRoles(Map<String, Set<String>> childIds, Map<String, Set<String>> resultResources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (MapUtils.isNotEmpty(childIds)) {
            RoleSearchBean resourceSearchBean = new RoleSearchBean();
            resourceSearchBean.setIncludeAccessRights(true);
            resourceSearchBean.setDeepCopy(true);
            for (String s : childIds.keySet()) {
                resourceSearchBean.setChildIdSet(null);
                resourceSearchBean.addChildId(s);
                List<Role> groupList = roleDataService.findBeansDto(resourceSearchBean, null, -1, -1);
                retVal.putAll(this.getRoleIds(groupList));
                if (CollectionUtils.isNotEmpty(groupList)) {
                    this.getResources(null, null, new HashSet<Role>(groupList), resultResources);
                }
            }
            retVal.putAll(this.processParentGroups(retVal, resultResources));
        }
        return retVal;
    }

    private Map<String, Set<String>> processParentResources(Map<String, Set<String>> childIds) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (MapUtils.isNotEmpty(childIds)) {
            ResourceSearchBean resourceSearchBean = new ResourceSearchBean();
            resourceSearchBean.setIncludeAccessRights(true);
            resourceSearchBean.setDeepCopy(true);
            for (String s : childIds.keySet()) {
                resourceSearchBean.setChildIdSet(null);
                resourceSearchBean.addChildId(s);
                retVal.putAll(this.getResourceIds(resourceDataService.findBeans(resourceSearchBean, -1, -1, null)));
            }
            retVal.putAll(this.processParentResources(retVal));
        }
        return retVal;
    }

    private void getGroups(Set<Group> groups, Map<String, Set<String>> resultResources, Map<String, Set<String>> resultGroups) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groups)) {
            //collect group ids
            for (Group r : groups) {
                retVal.put(r.getId(), null);
            }
        }
        retVal.putAll(processParentGroups(retVal, resultResources));
        resultGroups.putAll(retVal);
    }

    private void getRoles(Set<Role> roles, Map<String, Set<String>> resultResources, Map<String, Set<String>> resultRoles) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            //collect group ids
            for (Role r : roles) {
                retVal.put(r.getId(), null);
            }
        }
        retVal.putAll(processParentGroups(retVal, resultResources));
        resultRoles.putAll(retVal);
    }


    private Map<String, Set<String>> getResourceIds(List<Resource> resources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(resources)) {
            for (Resource r : resources) {
                retVal.put(r.getId(), r.getAccessRightIds());
            }
        }
        return retVal;
    }

    private Map<String, Set<String>> getRoleIds(List<Role> resources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(resources)) {
            for (Role r : resources) {
                retVal.put(r.getId(), r.getAccessRightIds());
            }
        }
        return retVal;
    }

    private Map<String, Set<String>> getGroupIds(List<Group> resources) {
        Map<String, Set<String>> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(resources)) {
            for (Group r : resources) {
                retVal.put(r.getId(), null);
            }
        }
        return retVal;
    }

    private String getManagedSystemName(String id, List<ManagedSysEntity> managedSysEntityList) {
        String retVal = null;
        if (id != null && managedSysEntityList != null) {
            for (ManagedSysEntity e : managedSysEntityList) {
                if (e.getId().equals(id)) {
                    retVal = e.getName();
                    break;
                }
            }
        }
        return retVal;
    }

    private String getResourceTypeName(String id, List<ResourceType> managedSysEntityList) {
        String retVal = null;
        if (id != null && managedSysEntityList != null) {
            for (ResourceType e : managedSysEntityList) {
                if (e.getId().equals(id)) {
                    retVal = e.getDescription();
                    break;
                }
            }
        }
        return retVal;
    }

    private String getMetadataTypeName(String id, List<MetadataType> managedSysEntityList) {
        String retVal = null;
        if (id != null && managedSysEntityList != null) {
            for (MetadataType e : managedSysEntityList) {
                if (e.getId().equals(id)) {
                    retVal = e.getDescription();
                    break;
                }
            }
        }
        return retVal;
    }

    private String getAccessRightsName(String id, List<AccessRight> managedSysEntityList) {
        String retVal = null;
        if (id != null && managedSysEntityList != null) {
            for (AccessRight e : managedSysEntityList) {
                if (e.getId().equals(id)) {
                    retVal = e.getName();
                    break;
                }
            }
        }
        return retVal;
    }


    private void fillNamedTypes(Set<UserAccessControlMemberBean> entitlements, List<ManagedSysEntity> managedSysEntities, List<MetadataType> roleMetadataTypes, List<MetadataType> groupMetadataTypes, List<MetadataType> resourceMetadataTypes, List<ResourceType> resourceTypes, List<AccessRight> accessRights) {
//TODO
        if (CollectionUtils.isNotEmpty(entitlements)) {
            for (UserAccessControlMemberBean bean : entitlements) {
                if (StringUtils.isNotBlank(bean.getManagedSystem())) {
                    if (CollectionUtils.isNotEmpty(managedSysEntities)) {
                        bean.setManagedSystem(getManagedSystemName(bean.getManagedSystem(), managedSysEntities));
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "role".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(roleMetadataTypes)) {
                        bean.setMetadataType(this.getMetadataTypeName(bean.getMetadataType(), roleMetadataTypes));
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "group".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(groupMetadataTypes)) {
                        bean.setMetadataType(this.getMetadataTypeName(bean.getMetadataType(), groupMetadataTypes));
                    }
                }
                if (StringUtils.isNotBlank(bean.getMetadataType()) && "resource".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(resourceMetadataTypes)) {
                        bean.setMetadataType(this.getMetadataTypeName(bean.getMetadataType(), resourceMetadataTypes));
                    }
                }
                if (StringUtils.isNotBlank(bean.getType()) && "resource".equalsIgnoreCase(bean.getObjectType())) {
                    if (CollectionUtils.isNotEmpty(resourceTypes)) {
                        bean.setType(this.getResourceTypeName(bean.getType(), resourceTypes));
                    }
                }
                if (CollectionUtils.isNotEmpty(bean.getRights())) {
                    Set<String> namedRigths = new HashSet<>();
                    for (String accessRight : bean.getRights()) {
                        namedRigths.add(this.getAccessRightsName(accessRight, accessRights));
                    }
                    bean.setRights(namedRigths);
                } else {
                    bean.setRights(null);
                    bean.setBinaryLink(true);
                }

            }
        }
    }

    private Set<UserAccessControlMemberBean> processGroups(Map<String, Set<String>> groups, Map<String, AuthorizationGroup> groupMap, List<String> managedSystemFilter, List<String> metadataTypeFilter, String nameFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String groupKey : groups.keySet()) {
            AuthorizationGroup group = groupMap.get(groupKey);
            if (!processNameFilter(group.getName(), nameFilter)) {
                continue;
            }
            if (group != null && filtered(managedSystemFilter, group.getManagedSysId()) && filtered(metadataTypeFilter, group.getMetadataTypeId())) {
                UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                bean.setObjectType("group");
                bean.setManagedSystem(group.getManagedSysId());
                bean.setName(group.getName());
                bean.setMetadataType(group.getMetadataTypeId());
                bean.setRights(CollectionUtils.isEmpty(groups.get(groupKey)) ? null : groups.get(groupKey));
                directSet.add(bean);
            }
        }
        return directSet;
    }

    private Set<UserAccessControlMemberBean> processRoles(Map<String, Set<String>> roles, Map<String, AuthorizationRole> roleMap, List<String> managedSystemFilter, List<String> metadataTypeFilter, String nameFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String roleKey : roles.keySet()) {
            AuthorizationRole role = roleMap.get(roleKey);
            if (!processNameFilter(role.getName(), nameFilter)) {
                continue;
            }
            if (role != null && filtered(managedSystemFilter, role.getManagedSysId()) && filtered(metadataTypeFilter, role.getMetadataTypeId())) {
                UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                bean.setObjectType("role");
                bean.setManagedSystem(role.getManagedSysId());
                bean.setName(role.getName());
                bean.setMetadataType(role.getMetadataTypeId());
                bean.setRights(CollectionUtils.isEmpty(roles.get(roleKey)) ? null : roles.get(roleKey));
                directSet.add(bean);

            }
        }
        return directSet;
    }

    private boolean processNameFilter(String name, String filter) {
        boolean ok = false;
        if (StringUtils.isBlank(filter) || "null".equalsIgnoreCase(filter)) {
            ok = true;
        } else if (name == null) {
            ok = false;
        } else {
            if (StringUtils.indexOf(filter, "*") == 0) {
                ok = name.toLowerCase().endsWith(filter.substring(1).toLowerCase());
            } else if (StringUtils.indexOf(filter, "*") == filter.length() - 1) {
                ok = name.toLowerCase().startsWith(filter.substring(0, filter.length() - 1).toLowerCase());
            } else {
                ok = name.equalsIgnoreCase(filter);
            }

        }
        return ok;
    }


    private Set<UserAccessControlMemberBean> processResources(Map<String, Set<String>> resources, Map<String, AuthorizationResource> resourceMap, List<String> resourceTypeFilter, List<String> metadataTypeFilter, String nameFilter) {
        Set<UserAccessControlMemberBean> directSet = new HashSet<UserAccessControlMemberBean>();
        for (String resourceKey : resources.keySet()) {
            AuthorizationResource resource = resourceMap.get(resourceKey);
            if (!processNameFilter(resource.getName(), nameFilter)) {
                continue;
            }
            if (resource != null &&
                    filtered(resourceTypeFilter, resource.getResourceTypeId()) && filtered(metadataTypeFilter, resource.getMetadataTypeId())) {
                UserAccessControlMemberBean bean = new UserAccessControlMemberBean();
                bean.setObjectType("resource");
                bean.setMetadataType(resource.getMetadataTypeId());
                bean.setType(resource.getResourceTypeId());
                bean.setName(resource.getName());
                bean.setRights(CollectionUtils.isEmpty(resources.get(resourceKey)) ? null : resources.get(resourceKey));
                directSet.add(bean);

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
