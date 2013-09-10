/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.classic.Session;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.*;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationCode;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.AccountLockEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.*;

/**
 * DefaultProvisioningService is responsible for receiving and processing
 * requests that are to be sent to the target system connectors.
 * 
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.provision.service.ProvisionService", targetNamespace = "http://www.openiam.org/service/provision", portName = "DefaultProvisionControllerServicePort", serviceName = "ProvisioningService")
@Component("defaultProvision")
public class DefaultProvisioningService extends AbstractProvisioningService {

    @Autowired
    private DeprovisionSelectedResourceHelper deprovisionSelectedResource;
    @Autowired
    @Qualifier("disableUser")
    private DisableUserDelegate disableUser;

    @Autowired
    HibernateTemplate hibernateTemplate;

    private static final Log log = LogFactory
            .getLog(DefaultProvisioningService.class);

    public Response testConnectionConfig(String managedSysId) {
        return validateConnectionConfig.testConnection(managedSysId,
                MuleContextProvider.getCtx());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#addUser(org.openiam.provision
     * .dto.ProvisionUser)
     */
    @Override
    @Transactional
    public ProvisionUserResponse addUser(ProvisionUser user) {
        ProvisionUserResponse resp = new ProvisionUserResponse();

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        Organization org = null;
        IdmAuditLog auditLog = null;
        boolean connectorSuccess = true;
        List<IdmAuditLog> pendingLogItems = new ArrayList<IdmAuditLog>();
        String requestId = "R" + UUIDGen.getUUID();

        // flag to determine if we should provision this user in target systems
        boolean provInTargetSystemNow = true;

        // determine if we provision now or in the future
        // if its in the future then we wont put the user in the target systems
        provInTargetSystemNow = provisionUserNow(user);
        if (!provInTargetSystemNow) {
            // start date is in the future
            // flag says that we should prov after the startdate
            user.setStatus(UserStatusEnum.PENDING_START_DATE);
        }

        if (user.getPrimaryOrganization() != null) {
            org = orgManager.getOrganization(user.getPrimaryOrganization()
                    .getId(), null);
        }

        // bind the objects to the scripting engine

        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("user", user);
        bindingMap.put("org", org);
        bindingMap.put("operation", "ADD");
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, null);
        // run the pre-processor before the body of the add operation
        if (callPreProcessor("ADD", user, bindingMap) != ProvisioningConstants.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return resp;
        }

        if (user.getStatus() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.MISSING_REQUIRED_ATTRIBUTE);
            return resp;
        }

        // make sure that our object as the attribute set that will be used for
        // audit logging
        checkAuditingAttributes(user);

        // CREATE THE PRIMARY IDENTITY IF IT HAS NOT BEEN PASSED IN

        boolean customPassword = false;
        Login primaryLogin = null;

        if (CollectionUtils.isEmpty(user.getPrincipalList())) {
            // build the list
            buildPrimaryPrincipal(user, bindingMap, scriptRunner);

        } else {
            primaryLogin = user.getPrimaryPrincipal(sysConfiguration
                    .getDefaultManagedSysId());
            // Check if a custom password is set
            if (StringUtils.isNotBlank(primaryLogin.getPassword())) {
                customPassword = true;
            } else {
                setPrimaryIDPassword(user, bindingMap, scriptRunner);
            }
        }

        if (primaryLogin == null) {
            primaryLogin = user.getPrimaryPrincipal(sysConfiguration
                    .getDefaultManagedSysId());
        }

        // check if there is a custom password provided in the request
        if (StringUtils.isNotBlank(user.getPassword())) {
            customPassword = true;
            primaryLogin.setPassword(user.getPassword());
        }
        // check if there is a custom login provided in the request
        if (StringUtils.isNotBlank(user.getLogin())) {
            primaryLogin.setLogin(user.getLogin());
        }

        Policy passwordPolicy = user.getPasswordPolicy();
        if (passwordPolicy == null) {
            passwordPolicy = passwordManager.getPasswordPolicyByUser(
                    primaryLogin.getDomainId(),
                    userDozerConverter.convertToEntity(user.getUser(), true));
        }

        // if the password of the primaryIdentity is a custom password validate
        // the password
        if (customPassword) {
            Password password = new Password();
            password.setDomainId(primaryLogin.getDomainId());
            password.setManagedSysId(primaryLogin.getManagedSysId());
            password.setPassword(primaryLogin.getPassword());
            password.setPrincipal(primaryLogin.getLogin());

            try {
                PasswordValidationCode valCode = passwordManager
                        .isPasswordValidForUserAndPolicy(password,
                                userDozerConverter.convertToEntity(
                                        user.getUser(), true),
                                loginDozerConverter.convertToEntity(
                                        primaryLogin, true), passwordPolicy);
                if (valCode == null
                        || valCode != PasswordValidationCode.SUCCESS) {
                    auditHelper.addLog("CREATE", user.getRequestorDomain(),
                            user.getRequestorLogin(), "IDM SERVICE",
                            user.getCreatedBy(), "0", "USER", user.getUserId(),
                            null, "FAIL", null, "USER_STATUS", user.getUser()
                                    .getStatus().toString(), requestId,
                            ResponseCode.FAIL_DECRYPTION.toString(),
                            user.getSessionId(), "Password validation failed",
                            user.getRequestClientIP(), primaryLogin.getLogin(),
                            primaryLogin.getDomainId());

                    resp.setStatus(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
                    return resp;
                }
            } catch (ObjectNotFoundException e) {
                auditHelper.addLog("CREATE", user.getRequestorDomain(),
                        user.getRequestorLogin(), "IDM SERVICE",
                        user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "FAIL", null, "USER_STATUS", user.getUser()
                                .getStatus().toString(), requestId,
                        ResponseCode.FAIL_DECRYPTION.toString(),
                        user.getSessionId(), e.toString(),
                        user.getRequestClientIP(), primaryLogin.getLogin(),
                        primaryLogin.getDomainId());

                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
                return resp;
            }
        }

        // validate that this identity does not already exist
        LoginEntity dupPrincipal = loginManager.getLoginByManagedSys(
                primaryLogin.getDomainId(), primaryLogin.getLogin(),
                primaryLogin.getManagedSysId());

        if (dupPrincipal != null) {
            // identity exists

            auditHelper.addLog("CREATE", user.getRequestorDomain(),
                    user.getRequestorLogin(), "IDM SERVICE",
                    user.getCreatedBy(), "0", "USER", user.getUserId(), null,
                    "FAIL", null, "USER_STATUS", user.getStatus()
                            .toString(), requestId, "DUPLICATE PRINCIPAL",
                    user.getSessionId(), "Identity already exists:"
                            + primaryLogin.getManagedSysId() + " - "
                            + primaryLogin.getLogin(),
                    user.getRequestClientIP(), primaryLogin.getLogin(),
                    primaryLogin.getDomainId());

            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.DUPLICATE_PRINCIPAL);
            return resp;

        }
        // identity passed isDuplicate check

        /* Create the new user in the openiam repository */
        resp = createUser(user, pendingLogItems);

        if (resp.getStatus() == ResponseStatus.SUCCESS) {
            user = resp.getUser();
            /*
             * auditLog = auditHelper.addLog("CREATE",
             * user.getRequestorDomain(), user.getRequestorLogin(),
             * "IDM SERVICE", user .getCreatedBy(), "0", "USER",
             * user.getUserId(), null, "SUCCESS", null, "USER_STATUS",
             * user.getUser() .getStatus().toString(), requestId, null, user
             * .getSessionId(), null, user.getRequestClientIP(),
             * primaryLogin.getLogin(), primaryLogin.getDomainId());
             * auditHelper.persistLogList(pendingLogItems, requestId,
             * user.getSessionId());
             */
        } else {
            /*
             * auditLog = auditHelper.addLog("CREATE",
             * user.getRequestorDomain(), user.getRequestorLogin(),
             * "IDM SERVICE", user .getCreatedBy(), "0", "USER",
             * user.getUserId(), null, "FAIL", null, "USER_STATUS",
             * user.getUser() .getStatus().toString(), requestId, resp
             * .getErrorCode().toString(), user.getSessionId(),
             * resp.getErrorText(), user.getRequestClientIP(), (primaryLogin !=
             * null ? primaryLogin.getLogin() : ""), (primaryLogin != null ?
             * primaryLogin.getDomainId() : ""));
             */
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_DECRYPTION);
            return resp;
        }

        primaryLogin = user.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
        // need decrypted password for use in the connectors:
        String decPassword = null;
        try {
            decPassword = loginManager.decryptPassword(
                    primaryLogin.getUserId(), primaryLogin.getPassword());
        } catch (EncryptionException e) {

            auditHelper.addLog("CREATE", user.getRequestorDomain(),
                    user.getRequestorLogin(), "IDM SERVICE",
                    user.getCreatedBy(), "0", "USER", user.getUserId(), null,
                    "FAIL", null, "USER_STATUS", user.getStatus()
                            .toString(), requestId,
                    ResponseCode.FAIL_DECRYPTION.toString(),
                    user.getSessionId(), e.toString(),
                    user.getRequestClientIP(), primaryLogin.getLogin(),
                    primaryLogin.getDomainId());

            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_DECRYPTION);
            return resp;
        }
        bindingMap.put("lg", primaryLogin);
        bindingMap.put("password", decPassword);

        // if the add password to history flag is on, then add this password to
        // the history so that its not used again

        if (user.isAddInitialPasswordToHistory() || customPassword) {
            // add the auto generated password to the history so that the user
            // can not use this password as their first password
            PasswordHistoryEntity hist = new PasswordHistoryEntity();
            hist.setDateCreated(new Date());
            hist.setLoginId(primaryLogin.getLoginId());
            hist.setPassword(primaryLogin.getPassword());
            passwordHistoryDao.save(hist);
        }

        // Update attributes that will be used by the password policy
        passwordPolicy = passwordManager.getPasswordPolicy(
                primaryLogin.getDomainId(), primaryLogin.getLogin(),
                primaryLogin.getManagedSysId());
        PolicyAttribute policyAttr = getPolicyAttribute("CHNG_PSWD_ON_RESET",
                passwordPolicy);
        if (policyAttr != null) {
            // don't force the user to immediately change it's own password
            if (policyAttr.getValue1().equalsIgnoreCase("1") && !customPassword) {
                primaryLogin.setResetPassword(1);
            } else {
                primaryLogin.setResetPassword(0);
            }
            // determin the password expiration and grace period dates
            setPasswordExpValues(passwordPolicy, primaryLogin);

            loginManager.updateLogin(loginDozerConverter.convertToEntity(
                    primaryLogin, true));

        } else {
            log.warn("Can't find CHNG_PWD_ON_RESET password policy - using false as default.  Please fix this in the Admin UI");
        }

        // provision the user into the systems that they should have access to.
        // get the list of resources for each role that user belongs too.

        bindingMap.put("userRole", user.getMemberOfRoles());

        if (provInTargetSystemNow) {
            Set<Role> resourceSet = (user.getMemberOfRoles() != null) ?
                    new HashSet<Role>(user.getMemberOfRoles()) : new HashSet<Role>();
            List<Resource> resourceList = new LinkedList<Resource>(getResourcesForRoles(resourceSet));

            // update the resource list to include the resources that have been
            // added directly
            if (resourceList == null) {

                resourceList = new ArrayList<Resource>();
            }

            addDirectResourceAssociation(user, resourceList);

            if (resourceList != null && !resourceList.isEmpty()) {
                for (Resource res : resourceList) {
                    // this try-catch block for protection other operations and other resources if one resource was fall with error
                    try {
                        log.debug("Resource->managedSysId ="
                                + res.getManagedSysId());
                        log.debug("Resource->resourceId =" + res.getResourceId());

                        String managedSysId = res.getManagedSysId();

                        if (managedSysId != null && managedSysId.length() > 0) {

                            bindingMap.put(TARGET_SYS_RES_ID, res.getResourceId());
                            bindingMap.put(TARGET_SYS_MANAGED_SYS_ID,
                                    res.getManagedSysId());

                            // object that will be sent to the connectors
                            List<AttributeMap> attrMap = managedSysService
                                    .getResourceAttributeMaps(res.getResourceId());
                            // List<AttributeMap> attrMap =
                            // resourceDataService.getResourceAttributeMaps(res.getResourceId());

                            ManagedSysDto mSys = managedSysService
                                    .getManagedSys(managedSysId);

                            log.debug("Managed sys =" + mSys);

                            ProvisionConnectorDto connector = provisionConnectorWebService
                                    .getProvisionConnector(mSys.getConnectorId());

                            ManagedSystemObjectMatch matchObj = null;
                            ManagedSystemObjectMatch[] matchObjAry = managedSysService
                                    .managedSysObjectParam(managedSysId, "USER");
                            if (matchObjAry != null && matchObjAry.length > 0) {
                                matchObj = matchObjAry[0];
                                bindingMap.put(MATCH_PARAM, matchObj);
                            }

                            Map<String, String> curValueMap = new HashMap<String, String>();
                            // Get Resource/MngSys identity
                            Login resLogin = getPrincipalForManagedSys(
                                    managedSysId, user.getPrincipalList());

                            boolean mngSysIdentityExists = resLogin != null;
                            if (!mngSysIdentityExists) {
                                log.debug(" - Building principal Name for: "
                                        + managedSysId);
                                // build the primary identity for resource by
                                // resource mapping
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS,
                                        IDENTITY_NEW);
                                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN,
                                        mngSysIdentityExists ? primaryLogin.getDomainId() : null);

                                log.debug(" - Building principal Name for: "
                                        + managedSysId);
                                // build the primary identity
                                String newPrincipalName = null;
                                try {
                                    newPrincipalName = ProvisionServiceUtil
                                            .buildPrincipalName(attrMap,
                                                    scriptRunner, bindingMap);
                                } catch (ScriptEngineException e) {
                                    log.error(e);
                                }
                                log.debug(" - New principalName = "
                                        + newPrincipalName);

                                // get the current object as it stands in the target
                                // system
                                resLogin = new Login();
                                resLogin.setLogin(newPrincipalName);
                                resLogin.setDomainId(primaryLogin.getDomainId());
                                resLogin.setManagedSysId(managedSysId);
                                resLogin.setPassword(primaryLogin.getPassword());
                                resLogin.setUserId(primaryLogin.getUserId());
                            }
                            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS,
                                    mngSysIdentityExists ? IDENTITY_NEW : IDENTITY_EXIST);
                            bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, null);
                            bindingMap.put(
                                    TARGET_SYSTEM_IDENTITY,
                                    mngSysIdentityExists ? resLogin
                                            .getLogin() : null);

                            // what the new object will look like
                            ExtensibleUser extUser = buildFromRules(user,
                                    resLogin, attrMap, scriptRunner,
                                    bindingMap);


                            boolean userExistedInTargetSystem = getCurrentObjectAtTargetSystem(
                                    resLogin, extUser, mSys, connector, matchObj,
                                    curValueMap);

                            if (!userExistedInTargetSystem) {
                                if (curValueMap == null || curValueMap.size() == 0) {
                                    // we may have identity for a user, but it my
                                    // have
                                    // been deleted from the target system
                                    // we dont need re-generate the identity in this
                                    // c
                                    bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS,
                                            IDENTITY_NEW);
                                    bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, null);
                                } else {
                                    bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS,
                                            IDENTITY_EXIST);
                                    bindingMap.put(TARGET_SYSTEM_ATTRIBUTES,
                                            curValueMap);
                                }

                                bindingMap.put(TARGET_SYSTEM_IDENTITY,
                                        resLogin.getLogin());
                                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN,
                                        resLogin.getDomainId());

                                // pre-processing
                                String preProcessScript = getResProperty(
                                        res.getResourceProps(), "PRE_PROCESS");
                                if (preProcessScript != null
                                        && !preProcessScript.isEmpty()) {
                                    PreProcessor ppScript = createPreProcessScript(
                                            preProcessScript, bindingMap);
                                    if (ppScript != null) {
                                        if (executePreProcess(ppScript, bindingMap,
                                                user, "ADD") == ProvisioningConstants.FAIL) {
                                            continue;
                                        }
                                    }
                                }

                                List<Login> priList = user.getPrincipalList();
                                if (priList != null) {
                                    for (Login l : priList) {
                                        log.debug("identity after builder="
                                                + l.getLoginId());
                                    }
                                } else {
                                    log.debug("priList is null");
                                }

                                log.debug("Creating identity in openiam repository:"
                                        + resLogin.getLoginId());

                                // validate if the identity exists in the system
                                // first

                                connectorSuccess = callConnector(resLogin,
                                        requestId, mSys, matchObj, extUser,
                                        connector, user, auditLog);

                                // only put the identity into the openiam repository
                                // if
                                // we successfully created the identity
                                if (connectorSuccess) {

                                    if (!mngSysIdentityExists) {
                                        loginManager.addLogin(loginDozerConverter
                                                .convertToEntity(resLogin, true));

                                    } else {
                                        log.debug("Skipping the creation of identity in openiam repository. Identity already exists"
                                                + resLogin.getLoginId());
                                    }

                                }

                                // post processing
                                String postProcessScript = getResProperty(
                                        res.getResourceProps(), "POST_PROCESS");
                                if (postProcessScript != null
                                        && !postProcessScript.isEmpty()) {
                                    PostProcessor ppScript = createPostProcessScript(
                                            postProcessScript, bindingMap);
                                    if (ppScript != null) {
                                        executePostProcess(ppScript, bindingMap,
                                                user, "ADD", connectorSuccess);
                                    }
                                }
                            } else {
                                // existing identity in target system

                                log.debug("Building attributes for managedSysId ="
                                        + managedSysId);

                                log.debug("identity for managedSys is not null "
                                        + resLogin.getLogin());


                                bindingMap.put(TARGET_SYSTEM_ATTRIBUTES,
                                        curValueMap);

                                String preProcessScript = getResProperty(
                                        res.getResourceProps(), "PRE_PROCESS");
                                if (preProcessScript != null
                                        && !preProcessScript.isEmpty()) {
                                    PreProcessor ppScript = createPreProcessScript(
                                            preProcessScript, bindingMap);
                                    if (ppScript != null) {
                                        if (executePreProcess(ppScript, bindingMap,
                                                user, "MODIFY") == ProvisioningConstants.FAIL) {
                                            continue;
                                        }
                                    }
                                }


                                // updates the attributes with the correct operation
                                // codes
                                extUser = updateAttributeList(extUser, curValueMap);

                                // test to see if the updates were carried for
                                // forward
                                List<ExtensibleAttribute> extAttList = extUser
                                        .getAttributes();
                                //

                                connectorSuccess = false;
                                if (connector.getConnectorInterface() != null
                                        && connector.getConnectorInterface()
                                                .equalsIgnoreCase("REMOTE")) {

                                    if (resLogin.getOperation() == AttributeOperationEnum.REPLACE
                                            && resLogin.getOrigPrincipalName() != null) {
                                        extAttList.add(new ExtensibleAttribute(
                                                "ORIG_IDENTITY", resLogin
                                                        .getOrigPrincipalName(), 2,
                                                "String"));
                                    }

                                    CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
                                    userReq.setObjectIdentity(resLogin.getLogin());
                                    userReq.setRequestID(requestId);
                                    userReq.setTargetID(resLogin.getManagedSysId());
                                    userReq.setHostLoginId(mSys.getUserId());
                                    String passwordDecoded = mSys.getPswd();
                                    try {
                                        passwordDecoded = getDecryptedPassword(mSys);
                                    } catch (ConnectorDataException e) {
                                        e.printStackTrace();
                                    }
                                    userReq.setHostLoginPassword(passwordDecoded);
                                    userReq.setHostUrl(mSys.getHostUrl());
                                    userReq.setBaseDN(matchObj.getBaseDn());
                                    userReq.setOperation("EDIT");
                                    userReq.setExtensibleObject(extUser);

                                    userReq.setScriptHandler(mSys
                                            .getModifyHandler());

                                    ObjectResponse respType = remoteConnectorAdapter
                                            .modifyRequest(mSys, userReq,
                                                    connector, MuleContextProvider.getCtx());
                                    if (respType.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }

                                } else {
                                    CrudRequest<ExtensibleUser> modReqType = new CrudRequest<ExtensibleUser>();

                                    modReqType.setTargetID(resLogin.getManagedSysId());
                                    modReqType.setObjectIdentity(resLogin.getLogin());
                                    modReqType.setRequestID(requestId);
                                    modReqType.setExtensibleObject(extUser);

                                    // check if this request calls for the identity
                                    // being renamed
                                    log.debug("Send request to connector - Original Principal Name = "
                                            + resLogin.getOrigPrincipalName());

                                    if (resLogin.getOrigPrincipalName() != null) {
                                        extAttList.add(new ExtensibleAttribute(
                                                "ORIG_IDENTITY", resLogin
                                                        .getOrigPrincipalName(), 2,
                                                "String"));

                                        // if
                                        // (mLg.getOrigPrincipalName().equalsIgnoreCase(mLg.getId().getLogin()))
                                        // {
                                        // extAttList.add(new
                                        // ExtensibleAttribute("ORIG_IDENTITY",
                                        // mLg.getOrigPrincipalName(), 2,
                                        // "String"));
                                        // }

                                    }


                                    log.debug("Creating identity in target system:"
                                            + resLogin.getLoginId());
                                    ObjectResponse respType = connectorAdapter
                                            .modifyRequest(mSys, modReqType,
                                                    MuleContextProvider.getCtx());

                                    if (respType.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }

                                }
                                // post processing
                                String postProcessScript = getResProperty(
                                        res.getResourceProps(), "POST_PROCESS");
                                if (postProcessScript != null
                                        && !postProcessScript.isEmpty()) {
                                    PostProcessor ppScript = createPostProcessScript(
                                            postProcessScript, bindingMap);
                                    if (ppScript != null) {
                                        executePostProcess(ppScript, bindingMap,
                                                user, "MODIFY", connectorSuccess);
                                    }
                                }
                            }
                            bindingMap.remove(MATCH_PARAM);
                        }
                    }catch(Throwable tw){
                       log.error(res,tw);
                    }
                }

            }
        }

        /* Response object */
        if (!connectorSuccess) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_CONNECTOR);
        } else {

            if (user.isEmailCredentialsToNewUsers()) {
                sendCredentialsToUser(user, primaryLogin.getLogin(),
                        decPassword);
            }
            if (user.isEmailCredentialsToSupervisor()) {
                if (user.getSuperiors() != null) {
                    Set<User> superiors = user.getSuperiors();
                    if (CollectionUtils.isNotEmpty(superiors)) {
                        for (User s : superiors) {
                            sendCredentialsToSupervisor(
                                    s,
                                    primaryLogin.getLogin(),
                                    decPassword,
                                    user.getFirstName() + " "
                                            + user.getLastName());
                        }
                    }
                }

                resp.setStatus(ResponseStatus.SUCCESS);
            }

            bindingMap.put("userAfterAdd", user);

            // call the post processor

            if (callPostProcessor("ADD", user, bindingMap) != ProvisioningConstants.SUCCESS) {
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                return resp;
            }
        }
        resp.setUser(user);
        return resp;
    }

    /**
     * Determine when we are going to provision a user
     * 
     * @param user
     * @return
     */
    private boolean provisionUserNow(ProvisionUser user) {

        // if no start is provided then we can assume that we want to provision
        // the user now. There is no future provisioning date
        // if a date is provided and its in the future, then provision it later

        Date curDate = new Date(System.currentTimeMillis());
        Date startDate = user.getStartDate();

        if (startDate == null) {
            // no startDate specified = assume that we can provision now
            return true;
        }

        if (!user.isProvisionOnStartDate()) {
            return true;
        }

        return !curDate.before(startDate);

    }

    private void setPasswordExpValues(Policy plcy, Login lg) {
        Calendar cal = Calendar.getInstance();
        Calendar expCal = Calendar.getInstance();

        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION");
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        // password has been changed - we dont need to force a change password
        // on the next login

        // calculate when the password will expire
        if (pswdExpValue != null && !pswdExpValue.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(pswdExpValue));
            expCal.add(Calendar.DATE, Integer.parseInt(pswdExpValue));
            lg.setPwdExp(expCal.getTime());

            // calc the grace period if there is a policy for it
            if (gracePeriod != null && !gracePeriod.isEmpty()) {
                cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
                lg.setGracePeriod(cal.getTime());
            }
        }

    }

    private String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        log.debug("Attribute Set size=" + attr.size());

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;

    }

    private PolicyAttribute getPolicyAttribute(String attributeName,
            Policy policy) {
        if (policy == null) {
            return null;
        }
        PolicyAttribute attribute = policy.getAttribute(attributeName);
        if (attribute == null || attribute.getValue1() == null
                || attribute.getValue1().length() == 0) {
            return null;
        }
        return attribute;
    }

    private LoginEntity getPrincipalForManagedSys(String mSys, List<LoginEntity> principalList) {
        if (CollectionUtils.isEmpty(principalList)) {
            return null;
        }
        for (LoginEntity l : principalList) {
            if (mSys != null) {
                if (l.getManagedSysId().equalsIgnoreCase(mSys)) {
                    return l;
                }
            }
        }
        return null;
    }

    private Login getPrincipalForManagedSys(String mSys,
            List<Login> principalList) {
        if (principalList == null) {
            return null;
        }
        for (Login l : principalList) {
            if (mSys != null) {
                if (l.getManagedSysId().equalsIgnoreCase(mSys)) {
                    return l;
                }
            }
        }
        return null;

    }

    @Override
    @Transactional
    public ProvisionUserResponse deleteByUserId(ProvisionUser user,
            UserStatusEnum status, String requestorId) {

        log.debug("----deleteByUserId called.------");

        List<LoginEntity> loginEntityList = loginManager.getLoginByUser(user.getUserId());
        LoginEntity primaryIdentity = getPrimaryIdentity(
                this.sysConfiguration.getDefaultManagedSysId(),
                loginDozerConverter.convertToEntityList(loginDozerConverter.convertToDTOList(loginEntityList,false), false));

        return deleteUser(primaryIdentity.getDomainId(), sysConfiguration
                .getDefaultManagedSysId(), primaryIdentity.getLogin(),status,requestorId);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#deleteUser(java.lang.String
     * , java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ProvisionUserResponse deleteUser(String securityDomain,
            String managedSystemId, String principal, UserStatusEnum status,
            String requestorId) {
        log.debug("----deleteUser called.------");

        IdmAuditLog auditLog = null;

        ProvisionUserResponse response = new ProvisionUserResponse(
                ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        if (status != UserStatusEnum.DELETED && status != UserStatusEnum.LEAVE
                && status != UserStatusEnum.TERMINATE
                && status != UserStatusEnum.RETIRED) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            return response;
        }

        String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        final LoginEntity login = loginManager.getLoginByManagedSys(
                securityDomain, principal, managedSystemId);
        if (login == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        String userId = login.getUserId();
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        UserEntity entity = userMgr.getUser(userId);
        User usr = userDozerConverter.convertToDTO(entity, true);
        if (usr == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        ProvisionUser pUser = new ProvisionUser(usr);
        // SET PRE ATTRIBUTES FOR DEFAULT SYS SCRIPT
        bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSystemId);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
        bindingMap.put(TARGET_SYS_RES_ID, null);
        bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

        if (callPreProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }

        if (usr.getStatus() == UserStatusEnum.DELETED
                || usr.getStatus() == UserStatusEnum.TERMINATE) {
            log.debug("User was already deleted. Nothing more to do.");
            return response;
        }

        if (!managedSystemId.equalsIgnoreCase(sysConfiguration
                .getDefaultManagedSysId())) {
            // managedSysId point to one of the seconardary identities- just
            // terminate that identity
            login.setStatus("INACTIVE");
            login.setAuthFailCount(0);
            login.setPasswordChangeCount(0);
            login.setIsLocked(0);
            loginManager.updateLogin(login);
            // call delete on the connector
            ManagedSysDto mSys = managedSysService
                    .getManagedSys(managedSystemId);

            ProvisionConnectorDto connector = provisionConnectorWebService
                    .getProvisionConnector(mSys.getConnectorId());

            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService
                    .managedSysObjectParam(mSys.getManagedSysId(), "USER");

            // pre-processing

            Resource res = null;
            String resourceId = mSys.getResourceId();

            bindingMap.put("IDENTITY", login);
            bindingMap.put("RESOURCE", res);
            bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
            bindingMap.put(TARGET_SYS_RES_ID, resourceId);
            bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

            if (resourceId != null) {
                res = resourceDataService.getResource(resourceId);
                if (res != null) {
                    String preProcessScript = getResProperty(
                            res.getResourceProps(), "PRE_PROCESS");
                    if (preProcessScript != null && !preProcessScript.isEmpty()) {
                        PreProcessor ppScript = createPreProcessScript(
                                preProcessScript, bindingMap);
                        if (ppScript != null) {
                            executePreProcess(ppScript, bindingMap, pUser,
                                    "DELETE");
                        }
                    }
                }
            }

            boolean connectorSuccess = false;

            if (connector.getConnectorInterface() != null
                    && connector.getConnectorInterface().equalsIgnoreCase(
                            "REMOTE")) {
                ObjectResponse resp = remoteDelete(
                        loginDozerConverter.convertToDTO(login, true),
                        requestId, mSys, connector, matchObj,
                        new ProvisionUser(usr), auditLog);
                if (resp.getStatus() == StatusCodeType.SUCCESS) {
                    connectorSuccess = true;
                }
            } else {

                ResponseType resp = localDelete(loginDozerConverter.convertToDTO(login, true),
                        requestId, mSys, auditLog);

                if (resp.getStatus() == StatusCodeType.SUCCESS) {
                    connectorSuccess = true;
                }
            }
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
            String postProcessScript = getResProperty(res.getResourceProps(),
                    "POST_PROCESS");
            if (postProcessScript != null && !postProcessScript.isEmpty()) {
                PostProcessor ppScript = createPostProcessScript(
                        postProcessScript, bindingMap);
                if (ppScript != null) {
                    executePostProcess(ppScript, bindingMap, pUser, "DELETE",
                            connectorSuccess);
                }
            }

        } else {
            // delete user and all its identities.
            usr.setStatus(status);
            usr.setSecondaryStatus(null);
            usr.setLastUpdatedBy(requestorId);
            usr.setLastUpdate(new Date(System.currentTimeMillis()));
            entity = userDozerConverter.convertToEntity(usr, true);
            userMgr.updateUserWithDependent(entity, false);

            LoginEntity lRequestor = loginManager
                    .getPrimaryIdentity(requestorId);
            LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

            if (lRequestor != null && lTargetUser != null) {

                auditLog = auditHelper.addLog("DELETE",
                        lRequestor.getDomainId(), lRequestor.getLogin(),
                        "IDM SERVICE", usr.getCreatedBy(), "0", "USER",
                        usr.getUserId(), null, "SUCCESS", null, "USER_STATUS",
                        usr.getStatus().toString(), requestId, null, null,
                        null, null, lTargetUser.getLogin(),
                        lTargetUser.getDomainId());
            } else {
                log.debug("Unable to log disable operation. Of of the following is null:");
                log.debug("Requestor identity=" + lRequestor);
                log.debug("Target identity=" + lTargetUser);
            }

            // update the identities and set them to inactive
            List<LoginEntity> principalList = loginManager
                    .getLoginByUser(userId);
            if (principalList != null) {
                for (LoginEntity l : principalList) {
                    // this try-catch block for protection other operations and other resources if one resource was fall with error
                    try {
                        if (l.getStatus() != null
                                && !l.getStatus().equalsIgnoreCase("INACTIVE")) {
                            l.setStatus("INACTIVE");
                            l.setAuthFailCount(0);
                            l.setPasswordChangeCount(0);
                            l.setIsLocked(0);
                            loginManager.updateLogin(l);

                            // only add the connectors if its a secondary identity.
                            if (!l.getManagedSysId().equalsIgnoreCase(
                                    this.sysConfiguration.getDefaultManagedSysId())) {

                                ManagedSysDto mSys = managedSysService
                                        .getManagedSys(l.getManagedSysId());

                                ProvisionConnectorDto connector = provisionConnectorWebService
                                        .getProvisionConnector(mSys
                                                .getConnectorId());

                                ManagedSystemObjectMatch matchObj = null;
                                ManagedSystemObjectMatch[] matchObjAry = managedSysService
                                        .managedSysObjectParam(
                                                mSys.getManagedSysId(), "USER");

                                log.debug("Deleting id=" + l.getLogin());
                                log.debug("- delete using managed sys id="
                                        + mSys.getManagedSysId());

                                // pre-processing
                                bindingMap.put(IDENTITY, l);
                                bindingMap.put(TARGET_SYS_RES, null);

                                Resource resource = null;
                                String resourceId = mSys.getResourceId();

                                // SET PRE ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap
                                        .put(TARGET_SYSTEM_IDENTITY, l.getLogin());
                                bindingMap.put(TARGET_SYS_MANAGED_SYS_ID,
                                        mSys.getManagedSysId());
                                bindingMap.put(TARGET_SYS_RES_ID, resourceId);
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS,
                                        IDENTITY_EXIST);
                                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN,
                                        l.getDomainId());

                                if (resourceId != null) {
                                    resource = resourceDataService
                                            .getResource(resourceId);
                                    if (resource != null) {
                                        bindingMap.put(TARGET_SYS_RES, resource);

                                        String preProcessScript = getResProperty(
                                                resource.getResourceProps(),
                                                "PRE_PROCESS");
                                        if (preProcessScript != null
                                                && !preProcessScript.isEmpty()) {
                                            PreProcessor ppScript = createPreProcessScript(
                                                    preProcessScript, bindingMap);
                                            if (ppScript != null) {
                                                if (executePreProcess(ppScript,
                                                        bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }


                                boolean connectorSuccess = false;

                                if (connector.getConnectorInterface() != null
                                        && connector.getConnectorInterface()
                                                .equalsIgnoreCase("REMOTE")) {
                                    ObjectResponse resp = remoteDelete(
                                            loginDozerConverter.convertToDTO(login,
                                                    true), requestId, mSys,
                                            connector, matchObj, pUser, auditLog);
                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }

                                } else {
                                    ResponseType resp = localDelete(loginDozerConverter.convertToDTO(login, true),
                                            requestId, mSys, auditLog);

                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }
                                }
                                // SET POST ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
                                if (resource != null) {
                                    String postProcessScript = getResProperty(
                                            resource.getResourceProps(),
                                            "POST_PROCESS");
                                    if (postProcessScript != null
                                            && !postProcessScript.isEmpty()) {
                                        PostProcessor ppScript = createPostProcessScript(
                                                postProcessScript, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript,
                                                    bindingMap, pUser, "DELETE",
                                                    connectorSuccess);
                                        }
                                    }
                                }
                            }
                        }
                    }catch(Throwable tw) {
                       log.error(l,tw);
                    }
                }
            }
        }

        // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT
        bindingMap.put(TARGET_SYSTEM_IDENTITY, login.getLogin());
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYS_RES_ID, null);
        bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, login.getDomainId());

        if (callPostProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return response;
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    @Override
    @Transactional
    public ProvisionUserResponse deprovisionSelectedResources(String userId,
            String requestorUserId, List<String> resourceList) {
        return deprovisionSelectedResource.deprovisionSelectedResources(userId,
                requestorUserId, resourceList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#disableUser(java.lang.
     * String, boolean)
     */
    @Override
    @Transactional
    public Response disableUser(String userId, boolean operation,
            String requestorId) {

        return disableUser.disableUser(userId, operation, requestorId,
                MuleContextProvider.getCtx());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#lockUser(java.lang.String,
     * org.openiam.provision.dto.AccountLockEnum)
     */
    @Override
    @Transactional
    public Response lockUser(String userId, AccountLockEnum operation,
            String requestorId) {
        final Response response = new Response();
        String auditReason = null;

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        if (requestorId == null) {
            throw new NullPointerException("requestorId is null");
        }

        if (operation == null) {
            throw new NullPointerException("Operation parameter is null");
        }

        UserEntity user = userMgr.getUser(userId);
        if (user == null) {
            log.error("UserId " + userId + " not found");
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
            return response;

        }
        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        if (operation.equals(AccountLockEnum.LOCKED)) {
            user.setSecondaryStatus(UserStatusEnum.LOCKED);
            if (lg != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Identity flag set to locked.");
                }
                lg.setIsLocked(1);
            }
            auditReason = "LOCKED";
        } else if (operation.equals(AccountLockEnum.LOCKED_ADMIN)) {
            user.setSecondaryStatus(UserStatusEnum.LOCKED_ADMIN);
            if (lg != null) {
                lg.setIsLocked(2);
            }
            auditReason = "LOCKED_ADMIN";
        } else {
            user.setSecondaryStatus(null);
            if (lg == null) {
                log.error(String.format(
                        "Primary identity for UserId %s not found", userId));
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                return response;
            }
            lg.setAuthFailCount(0);
            lg.setIsLocked(0);
            auditReason = "UNLOCK";
        }
        loginManager.updateLogin(lg);
        userMgr.updateUserWithDependent(user, false);

        String requestId = "R" + UUIDGen.getUUID();

        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorId);

        String login = null;
        String domain = null;
        if (lg != null) {
            login = lg.getLogin();
            domain = lg.getDomainId();
        }

        final String logDomainId = (lRequestor != null) ? lRequestor
                .getDomainId() : null;
        final String logLoginId = (lRequestor != null) ? lRequestor.getLogin()
                : null;
        final String logUserId = (user != null && user.getUserId() != null) ? user
                .getUserId() : null;

        auditHelper.addLog(auditReason, logDomainId, logLoginId, "IDM SERVICE",
                requestorId, "USER", "USER", logUserId, null, "SUCCESS", null,
                null, null, requestId, auditReason, null, null, null, login,
                domain);

        final List<LoginEntity> loginList = loginManager.getLoginByUser(userId);
        for (final LoginEntity userLogin : loginList) {
            if (userLogin != null) {
                if (userLogin.getManagedSysId() != null
                        && !userLogin.getManagedSysId().equals("0")) {
                    ResponseType responsetype = null;
                    final String managedSysId = userLogin.getManagedSysId();
                    final ManagedSysDto managedSys = managedSysService
                            .getManagedSys(managedSysId);
                    if (AccountLockEnum.LOCKED.equals(operation)
                            || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                        final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                        suspendCommand.setObjectIdentity(userLogin.getLogin());
                        suspendCommand.setTargetID(managedSysId);
                        suspendCommand.setRequestID("R" + System.currentTimeMillis());
                        connectorAdapter.suspendRequest(managedSys,
                                suspendCommand, MuleContextProvider.getCtx());
                    } else {
                        final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                        resumeRequest.setObjectIdentity(userLogin.getLogin());
                        resumeRequest.setTargetID(managedSysId);
                        resumeRequest.setRequestID("R"
                                + System.currentTimeMillis());
                        // responsetype = client.resume(resumeRequest);
                        connectorAdapter.resumeRequest(managedSys,
                                resumeRequest, MuleContextProvider.getCtx());
                    }

                    if (responsetype == null) {
                        log.info("Response object from set password is null");
                        response.setStatus(ResponseStatus.FAILURE);
                        return response;
                    }

                    if (responsetype.getStatus() == null) {
                        log.info("Response status is null");
                        response.setStatus(ResponseStatus.FAILURE);
                        return response;
                    }
                    log.info(String.format("Response status=%s",
                            response.getStatus()));

                    // TODO: process the result of the WS call to resume/suspend
                    // of teh connector
                }
            }
        }
        final List<RoleEntity> roleList = roleDataService.getUserRoles(
                user.getUserId(), null, 0, Integer.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (final RoleEntity role : roleList) {
                final List<Resource> resourceList = resourceDataService
                        .getResourcesForRole(role.getRoleId(), 0,
                                Integer.MAX_VALUE);
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    for (final Resource resource : resourceList) {
                        final ManagedSysDto managedSys = managedSysService
                                .getManagedSys(resource.getManagedSysId());
                        if (managedSys != null) {
                            ResponseType responsetype = null;
                            if(AccountLockEnum.LOCKED.equals(operation) || AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                                final SuspendResumeRequest suspendCommand = new SuspendResumeRequest();
                                suspendCommand.setObjectIdentity(lg.getLogin());
                                suspendCommand.setTargetID(managedSys.getManagedSysId());
                                suspendCommand.setRequestID("R"
                                        + System.currentTimeMillis());
                                connectorAdapter.suspendRequest(managedSys,
                                        suspendCommand, MuleContextProvider.getCtx());
                            } else {
                                final SuspendResumeRequest resumeRequest = new SuspendResumeRequest();
                                resumeRequest.setObjectIdentity(lg.getLogin());
                                resumeRequest.setTargetID(managedSys.getManagedSysId());
                                resumeRequest.setRequestID("R"
                                        + System.currentTimeMillis());
                                // responsetype = client.resume(resumeRequest);
                                connectorAdapter.resumeRequest(managedSys,
                                        resumeRequest, MuleContextProvider.getCtx());
                            }

                            if (responsetype.getStatus() == null) {
                                log.info("Response status is null");
                                response.setStatus(ResponseStatus.FAILURE);
                                return response;
                            }
                            log.info(String.format("Response status=%s",
                                    response.getStatus()));

                            // TODO: process the result of the WS call to
                            // resume/suspend of teh connector
                            /*
                             * if(StringUtils.isNotBlank(managedSys.getConnectorId
                             * ())) { final ProvisionConnector connector =
                             * provisionConnectorWebService
                             * .getConnector(managedSys.getConnectorId());
                             * if(connector != null) { final
                             * ClientProxyFactoryBean factory = new
                             * JaxWsProxyFactoryBean();
                             * factory.setServiceClass(ConnectorService.class);
                             * 
                             * log.info("Service endpoint : " +
                             * connector.getServiceUrl() );
                             * 
                             * factory.setAddress(connector.getServiceUrl());
                             * javax.xml.namespace.QName qname =
                             * javax.xml.namespace
                             * .QName.valueOf(connector.getServiceNameSpace());
                             * factory.setEndpointName(qname); final
                             * ConnectorService client = (ConnectorService)
                             * factory.create();
                             * 
                             * log.info("connector service client " + client);
                             * 
                             * ResponseType responsetype = null; final
                             * PSOIdentifierType psoIdentifierType = new
                             * PSOIdentifierType(lg.getId().getLogin(),null,
                             * lg.getId().getManagedSysId());
                             * 
                             * if(AccountLockEnum.LOCKED.equals(operation) ||
                             * AccountLockEnum.LOCKED_ADMIN.equals(operation)) {
                             * final SuspendRequestType suspendCommand = new
                             * SuspendRequestType();
                             * suspendCommand.setPsoID(psoIdentifierType);
                             * suspendCommand.setRequestID("R" +
                             * System.currentTimeMillis());
                             * connectorAdapter.suspendRequest(managedSys,
                             * suspendCommand, muleContext); } else { final
                             * ResumeRequestType resumeRequest = new
                             * ResumeRequestType();
                             * resumeRequest.setPsoID(psoIdentifierType);
                             * resumeRequest.setRequestID("R" +
                             * System.currentTimeMillis()); //responsetype =
                             * client.resume(resumeRequest);
                             * connectorAdapter.resumeRequest(managedSys,
                             * resumeRequest, muleContext); }
                             * 
                             * if (responsetype == null) {
                             * log.info("Response object from set password is null"
                             * ); response.setStatus(ResponseStatus.FAILURE);
                             * return response; }
                             * 
                             * if (responsetype.getStatus() == null) {
                             * log.info("Response status is null");
                             * response.setStatus(ResponseStatus.FAILURE);
                             * return response; }
                             * log.info(String.format("Response status=%s",
                             * response.getStatus()));
                             * 
                             * //TODO: process the result of the WS call to
                             * resume/suspend of teh connector } }
                             */
                        }
                    }
                }
            }
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#modifyUser(org.openiam
     * .provision.dto.ProvisionUser)
     */
    @Override
    @Transactional
    public ProvisionUserResponse modifyUser(ProvisionUser pUser) {

        Session session = hibernateTemplate.getSessionFactory().getCurrentSession(); // TODO: remove this!!!

        UserEntity origUser = userMgr.getUser(pUser.getUserId());
        if (origUser == null) {
            throw new IllegalArgumentException("UserId='" + pUser.getUserId() + "' is not valid");
        }

        log.debug("--- DEFAULT PROVISIONING SERVICE: modifyUser called ---");

        ProvisionUserResponse resp = new ProvisionUserResponse();
        String requestId = "R" + UUIDGen.getUUID();

        Organization org = null;
        if (pUser.getPrimaryOrganization() != null) {
            org = orgManager.getOrganization(pUser.getPrimaryOrganization().getId(), null);
        }

        if (org == null) {
            final List<Organization> organizationForCurrentUser = orgManager
                    .getOrganizationsForUserByType(pUser.getUserId(), null,
                            "ORGANIZATION");
            if (CollectionUtils.isNotEmpty(organizationForCurrentUser)) {
                for (final Organization organization : organizationForCurrentUser) {
                    if (!pUser.isOrganizationMarkedAsDeleted(organization.getId())) {
                        org = organization;
                        break;
                    }
                }
            }
        }

        // bind the objects to the scripting engine
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        // bindingMap.put("user", pUser.getUser());
        bindingMap.put("org", org);
        bindingMap.put("context", SpringContextProvider.getApplicationContext());
        bindingMap.put("operation", "MODIFY");
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYSTEM_IDENTITY, null);
        // clone the user object so that we have it for comparison in the
        // scripts
        bindingMap.put("userBeforeModify", new ProvisionUser(userDozerConverter.convertToDTO(origUser, true)));

        if (callPreProcessor("MODIFY", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return resp;
        }

        // make sure that our object as the attribute set that will be used for
        // audit logging
        checkAuditingAttributes(pUser); //TODO: Make a revision of this code

        // get the current roles
        List<Role> curRoleList = roleDataService.getUserRolesAsFlatList(pUser.getUserId()); //TODO: do we need children roles?
        // get all groups for user
        List<Group> curGroupList = groupDozerConverter.convertToDTOList(
                groupManager.getGroupsForUser(pUser.getUserId(), null, 0, Integer.MAX_VALUE), false);

        List<LoginEntity> curPrincipalList = origUser.getPrincipalList();

        // check that a primary identity exists some where
        LoginEntity curPrimaryIdentity = getPrimaryIdentity(sysConfiguration.getDefaultManagedSysId(), curPrincipalList);

        if (curPrimaryIdentity == null && pUser.getPrincipalList() == null) {
            log.debug("Identity not found...");
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return resp;
        }

        pUser.setObjectState(BaseObject.UPDATE);

        // make the role and group list before these updates available to the
        // attribute policies
        bindingMap.put("currentRoleList", curRoleList);
        bindingMap.put("currentGroupList", curGroupList);

        origUser.setLastUpdate(new Date(System.currentTimeMillis()));

        // update originalUser from IDM with the new user information
        updateUserProperties(origUser, pUser);

        // update attributes
        updateUserAttributes(origUser, pUser);

        // update addresses
        updateAddresses(origUser, pUser);

        // update phones
        updatePhones(origUser, pUser);

        // update emails
        updateUserEmails(origUser, pUser);

        // update supervisors
        updateSupervisors(pUser);

        // update groups
        updateGroups(origUser, pUser);

        // update roles
        Set<Role> roleSet = new HashSet<Role>();
        Set<Role> deleteRoleSet = new HashSet<Role>();

        updateRoles(origUser, pUser, roleSet, deleteRoleSet);
        bindingMap.put("userRole", roleSet);

        // update organization associations
        updateUserOrgAffiliations(origUser, pUser);

        // Set of resources that a person should have based on their active roles
        Set<Resource> resourceSet = getResourcesForRoles(roleSet);
        // Set of resources that are to be removed based on roles that are to be deleted
        Set<Resource> deleteResourceSet = getResourcesForRoles(deleteRoleSet);

        // update resources, update resources sets
        updateResources(origUser, pUser, resourceSet, deleteResourceSet);

        log.debug("Resources to be added ->> " + resourceSet);
        log.debug("Delete the following resources ->> " + deleteResourceSet);

        // determine which resources are new and which ones are existing
        updateResourceState(resourceSet, curPrincipalList); //TODO: Check do we really need this at all?

        // update principals
        updatePrincipals(origUser, pUser);

        // get primary identity and bind it for the groovy scripts
        LoginEntity primaryIdentityEntity = getPrimaryIdentity(sysConfiguration.getDefaultManagedSysId(),
                origUser.getPrincipalList());
        Login primaryIdentity = (primaryIdentityEntity != null) ?
                loginDozerConverter.convertToDTO(primaryIdentityEntity, false) : null;

        if (primaryIdentity == null) { // Try to generate a new primary identity from scratch
            primaryIdentity = buildPrimaryPrincipal(bindingMap, scriptRunner);
        }

        if (primaryIdentity != null) {
            if (StringUtils.isEmpty(primaryIdentity.getUserId())) {
                throw new IllegalArgumentException("primaryIdentity userId can not be empty");
            }
            String password = primaryIdentity.getPassword();
            if (password != null) {
                try {
                    String decPassword = loginManager.decryptPassword(primaryIdentity.getUserId(), password);
                    bindingMap.put("password", decPassword);

                } catch (EncryptionException e) {
                    bindingMap.put("password", password);  //TODO: Do we really need to do this way?
                }
            }
            bindingMap.put("lg", primaryIdentity);

        } else {
            log.debug("Primary identity not found for user=" + origUser.getUserId());
        }

        log.debug("Binding active roles to scripting");
        log.debug("- role set -> " + roleSet);
        log.debug("- Primary Identity : " + primaryIdentity);

        //TODO: Check what this code is for
        /*
        // SAS - Do not change the list of roles
        pUser.setMemberOfRoles(activeRoleList);
        // bindingMap.put("user", origUser);

        log.debug("**Updated orig user=" + origUser);
        log.debug("-- " + origUser.getUserId() + " " + origUser.getFirstName()
                + " " + origUser.getLastName());

        String userStatus = null;
        if (pUser.getStatus() != null) {
            userStatus = pUser.getStatus().toString();
        }
        */

        /*
         * IdmAuditLog auditLog = auditHelper.addLog("MODIFY",
         * pUser.getRequestorDomain(), pUser.getRequestorLogin(), "IDM SERVICE",
         * origUser.getCreatedBy(), "0", "USER", origUser.getUserId(), null,
         * "SUCCESS", null, "USER_STATUS", userStatus, requestId, null,
         * pUser.getSessionId(), null, pUser.getRequestClientIP(),
         * primaryIdentity.getLogin(), primaryIdentity.getDomainId());
         * 
         * auditHelper.persistLogList(pendingLogItems, requestId,
         * pUser.getSessionId());
         */

        // deprovision resources
        if (CollectionUtils.isNotEmpty(deleteResourceSet)) {
            for (Resource res : deleteResourceSet) {
                try { // Protects other resources if one resource failed
                    deprovisionResource(res, origUser, requestId);
                } catch (Throwable tw) {
                    log.error(res, tw);
                }
            }
        }

        // provision resources
        if (CollectionUtils.isNotEmpty(resourceSet)) {
            for (Resource res : resourceSet) {
                try { // Protects other resources if one resource failed
                    provisionResource(res, origUser, pUser, bindingMap, primaryIdentity, requestId);
                } catch (Throwable tw) {
                    log.error(res, tw);
                }
            }
        }

//        validateIdentitiesExistforSecurityDomain( //TODO: What is it???
//                loginDozerConverter.convertToDTO(primaryIdentity, true),
//                activeRoleList);

        log.debug("DEFAULT PROVISIONING SERVICE: modifyUser complete");

        ProvisionUser finalProvUser = new ProvisionUser(userDozerConverter.convertToDTO(origUser, true));
        bindingMap.put("userAfterModify", finalProvUser);

        if (callPostProcessor("MODIFY", finalProvUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return resp;
                    }

        /* Response object */
        resp.setStatus(ResponseStatus.SUCCESS);
        resp.setUser(finalProvUser);
        return resp;

                }



    private void updateResourceState(Set<Resource> resourceSet,
            List<LoginEntity> curPrincipalList) {
        if (CollectionUtils.isNotEmpty(resourceSet)) {
            for (Resource r : resourceSet) {
                r.setObjectState(BaseObject.NEW);
                for (LoginEntity l : curPrincipalList) {
                    if (r.getManagedSysId() != null) {
                        if (r.getManagedSysId().equalsIgnoreCase(l.getManagedSysId())) {
                            r.setObjectState(BaseObject.UPDATE);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void provisionResource(Resource res, UserEntity origUser, ProvisionUser pUser,
            Map<String, Object> bindingMap, Login primaryIdentity, String requestId) {
        String managedSysId = res.getManagedSysId();
                if (managedSysId != null) {
            if (pUser.getSrcSystemId() != null) {
                if (res.getResourceId().equalsIgnoreCase(pUser.getSrcSystemId())) { //TODO: ask why???
                    return;
                }
            }

            // what the new object will look like
            // Provision user that goes to the target system. Derived from origUser after all changes
            ProvisionUser targetSysProvUser = new ProvisionUser(userDozerConverter.convertToDTO(origUser, true));

            bindingMap.put(TARGET_SYS_RES_ID, res.getResourceId());
            bindingMap.put(TARGET_SYS_MANAGED_SYS_ID, managedSysId);
            bindingMap.put("user", targetSysProvUser);

            List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getResourceId());
            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
                    if (mSys == null || mSys.getConnectorId() == null) {
                return;
            }
            ProvisionConnectorEntity connectorEntity = connectorService.getProvisionConnectorsById(mSys.getConnectorId());
            if (connectorEntity == null) {
                return;
                    }
            ProvisionConnectorDto connector = provisionConnectorConverter.convertToDTO(connectorEntity, true);

                    ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, "USER");
                    if (matchObjAry != null && matchObjAry.length > 0) {
                        matchObj = matchObjAry[0];
                        bindingMap.put(MATCH_PARAM, matchObj);
                    }

                    // get the identity linked to this resource / managedsys
            // determine if this identity exists in IDM or not
            // if not, do an ADD otherwise, do an UPDATE
            LoginEntity mLg = getPrincipalForManagedSys(managedSysId, origUser.getPrincipalList());
                    if (mLg != null && mLg.getLoginId() != null) {
                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, mLg.getDomainId());
                    } else {
                bindingMap.put(TARGET_SYS_SECURITY_DOMAIN, mSys.getDomainId());
                    }

                    log.debug("PROCESSING IDENTITY =" + mLg);

                        Map<String, String> currentValueMap = new HashMap<String, String>();
            boolean isMngSysIdentityExistsInOpeniam = (mLg != null);

                        if (!isMngSysIdentityExistsInOpeniam) {
                try {
                    log.debug(" - Building principal Name for: " + managedSysId);
                    String newPrincipalName = ProvisionServiceUtil.buildPrincipalName(attrMap, scriptRunner, bindingMap);
                    log.debug(" - New principalName = " + newPrincipalName);

                    mLg = new LoginEntity();
                    log.debug(" - PrimaryIdentity for build new identity for target system = " + primaryIdentity);

                                mLg.setLogin(newPrincipalName);
                                mLg.setDomainId(primaryIdentity.getDomainId());
                                mLg.setManagedSysId(managedSysId);
                                mLg.setPassword(primaryIdentity.getPassword());
                                mLg.setUserId(primaryIdentity.getUserId());

                                mLg.setAuthFailCount(0);
                                mLg.setCreateDate(new Date(System.currentTimeMillis()));
                    mLg.setCreatedBy(origUser.getLastUpdatedBy());
                                mLg.setIsLocked(0);
                                mLg.setFirstTimeLogin(1);
                                mLg.setStatus("ACTIVE");

                            } catch (ScriptEngineException e) {
                                e.printStackTrace();
                            }
                        }

            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, isMngSysIdentityExistsInOpeniam ? IDENTITY_NEW : IDENTITY_EXIST);
                        bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, null);
            bindingMap.put(TARGET_SYSTEM_IDENTITY, isMngSysIdentityExistsInOpeniam ? mLg.getLogin() : null);
            bindingMap.put( TARGET_SYS_SECURITY_DOMAIN, isMngSysIdentityExistsInOpeniam ? mLg.getDomainId() : null);

            // Identity of current target system
            Login targetSysLogin = loginDozerConverter.convertToDTO(mLg, false);
            for (Login l : pUser.getPrincipalList()) { // saving Login properties from pUser
                if (l.getLoginId()!=null && l.getLoginId().equals(targetSysLogin.getLoginId())) {
                    targetSysLogin.setOperation(l.getOperation());
                    targetSysLogin.setOrigPrincipalName(l.getOrigPrincipalName());
                }
            }

            ExtensibleUser extUser = buildFromRules(targetSysProvUser, targetSysLogin,
                    attrMap, scriptRunner, bindingMap);

                        // get the attributes at the target system
                        // this lookup only for getting attributes from the
                        // system
                        boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(
                    targetSysLogin, extUser, mSys, connector, matchObj, currentValueMap);
                        boolean connectorSuccess = false;
            if (!isExistedInTargetSystem) {

                            // create the secondary identity for this resource
                log.debug("Adding new identity to target system. Primary Identity is:" + primaryIdentity);
                            // pre-processing
                String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                    PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                if (ppScript != null) {
                        if (executePreProcess(ppScript, bindingMap, pUser, "ADD") == ProvisioningConstants.FAIL) {
                            return;
                                    }
                                }
                            }
                if (connector.getConnectorInterface() != null &&
                        connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {
                    connectorSuccess = remoteAdd(targetSysLogin, requestId, mSys, matchObj, extUser, connector);

                            } else {
                                // build the request
                                CrudRequest<ExtensibleUser> addReqType = new CrudRequest<ExtensibleUser>();
                    addReqType.setObjectIdentity(targetSysLogin.getLogin());
                                addReqType.setRequestID(requestId);
                    addReqType.setTargetID(targetSysLogin.getManagedSysId());
                                addReqType.setExtensibleObject(extUser);
                    log.debug("Creating identity in target system:" + targetSysLogin.getLoginId());

                    ObjectResponse responseType = connectorAdapter.addRequest(mSys, addReqType, MuleContextProvider.getCtx());
                                if (responseType.getStatus() == StatusCodeType.SUCCESS) {
                                    connectorSuccess = true;
                                }
                                // post processing
                    String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                    if (StringUtils.isNotEmpty(postProcessScript)) {
                        PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                    if (ppScript != null) {
                            executePostProcess(ppScript, bindingMap, pUser, "ADD", connectorSuccess);
                                    }
                                }
                                if (!connectorSuccess) {
                        return;
                                }
                    /* TODO: Fix all audit messages
                    auditHelper.addLog("ADD IDENTITY", targetSysProvUser.getRequestorDomain(),
                            targetSysProvUser.getRequestorLogin(), "IDM SERVICE",
                            targetSysProvUser.getCreatedBy(),
                            targetSysLogin.getManagedSysId(), "USER",
                            origUser.getUserId(), null, "SUCCESS",
                            auditLog.getLogId(), "USER_STATUS",
                            userStatus, requestId, null,
                            targetSysProvUser.getSessionId(), null,
                            targetSysProvUser.getRequestClientIP(),
                            targetSysLogin.getLogin(), targetSysLogin.getDomainId());
                                 */
                                bindingMap.remove(MATCH_PARAM);
                                }
                if (connectorSuccess && !isMngSysIdentityExistsInOpeniam) {
                    origUser.getPrincipalList().add(mLg); // add new identity to user
                            }

            } else { // if user exists in target system

                log.debug("Building attributes for managedSysId = " + managedSysId);
                log.debug("identity for managedSys is " + targetSysLogin.getLogin());
                bindingMap.put(TARGET_SYSTEM_ATTRIBUTES, currentValueMap);

                String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                    PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                if (ppScript != null) {
                        if (executePreProcess(ppScript, bindingMap, pUser, "MODIFY")
                                == ProvisioningConstants.FAIL) {
                            return;
                                    }
                                }
                            }
                // updates the attributes with the correct operation codes
                extUser = updateAttributeList(extUser, currentValueMap);

                // test to see if the updates were carried for forward
                List<ExtensibleAttribute> extAttList = extUser.getAttributes();

                if (connector.getConnectorInterface() != null &&
                        connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

                    if (targetSysLogin.getOperation() == AttributeOperationEnum.REPLACE
                            && targetSysLogin.getOrigPrincipalName() != null) {
                                    extAttList.add(new ExtensibleAttribute(
                                "ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(), 2, "String"));
                                }

                                CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
                    userReq.setObjectIdentity(targetSysLogin.getLogin());
                                userReq.setRequestID(requestId);
                    userReq.setTargetID(targetSysLogin.getManagedSysId());
                                userReq.setHostLoginId(mSys.getUserId());
                                String passwordDecoded = mSys.getPswd();
                                try {
                                    passwordDecoded = getDecryptedPassword(mSys);
                                } catch (ConnectorDataException e) {
                                    e.printStackTrace();
                                }
                                userReq.setHostLoginPassword(passwordDecoded);
                                userReq.setHostUrl(mSys.getHostUrl());
                                userReq.setBaseDN(matchObj.getBaseDn());
                                userReq.setOperation("EDIT");
                                userReq.setExtensibleObject(extUser);

                    userReq.setScriptHandler(mSys.getModifyHandler());

                    ObjectResponse respType = remoteConnectorAdapter.modifyRequest(mSys, userReq,
                                                connector, MuleContextProvider.getCtx());

                    if (connectorSuccess && respType.getStatus() == StatusCodeType.SUCCESS) {
                                    connectorSuccess = true;
                                }

                            } else {
                    // build the request
                    CrudRequest<ExtensibleUser> modReqType = new CrudRequest<ExtensibleUser>();
                    modReqType.setTargetID(targetSysLogin.getManagedSysId());
                    modReqType.setObjectIdentity(targetSysLogin.getLogin());
                                modReqType.setRequestID(requestId);

                    // check if this request calls for the identity being renamed
                                log.debug("Send request to connector - Original Principal Name = "
                            + targetSysLogin.getOrigPrincipalName());

                    if (targetSysLogin.getOrigPrincipalName() != null) {
                                    extAttList.add(new ExtensibleAttribute(
                                "ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(), 2, "String"));
                                }
                                modReqType.setExtensibleObject(extUser);

                    log.debug("Creating identity in target system: " + targetSysLogin.getLoginId());
                                ObjectResponse respType = connectorAdapter
                                        .modifyRequest(mSys, modReqType,
                                                MuleContextProvider.getCtx());

                                if (respType.getStatus() == StatusCodeType.SUCCESS) {
                                    connectorSuccess = true;
                                }

                            }

                if (connectorSuccess && !isMngSysIdentityExistsInOpeniam) {
                    origUser.getPrincipalList().add(mLg); // add new identity to user
                }

                            // post processing
                String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                if (postProcessScript != null && !postProcessScript.isEmpty()) {
                    PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                if (ppScript != null) {
                        executePostProcess(ppScript, bindingMap, targetSysProvUser, "MODIFY", connectorSuccess);
                                }
                            }
                    }
                    bindingMap.remove(MATCH_PARAM);

                }
            }

    private void deprovisionResource(Resource res, UserEntity origUser, String requestId) {
        String managedSysId = res.getManagedSysId();
        log.debug("Deleting identity for managedSys=" + managedSysId);

        ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        if (mSys == null || mSys.getConnectorId() == null) {
            return;
            }

        LoginEntity mLg = getPrincipalForManagedSys(managedSysId, origUser.getPrincipalList());

        if (mLg != null) {

            ProvisionConnectorEntity connectorEntity = connectorService.getProvisionConnectorsById(mSys.getConnectorId());
            if (connectorEntity == null) {
                return;
        }
            ProvisionConnectorDto connector = provisionConnectorConverter.convertToDTO(connectorEntity, true);

            ObjectResponse respType = null;
            if (connector.getConnectorInterface() != null &&
                    connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

                CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
                request.setObjectIdentity(mLg.getLogin());
                request.setRequestID(requestId);
                request.setTargetID(mLg.getManagedSysId());
                request.setHostLoginId(mSys.getUserId());
                String passwordDecoded = mSys.getPswd();
                try {
                    passwordDecoded = getDecryptedPassword(mSys);
                } catch (ConnectorDataException e) {
                    e.printStackTrace();
                }
                request.setHostLoginPassword(passwordDecoded);
                request.setHostUrl(mSys.getHostUrl());
                request.setOperation("DELETE");
                request.setScriptHandler(mSys.getDeleteHandler());

                respType = remoteConnectorAdapter.deleteRequest(mSys, request, connector, MuleContextProvider.getCtx());

            } else {

                CrudRequest<ExtensibleUser> reqType = new CrudRequest<ExtensibleUser>();
                reqType.setRequestID(requestId);
                reqType.setObjectIdentity(mLg.getLogin());
                reqType.setTargetID(managedSysId);

                respType = connectorAdapter.deleteRequest(mSys, reqType, MuleContextProvider.getCtx());

    }
            if (respType != null && respType.getStatus() == StatusCodeType.SUCCESS) {

                for (LoginEntity e : origUser.getPrincipalList()) {
                    if (e.getLoginId().equals(mLg.getLoginId())) {
                        e.setStatus("INACTIVE");
                        e.setAuthFailCount(0);
                        e.setPasswordChangeCount(0);
                        e.setIsLocked(0);

                        // change the password to a random scrambled password
                        String scrambledPassword = PasswordGenerator.generatePassword(10);
                        try {
                            e.setPassword(loginManager.encryptPassword(mLg.getUserId(), scrambledPassword));
                        } catch (EncryptionException ee) {
                            log.error(ee);
                            // put the password in a clean state so that the
                            // operation continues
                            e.setPassword(null);
        }
                        break;
                    }
                }

                // LOG THIS EVENT
                    /*
                     * auditHelper.addLog("REMOVE IDENTITY", pUser
                      * .getRequestorDomain(), pUser.getRequestorLogin(),
                     * "IDM SERVICE", origUser.getCreatedBy(), mLg
                     * .getManagedSysId(), "USER", origUser .getUserId(), null,
                     * "SUCCESS", auditLogId, "USER_STATUS", status, requestId,
                     * null, pUser .getSessionId(), null, pUser
                     * .getRequestClientIP(), mLg .getLogin(),
                     * mLg.getDomainId());
                     */
            }
        }

    }

    private void deProvisionResources(List<Resource> deleteResourceList,
            String userId, String requestorId, String requestId,
            ProvisionUser pUser, String status, User origUser) {
        if (deleteResourceList != null) {

            List<LoginEntity> identityList = loginManager
                    .getLoginByUser(userId);

            for (Resource res : deleteResourceList) {
                String managedSysId = res.getManagedSysId();

                log.debug("Deleting identity for managedSys=" + managedSysId);

                // object that will be sent to the connectors
                List<AttributeMap> attrMap = managedSysService
                        .getResourceAttributeMaps(res.getResourceId());
                // List<AttributeMap> attrMap =
                // resourceDataService.getResourceAttributeMaps(res.getResourceId());

                Login mLg = getPrincipalForManagedSys(managedSysId,
                        loginDozerConverter.convertToDTOList(identityList,
                                false));

                if (mLg != null) {
                    // make sure the identity exists before we deprovision it.
                    ManagedSysDto mSys = managedSysService
                            .getManagedSys(managedSysId);
                    ProvisionConnectorDto connector = provisionConnectorWebService
                            .getProvisionConnector(mSys.getConnectorId());

                    mLg.setStatus("INACTIVE");
                    mLg.setAuthFailCount(0);
                    mLg.setPasswordChangeCount(0);
                    mLg.setIsLocked(0);
                    // change the password to a random scrambled password
                    String scrambledPassword = PasswordGenerator
                            .generatePassword(10);
                    try {
                        mLg.setPassword(loginManager.encryptPassword(
                                mLg.getUserId(), scrambledPassword));
                    } catch (EncryptionException ee) {
                        log.error(ee);
                        // put the password in a clean state so that the
                        // operation continues
                        mLg.setPassword(null);
                    }

                    loginManager.updateLogin(loginDozerConverter
                            .convertToEntity(mLg, true));

                    // LOG THIS EVENT
                    /*
                     * auditHelper.addLog("REMOVE IDENTITY", pUser
                      * .getRequestorDomain(), pUser.getRequestorLogin(),
                     * "IDM SERVICE", origUser.getCreatedBy(), mLg
                     * .getManagedSysId(), "USER", origUser .getUserId(), null,
                     * "SUCCESS", auditLogId, "USER_STATUS", status, requestId,
                     * null, pUser .getSessionId(), null, pUser
                     * .getRequestClientIP(), mLg .getLogin(),
                     * mLg.getDomainId());
                     */


                    if (connector.getConnectorInterface() != null
                            && connector.getConnectorInterface()
                                    .equalsIgnoreCase("REMOTE")) {

                        CrudRequest<ExtensibleUser> request = new CrudRequest();

                        request.setObjectIdentity(mLg.getLogin());
                        request.setRequestID(requestId);
                        request.setTargetID(mLg.getManagedSysId());
                        request.setHostLoginId(mSys.getUserId());
                        String passwordDecoded = mSys.getPswd();
                        try {
                            passwordDecoded = getDecryptedPassword(mSys);
                        } catch (ConnectorDataException e) {
                            e.printStackTrace();
                        }
                        request.setHostLoginPassword(passwordDecoded);
                        request.setHostUrl(mSys.getHostUrl());

                        request.setOperation("DELETE");

                        request.setScriptHandler(mSys.getDeleteHandler());

                        remoteConnectorAdapter.deleteRequest(mSys, request,
                                connector, MuleContextProvider.getCtx());

                    } else {
                        CrudRequest<ExtensibleUser> reqType = new CrudRequest<ExtensibleUser>();
                        reqType.setRequestID(requestId);
                        reqType.setObjectIdentity(mLg.getLogin());
                        reqType.setTargetID(managedSysId);
                        connectorAdapter.deleteRequest(
                                mSys, reqType, MuleContextProvider.getCtx());

                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#resetPassword(org.openiam
     * .provision.dto.PasswordSync)
     */
    @Override
    @Transactional
    public PasswordResponse resetPassword(PasswordSync passwordSync) {
        log.debug("----resetPassword called.------");

        final PasswordResponse response = new PasswordResponse(
                ResponseStatus.SUCCESS);

        final String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        final LoginEntity login = loginManager.getLoginByManagedSys(
                passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                passwordSync.getManagedSystemId());
        if (login == null) {
            auditHelper.addLog("RESET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    null, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.PRINCIPAL_NOT_FOUND.toString(), null,
                    "Principal not found: " + passwordSync.getPrincipal());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        final String userId = login.getUserId();
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        final UserEntity usr = userMgr.getUser(userId);
        if (usr == null) {
            auditHelper.addLog("RESET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    userId, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.PRINCIPAL_NOT_FOUND.toString(), null,
                    "User object not found: " + passwordSync.getPrincipal(),
                    passwordSync.getRequestClientIP(),
                    passwordSync.getSecurityDomain(),
                    passwordSync.getPrincipal());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        String password = passwordSync.getPassword();
        if (StringUtils.isEmpty(password)) {
            // autogenerate the password
            password = String.valueOf(PasswordGenerator.generatePassword(8));
        }
        String encPassword = null;
        try {
            encPassword = loginManager.encryptPassword(userId, password);
        } catch (EncryptionException e) {
            auditHelper
                    .addLog("RESET PASSWORD",
                            passwordSync.getRequestorDomain(),
                            passwordSync.getRequestorLogin(), "IDM SERVICE",
                            passwordSync.getRequestorId(),
                            passwordSync.getManagedSystemId(), "PASSWORD",
                            userId, null, "FAILURE", null, null, null,
                            requestId, ResponseCode.FAIL_ENCRYPTION.toString(),
                            null, e.toString());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
            return response;
        }
        final boolean retval = loginManager.resetPassword(
                passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                passwordSync.getManagedSystemId(), encPassword);

        if (retval) {
            log.debug("-Password changed in openiam repository for user:"
                    + passwordSync.getPrincipal());

            auditHelper.addLog("RESET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(),
                    passwordSync.getManagedSystemId(), "PASSWORD", userId,
                    null, "SUCCESS", null, null, null, requestId, null, null,
                    null, passwordSync.getRequestClientIP(),
                    passwordSync.getPrincipal(),
                    passwordSync.getSecurityDomain());

            /*
             * came with merge from v2.3 //check if password should be sent to
             * the user. if (passwordSync.isSendPasswordToUser()) { //
             * sendPasswordToUser(usr, password); }
             */
            if (passwordSync.getSendPasswordToUser()) {
                sendResetPasswordToUser(usr, passwordSync.getPrincipal(),
                        password);
            }

        } else {
            auditHelper.addLog("RESET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(),
                    passwordSync.getManagedSystemId(), "PASSWORD", null, null,
                    "FAILURE", null, null, null, requestId,
                    ResponseCode.PRINCIPAL_NOT_FOUND.toString(), null,
                    "Principal not found: " + passwordSync.getPrincipal());

            Response resp = new Response();
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
        }

        if (StringUtils.equalsIgnoreCase(passwordSync.getManagedSystemId(),
                sysConfiguration.getDefaultManagedSysId())) {
            // typical sync
            final List<LoginEntity> principalList = loginManager
                    .getLoginByUser(login.getUserId());
            if (principalList != null) {
                log.debug("PrincipalList size =" + principalList.size());
                for (final LoginEntity lg : principalList) {
                    // get the managed system for the identity - ignore the
                    // managed system id that is linked to openiam's repository
                    log.debug("**** Managed System Id in passwordsync object="
                            + passwordSync.getManagedSystemId());

                    if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(),
                            passwordSync.getManagedSystemId())
                            && !StringUtils.equalsIgnoreCase(
                                    lg.getManagedSysId(),
                                    sysConfiguration.getDefaultManagedSysId())) {
                        // determine if you should sync the password or not
                        final String managedSysId = lg.getManagedSysId();
                        final ManagedSysEntity mSys = managedSystemService
                                .getManagedSysById(managedSysId);
                        if (mSys != null) {
                            final ResourceEntity res = resourceService
                                    .findResourceById(mSys.getResourceId());
                            log.debug(" - managedsys id = " + managedSysId);
                            log.debug(" - Resource for sysId =" + res);

                            // check the sync flag

                            if (syncAllowed(res)) {

                                log.debug("Sync allowed for sys="
                                        + managedSysId);
                                loginManager.resetPassword(lg.getDomainId(),
                                        lg.getLogin(), lg.getManagedSysId(),
                                        encPassword);

                                final ProvisionConnectorEntity connector = connectorService
                                        .getProvisionConnectorsById(mSys
                                                .getConnectorId());

                                ManagedSystemObjectMatchEntity matchObj = null;
                                final List<ManagedSystemObjectMatchEntity> matcheList = managedSystemService
                                        .managedSysObjectParam(managedSysId,
                                                "USER");
                                if (CollectionUtils.isNotEmpty(matcheList)) {
                                    matchObj = matcheList.get(0);
                                }

                                if (StringUtils.equalsIgnoreCase(
                                        connector.getConnectorInterface(),
                                        "REMOTE")) {
                                    remoteResetPassword(requestId, lg,
                                            password, mSys, matchObj,
                                            connector, passwordSync);

                                } else {
                                    localResetPassword(requestId, lg, password,
                                            mSys, passwordSync);

                                }
                            }
                        }
                    }
                }
            }

        } else {
            // update just the system that was specified

            final ManagedSysEntity mSys = managedSystemService
                    .getManagedSysById(passwordSync.getManagedSystemId());
            final ProvisionConnectorEntity connector = connectorService
                    .getProvisionConnectorsById(mSys.getConnectorId());

            ManagedSystemObjectMatchEntity matchObj = null;
            final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                    .managedSysObjectParam(mSys.getManagedSysId(), "USER");
            if (CollectionUtils.isNotEmpty(matchList)) {
                matchObj = matchList.get(0);
            }

            if (StringUtils.equalsIgnoreCase(connector.getConnectorInterface(),
                    "REMOTE")) {
                remoteResetPassword(requestId, login, password, mSys, matchObj,
                        connector, passwordSync);

            } else {

                localResetPassword(requestId, login, password, mSys,
                        passwordSync);

            }
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    @Override
    @Transactional
    public LookupUserResponse getTargetSystemUser(String principalName,
            String managedSysId) {

        log.debug("getTargetSystemUser called. for = " + principalName);

        LookupUserResponse response = new LookupUserResponse(
                ResponseStatus.SUCCESS);
        response.setManagedSysId(managedSysId);
        response.setPrincipalName(principalName);
        // get the connector for the managedSystem

        ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
        ProvisionConnectorDto connector = provisionConnectorWebService
                .getProvisionConnector(mSys.getConnectorId());

        // do the lookup

        if (connector.getConnectorInterface() != null
                && connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

            log.debug("Calling lookupRequest with Remote connector");

            LookupRequest reqType = new LookupRequest();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);
            reqType.setSearchValue(principalName);

            reqType.setTargetID(managedSysId);
            reqType.setHostLoginId(mSys.getUserId());
            String passwordDecoded = mSys.getPswd();
            try {
                passwordDecoded = getDecryptedPassword(mSys);
            } catch (ConnectorDataException e) {
                e.printStackTrace();
            }
            reqType.setHostLoginPassword(passwordDecoded);
            reqType.setHostUrl(mSys.getHostUrl());
            reqType.setExtensibleObject(new ExtensibleUser());
            reqType.setScriptHandler(mSys.getLookupHandler());

            SearchResponse responseType = remoteConnectorAdapter.lookupRequest(
                    mSys, reqType, connector, MuleContextProvider.getCtx());
            if (responseType.getStatus() == StatusCodeType.FAILURE || responseType.getObjectList().size() == 0) {
                response.setStatus(ResponseStatus.FAILURE);
                return response;
            }

            String targetPrincipalName = responseType.getObjectList().get(0)
                    .getObjectIdentity() != null ? responseType.getObjectList().get(0)
                    .getObjectIdentity() : parseUserPrincipal(responseType
                    .getObjectList().get(0).getAttributeList());
            response.setPrincipalName(targetPrincipalName);
            response.setAttrList(responseType.getObjectList().get(0).getAttributeList());
            response.setResponseValue(responseType.getObjectList().get(0));

            return response;

        } else {

            log.debug("Calling lookupRequest local connector");

            LookupRequest request = new LookupRequest();
            //TODO
            request.setExtensibleObject(new ExtensibleUser());
            request.setSearchValue(principalName);
            request.setTargetID(managedSysId);
            SearchResponse responseType = connectorAdapter.lookupRequest(
                    mSys, request, MuleContextProvider.getCtx());

            if (responseType.getStatus() == StatusCodeType.FAILURE) {
                response.setStatus(ResponseStatus.FAILURE);
                return response;
            }

            List<ExtensibleAttribute> attributes = new LinkedList<ExtensibleAttribute>();
            if(!CollectionUtils.isEmpty(responseType.getObjectList())) {
                attributes = responseType.getObjectList().get(0).getAttributeList();
            }
            response.setPrincipalName(parseUserPrincipal(attributes));
            response.setAttrList(attributes);

            return response;
        }
    }

    @Override
    @Transactional
    public LookupUserResponse getTargetSystemUserWithUserId(String userId,
            String managedSysId) {

        // get the principalName for this managedSysId

        List<LoginEntity> principalList = loginManager.getLoginByUser(userId);

        for (LoginEntity l : principalList) {

            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                return getTargetSystemUser(l.getLogin(), managedSysId);
            }

        }

        LookupUserResponse response = new LookupUserResponse(
                ResponseStatus.FAILURE);
        response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
        return response;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.provision.service.ProvisionService#setPassword(org.openiam
     * .provision.dto.PasswordSync)
     */
    @Override
    @Transactional
    public Response setPassword(PasswordSync passwordSync) {
        log.debug("----setPassword called.------");

        final Response response = new Response(ResponseStatus.SUCCESS);
        final Map<String, Object> bindingMap = new HashMap<String, Object>();

        if (callPreProcessor("SET_PASSWORD", null, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }

        final String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        final LoginEntity login = loginManager.getLoginByManagedSys(
                passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                passwordSync.getManagedSystemId());
        if (login == null) {
            auditHelper.addLog("SET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    null, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.PRINCIPAL_NOT_FOUND.toString(), null, null,
                    passwordSync.getRequestClientIP(),
                    passwordSync.getPrincipal(),
                    passwordSync.getSecurityDomain());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        final String userId = login.getUserId();
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        final UserEntity usr = userMgr.getUser(userId);
        if (usr == null) {
            auditHelper.addLog("SET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    null, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.USER_NOT_FOUND.toString(), null, null,
                    passwordSync.getRequestClientIP(),
                    passwordSync.getPrincipal(),
                    passwordSync.getSecurityDomain());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        // validate the password against password policy
        final Password pswd = new Password();
        pswd.setDomainId(passwordSync.getSecurityDomain());
        pswd.setManagedSysId(passwordSync.getManagedSystemId());
        pswd.setPrincipal(passwordSync.getPrincipal());
        pswd.setPassword(passwordSync.getPassword());

        try {
            final PasswordValidationCode rtVal = passwordManager
                    .isPasswordValid(pswd);
            if (rtVal != PasswordValidationCode.SUCCESS) {

                auditHelper.addLog("SET PASSWORD",
                        passwordSync.getRequestorDomain(),
                        passwordSync.getRequestorLogin(), "IDM SERVICE",
                        passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                        usr.getUserId(), null, "FAILURE", null, null, null,
                        requestId, rtVal.getValue(), null, null,
                        passwordSync.getRequestClientIP(),
                        passwordSync.getPrincipal(),
                        passwordSync.getSecurityDomain());

                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.valueOf(rtVal.getValue()));
                return response;
            }

        } catch (ObjectNotFoundException oe) {
            log.error("Object not found", oe);
        }

        String encPassword = null;
        try {
            encPassword = loginManager.encryptPassword(usr.getUserId(),
                    passwordSync.getPassword());
        } catch (EncryptionException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
            return response;
        }

        // make sure that update all the primary identity records
        final List<LoginEntity> principalList = loginManager
                .getLoginByUser(login.getUserId());
        // List<Login> identityList =
        // loginManager.getLoginByUser(usr.getUserId()) ;
        for (final LoginEntity l : principalList) {

            // find the openiam identity and update it in openiam DB
            if (StringUtils.equalsIgnoreCase(l.getManagedSysId(),
                    passwordSync.getManagedSystemId())) {

                final boolean retval = loginManager.setPassword(
                        l.getDomainId(), l.getLogin(),
                        passwordSync.getManagedSystemId(), encPassword,
                        passwordSync.isPreventChangeCountIncrement());
                if (retval) {
                    log.debug("-Password changed in openiam repository for user:"
                            + passwordSync.getPrincipal());

                    auditHelper.addLog("SET PASSWORD",
                            passwordSync.getRequestorDomain(),
                            passwordSync.getRequestorLogin(), "IDM SERVICE",
                            passwordSync.getRequestorId(), "PASSWORD",
                            "PASSWORD", usr.getUserId(), null, "SUCCESS", null,
                            null, null, requestId, null, null, null,
                            passwordSync.getRequestClientIP(), l.getLogin(),
                            l.getDomainId());

                    // update the user object that the password was changed
                    usr.setDatePasswordChanged(new Date(System
                            .currentTimeMillis()));

                    userMgr.updateUserWithDependent(usr, false);

                } else {
                    auditHelper.addLog("SET PASSWORD",
                            passwordSync.getRequestorDomain(),
                            passwordSync.getRequestorLogin(), "IDM SERVICE",
                            passwordSync.getRequestorId(), "PASSWORD",
                            "PASSWORD", usr.getUserId(), null, "FAILURE", null,
                            null, null, requestId, null, null, null,
                            passwordSync.getRequestClientIP(), l.getLogin(),
                            l.getDomainId());
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                }
            }
        }

        boolean connectorSuccess = false;

        if (StringUtils.equalsIgnoreCase(passwordSync.getManagedSystemId(),
                sysConfiguration.getDefaultManagedSysId())) {
            // typical sync
            // List<Login> principalList =
            // loginManager.getLoginByUser(login.getUserId());
            // if (principalList != null) {

            // sync the non-openiam identities
            for (final LoginEntity lg : principalList) {
                // get the managed system for the identity - ignore the managed
                // system id that is linked to openiam's repository
                log.debug("**** Managed System Id in passwordsync object="
                        + passwordSync.getManagedSystemId());

                if (!StringUtils.equalsIgnoreCase(lg.getManagedSysId(),
                        sysConfiguration.getDefaultManagedSysId())) {

                    // determine if you should sync the password or not
                    final String managedSysId = lg.getManagedSysId();
                    final ManagedSysEntity mSys = managedSystemService
                            .getManagedSysById(managedSysId);
                    if (mSys != null) {
                        final ResourceEntity resource = resourceService
                                .findResourceById(mSys.getResourceId());

                        log.debug(" - managedsys id = " + managedSysId);
                        log.debug(" - Resource for sysId =" + resource);

                        // check the sync flag

                        if (syncAllowed(resource)) {

                            log.debug("Sync allowed for sys=" + managedSysId);

                            // pre-process

                            bindingMap.put("IDENTITY", lg);
                            bindingMap.put("RESOURCE", resource);
                            bindingMap.put("PASSWORD_SYNC", passwordSync);

                            if (resource != null) {
                                final String preProcessScript = getResourceProperty(
                                        resource, "PRE_PROCESS");
                                if (preProcessScript != null
                                        && !preProcessScript.isEmpty()) {
                                    final PreProcessor ppScript = createPreProcessScript(
                                            preProcessScript, bindingMap);
                                    if (ppScript != null) {
                                        if (executePreProcess(ppScript,
                                                bindingMap, null,
                                                "SET_PASSWORD") == ProvisioningConstants.FAIL) {
                                            continue;
                                        }
                                    }
                                }
                            }

                            // update the password in openiam
                            loginManager.setPassword(lg.getDomainId(), lg
                                    .getLogin(), lg.getManagedSysId(),
                                    encPassword, passwordSync
                                            .isPreventChangeCountIncrement());

                            if (StringUtils.isNotEmpty(mSys.getConnectorId())) {
                                final ProvisionConnectorEntity connector = connectorService
                                        .getProvisionConnectorsById(mSys
                                                .getConnectorId());

                                ManagedSystemObjectMatchEntity matchObj = null;
                                final List<ManagedSystemObjectMatchEntity> matchObjects = managedSystemService
                                        .managedSysObjectParam(
                                                mSys.getManagedSysId(), "USER");
                                if (CollectionUtils.isNotEmpty(matchObjects)) {
                                    matchObj = matchObjects.get(0);
                                }
                                if (StringUtils.equalsIgnoreCase(
                                        connector.getConnectorInterface(),
                                        "REMOTE")) {
                                    ResponseType resp = remoteSetPassword(
                                            requestId, lg, passwordSync, mSys,
                                            matchObj, connector);
                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }

                                } else {
                                    ResponseType resp = localSetPassword(
                                            requestId, lg, passwordSync, mSys);
                                    if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                        connectorSuccess = true;
                                    }
                                }

                                // post-process
                                if (resource != null) {
                                    final String postProcessScript = getResourceProperty(
                                            resource, "POST_PROCESS");
                                    if (postProcessScript != null
                                            && !postProcessScript.isEmpty()) {
                                        final PostProcessor ppScript = createPostProcessScript(
                                                postProcessScript, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript,
                                                    bindingMap, null,
                                                    "SET_PASSWORD",
                                                    connectorSuccess);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.debug("Sync not allowed for sys=" + managedSysId);
                    }
                }
            }
            // }
        } else {
            // just the update the managed system that was specified.
            final ManagedSysEntity mSys = managedSystemService
                    .getManagedSysById(passwordSync.getManagedSystemId());
            final ProvisionConnectorEntity connector = connectorService
                    .getProvisionConnectorsById(mSys.getConnectorId());

            ManagedSystemObjectMatchEntity matchObj = null;
            final List<ManagedSystemObjectMatchEntity> matchObjects = managedSystemService
                    .managedSysObjectParam(mSys.getManagedSysId(), "USER");
            if (CollectionUtils.isNotEmpty(matchObjects)) {
                matchObj = matchObjects.get(0);
            }

            // pre-process
            final ResourceEntity resource = resourceService
                    .findResourceById(mSys.getResourceId());

            bindingMap.put("IDENTITY", login);
            bindingMap.put("PASSWORD_SYNC", passwordSync);

            if (resource != null) {
                bindingMap.put("RESOURCE", resource);

                final String preProcessScript = getResourceProperty(resource,
                        "PRE_PROCESS");
                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                    final PreProcessor ppScript = createPreProcessScript(
                            preProcessScript, bindingMap);
                    if (ppScript != null) {
                        executePreProcess(ppScript, bindingMap, null,
                                "SET_PASSWORD");
                    }
                }
            }

            if (StringUtils.equalsIgnoreCase(connector.getConnectorInterface(),
                    "REMOTE")) {
                remoteSetPassword(requestId, login, passwordSync, mSys,
                        matchObj, connector);

            } else {

                localSetPassword(requestId, login, passwordSync, mSys);

            }
            // post-process
            if (resource != null) {
                String postProcessScript = getResourceProperty(resource,
                        "POST_PROCESS");
                if (postProcessScript != null && !postProcessScript.isEmpty()) {
                    PostProcessor ppScript = createPostProcessScript(
                            postProcessScript, bindingMap);
                    if (ppScript != null) {
                        executePostProcess(ppScript, bindingMap, null,
                                "SET_PASSWORD", connectorSuccess);
                    }
                }
            }

        }

        if (callPostProcessor("SET_PASSWORD", null, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return response;
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    @Override
    @Transactional
    public Response syncPasswordFromSrc(final PasswordSync passwordSync) {
        // ManagedSystemId where this event originated.
        // Ensure that we dont send the event back to this system

        log.debug("----syncPasswordFromSrc called.------");
        long curTime = System.currentTimeMillis();

        final Response response = new Response(ResponseStatus.SUCCESS);

        final String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        final LoginEntity login = loginManager.getLoginByManagedSys(
                passwordSync.getSecurityDomain(), passwordSync.getPrincipal(),
                passwordSync.getManagedSystemId());
        if (login == null) {
            auditHelper.addLog("SET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    null, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.PRINCIPAL_NOT_FOUND.toString(), null, null,
                    passwordSync.getRequestClientIP(),
                    passwordSync.getPrincipal(),
                    passwordSync.getSecurityDomain());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        final String userId = login.getUserId();
        if (userId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        final UserEntity usr = userMgr.getUser(userId);
        if (usr == null) {
            auditHelper.addLog("SET PASSWORD",
                    passwordSync.getRequestorDomain(),
                    passwordSync.getRequestorLogin(), "IDM SERVICE",
                    passwordSync.getRequestorId(), "PASSWORD", "PASSWORD",
                    null, null, "FAILURE", null, null, null, requestId,
                    ResponseCode.USER_NOT_FOUND.toString(), null, null,
                    passwordSync.getRequestClientIP(),
                    passwordSync.getPrincipal(),
                    passwordSync.getSecurityDomain());

            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        // do not check the password policy
        // assume that the system that accepted the password already checked
        // this.

        String encPassword = null;
        try {
            encPassword = loginManager.encryptPassword(userId,
                    passwordSync.getPassword());

        } catch (EncryptionException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
            return response;
        }

        // make sure that update all the primary identity records
        final List<LoginEntity> principalList = loginManager
                .getLoginByUser(login.getUserId());
        // List<Login> identityList =
        // loginManager.getLoginByUser(usr.getUserId()) ;
        for (final LoginEntity l : principalList) {
            // if the managedsysId is equal to the source or the openiam default
            // ID, then only update the database
            // otherwise do a sync
            if (StringUtils.equalsIgnoreCase(l.getManagedSysId(),
                    passwordSync.getManagedSystemId())
                    || StringUtils.equalsIgnoreCase(l.getManagedSysId(),
                            sysConfiguration.getDefaultManagedSysId())) {
                log.debug("Updating password for " + l.getLoginId());

                final boolean retval = loginManager.setPassword(
                        l.getDomainId(), l.getLogin(), l.getManagedSysId(),
                        encPassword,
                        passwordSync.isPreventChangeCountIncrement());
                if (retval) {
                    log.debug("-Password changed in openiam repository for user:"
                            + passwordSync.getPrincipal());

                    auditHelper.addLog("SET PASSWORD",
                            passwordSync.getRequestorDomain(),
                            passwordSync.getRequestorLogin(), "IDM SERVICE",
                            passwordSync.getRequestorId(), "PASSWORD",
                            "PASSWORD", usr.getUserId(), null, "SUCCESS", null,
                            null, null, requestId, null, null, null,
                            passwordSync.getRequestClientIP(), l.getLogin(),
                            l.getDomainId());

                    // update the user object that the password was changed
                    usr.setDatePasswordChanged(new Date(curTime));
                    // reset any locks that may be in place
                    if (usr.getSecondaryStatus() == UserStatusEnum.LOCKED) {
                        usr.setSecondaryStatus(null);
                    }
                    userMgr.updateUserWithDependent(usr, false);
                } else {
                    auditHelper.addLog("SET PASSWORD",
                            passwordSync.getRequestorDomain(),
                            passwordSync.getRequestorLogin(), "IDM SERVICE",
                            passwordSync.getRequestorId(), "PASSWORD",
                            "PASSWORD", usr.getUserId(), null, "FAILURE", null,
                            null, null, requestId, null, null, null,
                            passwordSync.getRequestClientIP(), l.getLogin(),
                            l.getDomainId());
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
                }
            } else {

                log.debug("Synchronizing password from: " + l.getLoginId());

                // determine if you should sync the password or not
                final String managedSysId = l.getManagedSysId();
                final ManagedSysEntity mSys = managedSystemService
                        .getManagedSysById(managedSysId);
                if (mSys != null) {
                    final ResourceEntity res = resourceService
                            .findResourceById(mSys.getResourceId());

                    // check the sync flag

                    if (syncAllowed(res)) {

                        log.debug("Sync allowed for sys=" + managedSysId);

                        // update the password in openiam
                        loginManager.setPassword(l.getDomainId(), l.getLogin(),
                                l.getManagedSysId(), encPassword,
                                passwordSync.isPreventChangeCountIncrement());

                        final ProvisionConnectorEntity connector = connectorService
                                .getProvisionConnectorsById(mSys
                                        .getConnectorId());

                        ManagedSystemObjectMatchEntity matchObj = null;
                        final List<ManagedSystemObjectMatchEntity> matchList = managedSystemService
                                .managedSysObjectParam(mSys.getManagedSysId(),
                                        "USER");
                        if (CollectionUtils.isNotEmpty(matchList)) {
                            matchObj = matchList.get(0);
                        }

                        // exclude the system where this event occured.

                        if (StringUtils.equalsIgnoreCase(
                                connector.getConnectorInterface(), "REMOTE")) {

                            remoteSetPassword(requestId, l, passwordSync, mSys,
                                    matchObj, connector);

                        } else {

                            localSetPassword(requestId, l, passwordSync, mSys);

                        }

                    } else {
                        log.debug("Sync not allowed for sys=" + managedSysId);
                    }
                }
            }
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    /* ********* Helper Methods --------------- */

    private boolean syncAllowed(final ResourceEntity resource) {
        boolean retVal = false;
        if (resource != null) {
            retVal = StringUtils.equalsIgnoreCase(
                    getResourceProperty(resource, "INCLUDE_IN_PASSWORD_SYNC"),
                    "N");
        }
        return retVal;
    }

    private String getResourceProperty(final ResourceEntity resource,
            final String propertyName) {
        String retVal = null;
        if (resource != null && StringUtils.isNotBlank(propertyName)) {
            final ResourcePropEntity property = resource
                    .getResourceProperty(propertyName);
            if (property != null) {
                retVal = property.getPropValue();
            }
        }
        return retVal;
    }

    @Deprecated
    private String getResProperty(Set<ResourceProp> resPropSet,
            String propertyName) {
        String value = null;

        if (resPropSet == null) {
            return null;
        }
        Iterator<ResourceProp> propIt = resPropSet.iterator();
        while (propIt.hasNext()) {
            ResourceProp prop = propIt.next();
            if (prop.getName().equalsIgnoreCase(propertyName)) {
                return prop.getPropValue();
            }
        }

        return value;
    }

    private Set<Resource> getResourcesForRoles(Set<Role> roleSet) {

        log.debug("GetResourcesForRole().....");
        final Set<Resource> resourceList = new HashSet<Resource>();
        if (CollectionUtils.isNotEmpty(roleSet)) {
            for (Role rl : roleSet) {
                if (rl.getRoleId() != null) {
                    List<ResourceEntity> resources = resourceService.getResourcesForRole(rl.getRoleId(), -1, -1);
                    if (CollectionUtils.isNotEmpty(resources)) {
                        resourceList.addAll(resourceDozerConverter.convertToDTOList(resources, false));
        }
                }
                }
            }
            return resourceList;
        }

    private List<Resource> adjustForOverlappingResource(
            List<Resource> resourceList, List<Resource> deleteResourceList) {

        List<Resource> newDelResList = new LinkedList<Resource>();

        if ((deleteResourceList != null && !deleteResourceList.isEmpty())
                && (resourceList == null || resourceList.isEmpty())) {

            // delete resource list is correct and no adjustment is required
            return deleteResourceList;

        }

        if ((deleteResourceList != null && !deleteResourceList.isEmpty())
                && (resourceList != null && !resourceList.isEmpty())) {
            for (Resource r : resourceList) {
                boolean found = false;
                for (Resource delRes : deleteResourceList) {

                    if (r.getResourceId().equalsIgnoreCase(
                            delRes.getResourceId())) {
                        found = true;

                    }
                }
                if (!found) {
                    newDelResList.add(r);

                }
            }
        }
        return newDelResList;
    }

    private void applyResourceExceptions(ProvisionUser user,
            List<Resource> addResourceList, List<Resource> deleteResourceList) {
        List<UserResourceAssociation> userResAssocList = user
                .getUserResourceList();

        if (userResAssocList == null || userResAssocList.isEmpty()) {
            return;
        }

        for (UserResourceAssociation ura : userResAssocList) {

            if (ura.getOperation() == AttributeOperationEnum.DELETE) {

                log.debug("Adding resource " + ura.getResourceId()
                        + " to the delete list ");

                // add this resource to the delete list
                if (!resourceExists(ura.getResourceId(), deleteResourceList)) {

                    if (ura.getManagedSystemId() == null) {

                        Resource resObj = resourceDataService.getResource(ura
                                .getResourceId());
                        ura.setManagedSystemId(resObj.getManagedSysId());

                    }

                    log.debug(" - Adding to deleteResourceList " + ura);
                    deleteResourceList.add(new Resource(ura.getResourceId(),
                            ura.getManagedSystemId()));

                }

            } else if (ura.getOperation() == AttributeOperationEnum.ADD) {
                // add this resource to the delete list
                if (!resourceExists(ura.getResourceId(), addResourceList)) {

                    if (ura.getManagedSystemId() == null) {

                        Resource resObj = resourceDataService.getResource(ura
                                .getResourceId());
                        ura.setManagedSystemId(resObj.getManagedSysId());

                    }

                    addResourceList.add(new Resource(ura.getResourceId(), ura
                            .getManagedSystemId()));
                }
            }
        }
    }

    private void addDirectResourceAssociation(ProvisionUser user,
            List<Resource> resourceList) {

        log.debug("addDirectResourceAssociation: Adding resources to list directly.");

        List<UserResourceAssociation> userResAssocList = user
                .getUserResourceList();

        if (userResAssocList == null || userResAssocList.isEmpty()) {
            return;
        }

        for (UserResourceAssociation ura : userResAssocList) {

            if (resourceExists(ura.getResourceId(), resourceList)) {

                if (ura.getOperation() == AttributeOperationEnum.DELETE) {

                    for (Resource r : resourceList) {
                        if (ura.getResourceId().equalsIgnoreCase(
                                r.getResourceId())) {
                            resourceList.remove(r);

                            log.debug("Removing resource from resource list - "
                                    + ura.getResourceId());
                        }
                    }
                }

            } else {
                // resource is not current list
                if (ura.getOperation() == AttributeOperationEnum.ADD) {

                    if (ura.getManagedSystemId() == null) {

                        log.debug("addDirectResourceAssociation: URA=" + ura);

                        if (ura.getResourceId() != null) {

                            Resource resObj = resourceDataService
                                    .getResource(ura.getResourceId());

                            if (resObj != null) {

                                ura.setManagedSystemId(resObj.getManagedSysId());

                            }
                        }
                    }

                    if (ura.getResourceId() != null
                            && ura.getManagedSystemId() != null) {

                        log.debug("addDirectResourceAssociation:: Adding resource to resource list - "
                                + ura.getResourceId());

                        resourceList.add(new Resource(ura.getResourceId(), ura
                                .getManagedSystemId()));
                    }
                }
            }
        }
    }

    private boolean resourceExists(String resId, List<Resource> resourceList) {

        if (resourceList == null) {
            return false;
        }

        for (Resource r : resourceList) {
            if (r.getResourceId().equalsIgnoreCase(resId)) {
                return true;
            }

        }
        return false;
    }

    @Override
    @Transactional
    public List<String> getAttributesList(String mSysId,
                                          LookupRequest config) {
        if (mSysId == null)
            return null;
        ManagedSysDto msys = managedSysService.getManagedSys(mSysId);
        if (msys == null)
            return null;
        LookupAttributeResponse response = connectorAdapter
                .lookupAttributes(msys.getConnectorId(), config, MuleContextProvider.getCtx());
        if (StatusCodeType.SUCCESS.equals(response.getStatus())) {
            List<String> attributeNames = new LinkedList<String>();
            for(ExtensibleAttribute attr : response.getAttributes()) {
                attributeNames.add(attr.getName());
            }
            return attributeNames;
        } else  {
            return null;
        }
    }
}
