/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.SupervisorDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Helper class that will be called by the DefaultProvisioningService to add
 * users in to the OpenIAM repository.
 * 
 * @author suneet
 * 
 */
public class AddUser {

    protected static final Log log = LogFactory.getLog(AddUser.class);

    protected RoleDataService roleDataService;
    protected GroupDataService groupManager;
    protected UserDataService userMgr;
    protected LoginDataService loginManager;
    protected SysConfiguration sysConfiguration;
    protected ResourceDataService resourceDataService;
    protected ManagedSystemWebService managedSysService;
    @Autowired
    protected AuditHelper auditHelper;
    protected OrganizationDataService orgManager;

    @Autowired
    private UserDozerConverter userDozerConverter;

    @Autowired
    private LoginDozerConverter loginDozerConverter;

    @Autowired
    private SupervisorDozerConverter supervisorDozerConverter;

    public ProvisionUserResponse createUser(ProvisionUser user,
            List<IdmAuditLog> logList) {

        ProvisionUserResponse resp = new ProvisionUserResponse();
        resp.setStatus(ResponseStatus.SUCCESS);
        ResponseCode code;

        User newUser = null;
        try {
            newUser = user.getUser();
            final UserEntity entity = userDozerConverter.convertToEntity(
                    newUser, true);
            userMgr.addUser(entity);
            newUser = userDozerConverter.convertToDTO(entity, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (newUser == null || newUser.getUserId() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        user.setUserId(newUser.getUserId());
        log.debug("User id=" + newUser.getUserId()
                + " created in openiam repository");

        code = addSupervisors(user);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }

        try {
            addPrincipals(user);
        } catch (EncryptionException e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
            return resp;
        }
        code = addGroups(user, newUser.getUserId(), logList);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        code = addRoles(user, newUser.getUserId(), logList);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        code = addAffiliations(user, newUser.getUserId(), logList);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }

        return resp;
    }

    private ResponseCode addSupervisors(ProvisionUser u) {
        Set<User> superiors = u.getSuperiors();
        if (CollectionUtils.isNotEmpty(superiors)) {
            for (User s : superiors) {
                try {
                    userMgr.addSuperior(s.getUserId(), u.getUserId());
                    log.info("created user supervisor");

                } catch (Exception e) {
                    return ResponseCode.SUPERVISOR_ERROR;
                }
            }
        }
        return ResponseCode.SUCCESS;
    }

    private void addPrincipals(ProvisionUser u) throws EncryptionException {
        List<Login> principalList = u.getPrincipalList();
        if (principalList != null && !principalList.isEmpty()) {
            for (Login lg : principalList) {
                lg.setFirstTimeLogin(1);
                lg.setIsLocked(0);
                lg.setCreateDate(new Date(System.currentTimeMillis()));
                lg.setUserId(u.getUserId());
                lg.setStatus("ACTIVE");
                // encrypt the password
                if (lg.getPassword() != null) {
                    String pswd = lg.getPassword();
                    lg.setPassword(loginManager.encryptPassword(u.getUserId(),
                            pswd));
                }
                loginManager.addLogin(loginDozerConverter.convertToEntity(lg,
                        true));
            }
        }

    }

    private ResponseCode addGroups(ProvisionUser user, String newUserId,
            List<IdmAuditLog> logList) {
        List<Group> groupList = user.getMemberOfGroups();

        if (groupList != null) {
            for (Group g : groupList) {
                // check if the group id is valid
                if (g.getGrpId() == null) {
                    return ResponseCode.GROUP_ID_NULL;
                }
                if (groupManager.getGroup(g.getGrpId()) == null) {
                    if (g.getGrpId() == null) {
                        return ResponseCode.GROUP_ID_NULL;
                    }
                }
                groupManager.addUserToGroup(g.getGrpId(), newUserId);
                // add to audit log
                logList.add(auditHelper.createLogObject("ADD GROUP",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER",
                        user.getUserId(), null, "SUCCESS", null, "USER_STATUS",
                        user.getUser().getStatus().toString(), null, null,
                        user.getSessionId(), null, g.getGrpName(),
                        user.getRequestClientIP(), null, null));

            }
        }
        return ResponseCode.SUCCESS;
    }

    private ResponseCode addRoles(ProvisionUser user, String newUserId,
            List<IdmAuditLog> logList) {
        List<Role> roleList = user.getMemberOfRoles();
        log.debug("Role list = " + roleList);
        if (roleList != null && roleList.size() > 0) {
            for (Role r : roleList) {
                // check if the roleId is valid
                if (r.getRoleId() == null) {
                    return ResponseCode.ROLE_ID_NULL;
                }
                if (roleDataService.getRole(r.getRoleId()) == null) {
                    return ResponseCode.ROLE_ID_INVALID;
                }
                roleDataService.addUserToRole(r.getRoleId(), newUserId);

                logList.add(auditHelper.createLogObject("ADD ROLE",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER",
                        user.getUserId(), null, "SUCCESS", null, "USER_STATUS",
                        user.getUser().getStatus().toString(), "NA", null,
                        user.getSessionId(), null, r.getRoleId(),
                        user.getRequestClientIP(), null, null));

            }
        }
        return ResponseCode.SUCCESS;
    }

    private ResponseCode addAffiliations(ProvisionUser user, String newUserId,
            List<IdmAuditLog> logList) {
        List<Organization> affiliationList = user.getUserAffiliations();
        log.debug("addAffiliations:Affiliation List list = " + affiliationList);
        if (affiliationList != null && affiliationList.size() > 0) {
            for (Organization org : affiliationList) {
                // check if the roleId is valid
                if (org.getId() == null) {
                    return ResponseCode.OBJECT_ID_INVALID;
                }
                orgManager.addUserToOrg(org.getId(), user.getUserId());

                logList.add(auditHelper.createLogObject("ADD AFFILIATION",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER",
                        user.getUserId(), null, "SUCCESS", null, "USER_STATUS",
                        user.getUser().getStatus().toString(), "NA", null,
                        user.getSessionId(), null, org.getOrganizationName(),
                        user.getRequestClientIP(), null, null));

            }
        }
        return ResponseCode.SUCCESS;
    }

    /**
     * Builds the list of principals from the policies that we have defined in
     * the groovy scripts.
     * 
     * @param user
     * @param bindingMap
     * @param se
     */
    public void buildPrimaryPrincipal(ProvisionUser user,
            Map<String, Object> bindingMap, ScriptIntegration se) {

        List<Login> principalList = new ArrayList<Login>();
        List<AttributeMap> policyAttrMap = this.managedSysService
                .getResourceAttributeMaps(sysConfiguration
                        .getDefaultManagedSysId());
        // List<AttributeMap> policyAttrMap =
        // resourceDataService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());

        log.debug("Building primary identity. ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity = new Login();
            EmailAddress primaryEmail = new EmailAddress();

            // init values
            primaryIdentity.setDomainId(sysConfiguration
                    .getDefaultSecurityDomain());
            primaryIdentity.setManagedSysId(sysConfiguration
                    .getDefaultManagedSysId());

            try {
                for (AttributeMap attr : policyAttrMap) {
                    try {
                        String output = (String)ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, se);
                        String objectType = attr.getMapForObjectType();
                        if (objectType != null) {
                            if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                                if (attr.getAttributeName().equalsIgnoreCase(
                                        "PRINCIPAL")) {
                                    primaryIdentity.setLogin(output);
                                }
                                if (attr.getAttributeName().equalsIgnoreCase(
                                        "PASSWORD")) {
                                    primaryIdentity.setPassword(output);
                                }
                                if (attr.getAttributeName().equalsIgnoreCase(
                                        "DOMAIN")) {
                                    primaryIdentity.setDomainId(output);
                                }
                            }
                            if (objectType.equals("EMAIL")) {
                                primaryEmail.setEmailAddress(output);
                                primaryEmail.setIsDefault(true);
                            }
                        }
                    } catch (ScriptEngineException e) {
                        log.error(e);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
            principalList.add(primaryIdentity);
            user.setPrincipalList(principalList);
            user.getEmailAddresses().add(primaryEmail);

        } else {
            log.debug("- policyAttrMap IS null");
        }

    }

    /**
     * when a request already contains an identity and password has not been
     * setup, this method generates a password based on our rules.
     * 
     * @param user
     * @param bindingMap
     * @param se
     */
    public void setPrimaryIDPassword(ProvisionUser user,
            Map<String, Object> bindingMap, ScriptIntegration se) {

        // this method should only be the called if the request already contains
        // 1 or more identities

        List<Login> principalList = user.getPrincipalList();
        List<AttributeMap> policyAttrMap = this.managedSysService
                .getResourceAttributeMaps(sysConfiguration
                        .getDefaultManagedSysId());
        // List<AttributeMap> policyAttrMap =
        // resourceDataService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());

        log.debug("setPrimaryIDPassword() ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity = user.getPrimaryPrincipal(sysConfiguration
                    .getDefaultManagedSysId());

            // Login primaryIdentity = new Login();
            // LoginId primaryID = new LoginId();
            // EmailAddress primaryEmail = new EmailAddress();

            // init values
            // primaryID.setDomainId(sysConfiguration.getDefaultSecurityDomain());
            // primaryID.setManagedSysId(sysConfiguration.getDefaultManagedSysId());

            try {
                for (AttributeMap attr : policyAttrMap) {
                    try {
                        String output = (String)ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, se);
                        String objectType = attr.getMapForObjectType();
                        if (objectType != null) {
                            if (objectType.equalsIgnoreCase("PRINCIPAL")) {

                                if (attr.getAttributeName().equalsIgnoreCase(
                                        "PASSWORD")) {
                                    primaryIdentity.setPassword(output);
                                }

                            }

                        }
                    } catch (ScriptEngineException e) {
                        log.error(e);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
            // primaryIdentity.setId(primaryID);
            // principalList.add(primaryIdentity);
            user.setPrincipalList(principalList);
            // user.getEmailAddress().add(primaryEmail);

        } else {
            log.debug("- policyAttrMap IS null");
        }

    }

    /**
     * If the user has selected roles that are in multiple domains, we need to
     * make sure that they identities for each of these domains
     * 
     * @param primaryIdentity
     * @param roleList
     */
    public void validateIdentitiesExistforSecurityDomain(Login primaryIdentity,
            List<Role> roleList) {
        if (roleList == null || roleList.isEmpty()) {
            return;
        }

        List<LoginEntity> identityList = loginManager
                .getLoginByUser(primaryIdentity.getUserId());

        for (Role r : roleList) {

            String secDomain = r.getServiceId();
            if (!identityInDomain(secDomain, identityList)) {
                addIdentity(secDomain, primaryIdentity);
            }

        }

    }

    private boolean identityInDomain(String secDomain,
            List<LoginEntity> identityList) {
        for (LoginEntity l : identityList) {
            if (l.getDomainId().equalsIgnoreCase(secDomain)) {
                return true;
            }

        }
        return false;

    }

    private void addIdentity(String secDomain, Login primaryIdentity) {
        if (loginManager.getLoginByManagedSys(secDomain,
                primaryIdentity.getLogin(), primaryIdentity.getManagedSysId()) == null) {

            LoginEntity newLg = new LoginEntity();
            newLg.setDomainId(secDomain);
            newLg.setLogin(primaryIdentity.getLogin());
            newLg.setManagedSysId(primaryIdentity.getManagedSysId());
            newLg.setAuthFailCount(0);
            newLg.setFirstTimeLogin(primaryIdentity.getFirstTimeLogin());
            newLg.setIsLocked(primaryIdentity.getIsLocked());
            newLg.setLastAuthAttempt(primaryIdentity.getLastAuthAttempt());
            newLg.setGracePeriod(primaryIdentity.getGracePeriod());
            newLg.setPassword(primaryIdentity.getPassword());
            newLg.setPasswordChangeCount(primaryIdentity
                    .getPasswordChangeCount());
            newLg.setStatus(primaryIdentity.getStatus());
            newLg.setIsLocked(primaryIdentity.getIsLocked());
            newLg.setUserId(primaryIdentity.getUserId());
            newLg.setResetPassword(primaryIdentity.getResetPassword());

            log.debug("Adding identity = " + newLg);

            loginManager.addLogin(newLg);
        }

    }

    public RoleDataService getRoleDataService() {
        return roleDataService;
    }

    public void setRoleDataService(RoleDataService roleDataService) {
        this.roleDataService = roleDataService;
    }

    public GroupDataService getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(GroupDataService groupManager) {
        this.groupManager = groupManager;
    }

    public UserDataService getUserMgr() {
        return userMgr;
    }

    public void setUserMgr(UserDataService userMgr) {
        this.userMgr = userMgr;
    }

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    public SysConfiguration getSysConfiguration() {
        return sysConfiguration;
    }

    public void setSysConfiguration(SysConfiguration sysConfiguration) {
        this.sysConfiguration = sysConfiguration;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    public ManagedSystemWebService getManagedSysService() {
        return managedSysService;
    }

    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    public AuditHelper getAuditHelper() {
        return auditHelper;
    }

    public void setAuditHelper(AuditHelper auditHelper) {
        this.auditHelper = auditHelper;
    }

    public OrganizationDataService getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrganizationDataService orgManager) {
        this.orgManager = orgManager;
    }
}
