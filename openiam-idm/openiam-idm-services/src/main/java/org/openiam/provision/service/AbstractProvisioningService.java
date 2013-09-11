package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.mule.util.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.*;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.*;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for the provisioning service
 * User: suneetshah
 */
public abstract class AbstractProvisioningService implements ProvisionService {

    protected static final Log log = LogFactory.getLog(AbstractProvisioningService.class);

    public static final String NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION = "NEW_USER_EMAIL_SUPERVISOR";
    public static final String NEW_USER_EMAIL_NOTIFICATION = "NEW_USER_EMAIL";
    public static final String PASSWORD_EMAIL_NOTIFICATION = "USER_PASSWORD_EMAIL";

    public static final String MATCH_PARAM = "matchParam";
    public static final String TARGET_SYSTEM_IDENTITY_STATUS = "targetSystemIdentityStatus";
    public static final String TARGET_SYSTEM_IDENTITY = "targetSystemIdentity";
    public static final String TARGET_SYSTEM_ATTRIBUTES = "targetSystemAttributes";

    public static final String TARGET_SYS_RES_ID = "resourceId";
    public static final String TARGET_SYS_RES = "RESOURCE";
    public static final String TARGET_SYS_MANAGED_SYS_ID = "managedSysId";
    public static final String TARGET_SYS_SECURITY_DOMAIN = "securityDomain";

    public static final String IDENTITY = "IDENTITY";
    public static final String IDENTITY_NEW = "NEW";
    public static final String IDENTITY_EXIST = "EXIST";

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;
    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter objectMatchDozerConverter;
	@Autowired
	protected ProvisionConnectorService connectorService;
    @Autowired
    protected ResourceService resourceService;
    @Autowired
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ManagedSystemWebService managedSysService;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected GroupDataService groupManager;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    protected OrganizationDataService orgManager;
    @Autowired
    protected OrganizationService organizationService;
    @Autowired
    protected PasswordService passwordManager;
    @Autowired
    protected AuditHelper auditHelper;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected RemoteConnectorAdapter remoteConnectorAdapter;
    @Autowired
    protected ProvisionConnectorWebService provisionConnectorWebService;
    @Autowired
    protected ValidateConnectionConfig validateConnectionConfig;
    @Autowired
    protected PasswordHistoryDAO passwordHistoryDao;
    @Autowired
    protected UserDozerConverter userDozerConverter;
    @Autowired
    ResourceDozerConverter resourceDozerConverter;
    @Autowired
    protected SupervisorDozerConverter supervisorDozerConverter;
    @Autowired
    protected LoginDozerConverter loginDozerConverter;
    @Autowired
    protected RoleDozerConverter roleDozerConverter;
    @Autowired
    protected GroupDozerConverter groupDozerConverter;
    @Autowired
    protected UserAttributeDozerConverter userAttributeDozerConverter;
    @Autowired
    protected PhoneDozerConverter phoneDozerConverter;
    @Autowired
    protected EmailAddressDozerConverter emailAddressDozerConverter;
    @Autowired
    protected AddressDozerConverter addressDozerConverter;
    @Autowired
    protected ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    protected ProvisionConnectorConverter provisionConnectorConverter;
    @Value("${openiam.service_base}")
    protected String serviceHost;
    @Value("${openiam.idm.ws.path}")
    protected String serviceContext;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    @Autowired
    protected String preProcessor;
    @Autowired
    protected String postProcessor;

    protected void checkAuditingAttributes(ProvisionUser pUser) {
        if ( pUser.getRequestClientIP() == null || pUser.getRequestClientIP().isEmpty() ) {
            pUser.setRequestClientIP("NA");
        }
        if ( pUser.getRequestorLogin() == null || pUser.getRequestorLogin().isEmpty() ) {
            pUser.setRequestorLogin("NA");
        }
        if ( pUser.getRequestorDomain() == null || pUser.getRequestorDomain().isEmpty() ) {
            pUser.setRequestorDomain("NA");
        }
        if ( pUser.getCreatedBy() == null || pUser.getCreatedBy().isEmpty() ) {
            pUser.setCreatedBy("NA");
        }
    }

    protected boolean callConnector(Login mLg, String requestId, ManagedSysDto mSys,
                                    ManagedSystemObjectMatch matchObj, ExtensibleUser extUser,
                                    ProvisionConnectorDto connector,
                                    ProvisionUser user, IdmAuditLog idmAuditLog) {

        if (connector.getConnectorInterface() != null &&
                connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

            return remoteAdd(mLg, requestId, mSys, matchObj, extUser, connector);
        }
        return localAdd(mLg, requestId, mSys, matchObj, extUser, user, idmAuditLog);
    }

    protected String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if( managedSys.getPswd()!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

    protected boolean getCurrentObjectAtTargetSystem(Login mLg, ExtensibleUser extUser, ManagedSysDto mSys,
                                                                 ProvisionConnectorDto connector,
                                                                 ManagedSystemObjectMatch matchObj,
                                                                 Map<String, String> curValueMap ) {

        String identity = mLg.getLogin();
        MuleContext muleContext = MuleContextProvider.getCtx();
        log.debug("Getting the current attributes in the target system for =" + identity);

        log.debug("- IsRename: " + mLg.getOrigPrincipalName());

        if (mLg.getOrigPrincipalName() != null && !mLg.getOrigPrincipalName().isEmpty()) {
            identity = mLg.getOrigPrincipalName();
        }

        LookupRequest<ExtensibleUser> reqType = new LookupRequest<ExtensibleUser>();
        String requestId = "R" + UUIDGen.getUUID();
        reqType.setRequestID(requestId);
        reqType.setSearchValue(identity);

        reqType.setTargetID(mLg.getManagedSysId());
        reqType.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        reqType.setHostLoginPassword(passwordDecoded);
        reqType.setHostUrl(mSys.getHostUrl());
        if (matchObj != null)
        	reqType.setBaseDN(matchObj.getBaseDn());
        reqType.setExtensibleObject(extUser);
        reqType.setScriptHandler(mSys.getLookupHandler());

        if (connector.getConnectorInterface() != null &&
                connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {

            SearchResponse lookupRespType = remoteConnectorAdapter.lookupRequest(mSys, reqType, connector, muleContext);

            if (lookupRespType != null && lookupRespType.getStatus() == StatusCodeType.SUCCESS) {
                return true;

            } else {
                log.debug("Attribute lookup did not find a match.");
                return false;
            }

        } else {

            SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
            if(lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
                List<ExtensibleAttribute> extAttrList = lookupSearchResponse.getObjectList().size() > 0 ? lookupSearchResponse.getObjectList().get(0).getAttributeList() : new LinkedList<ExtensibleAttribute>();

                if (extAttrList != null) {
                    for (ExtensibleAttribute obj : extAttrList) {
                        String name = obj.getName();
                        String value = obj.getValue();
                        curValueMap.put(name, value);
                    }
                } else {
                    log.debug(" - NO attributes found in target system lookup ");
                }
            }
        }

        if (curValueMap.size() == 0) {
            return false;
        }
        return true;

    }

/* came with merge from v2.3
        protected void sendPasswordToUser(User user, String password) {

            try {
                MuleClient client = new MuleClient(muleContext);

                HashMap<String, String> msgParamsMap = new HashMap<String, String>();
                msgParamsMap.put(MailTemplateParameters.SERVICE_HOST.value(), serviceHost);
                msgParamsMap.put(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext);
                msgParamsMap.put(MailTemplateParameters.USER_ID.value(), user.getUserId());
                msgParamsMap.put(MailTemplateParameters.PASSWORD.value(), password);
                msgParamsMap.put(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName());
                msgParamsMap.put(MailTemplateParameters.LAST_NAME.value(), user.getLastName());

                Map<String, String> msgProp = new HashMap<String, String>();
                msgProp.put("SERVICE_HOST", serviceHost);
                msgProp.put("SERVICE_CONTEXT", serviceContext);
                client.sendAsync("vm://notifyUserByEmailMessage", new NotificationRequest(PASSWORD_EMAIL_NOTIFICATION, msgParamsMap), msgProp);

            } catch (MuleException me) {
                log.error(me.toString());
            }

        }
*/
    protected void sendResetPasswordToUser(UserEntity user, String principal, String password) {
        try {
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());

            List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getUserId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), principal));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest  notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getUserId());
            notificationRequest.setParamList(msgParams);
            notificationRequest.setNotificationType(PASSWORD_EMAIL_NOTIFICATION);


            client.sendAsync("vm://notifyUserByEmailMessage", notificationRequest, msgProp);

        } catch (MuleException me) {
            log.error(me.toString());
        }

    }

    protected void sendCredentialsToUser(User user, String identity, String password) {

        try {
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());

            List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getUserId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest  notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getUserId());
            notificationRequest.setParamList(msgParams);
            notificationRequest.setNotificationType(NEW_USER_EMAIL_NOTIFICATION);

            client.sendAsync("vm://notifyUserByEmailMessage", notificationRequest, msgProp);

        } catch (MuleException me) {
            log.error(me.toString());
        }

    }

    protected void sendCredentialsToSupervisor(User user, String identity, String password, String name) {

        try {
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());

            List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getUserId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_NAME.value(), name));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getUserId());
            notificationRequest.setNotificationType(NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION);
            notificationRequest.setParamList(msgParams);
            client.sendAsync("vm://notifyUserByEmailMessage",notificationRequest, msgProp);

        } catch (MuleException me) {
            log.error(me.toString());
        }

    }

    public ProvisionUserResponse createUser(ProvisionUser pUser, List<IdmAuditLog> logList) {

        ProvisionUserResponse resp = new ProvisionUserResponse();
        resp.setStatus(ResponseStatus.SUCCESS);
        ResponseCode code;

        UserEntity userEntity = userDozerConverter.convertToEntity(pUser.getUser(), true);
        if(MapUtils.isNotEmpty(userEntity.getUserAttributes())) {
            for(final UserAttributeEntity entity : userEntity.getUserAttributes().values()) {
                if(entity != null) {
                    entity.setUser(userEntity);
                    entity.getElement();
                }
            }
        }
        code = addGroups(pUser, userEntity, logList);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        code = addRoles(pUser, userEntity, logList); //TODO: if we remove this code we can loose assigned collections (e.g. resources) for role, we need find a solution how to keep them
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        code = addAffiliations(pUser, userEntity, logList);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        //TODO: add resources
        try {
            userMgr.addUser(userEntity);
        } catch (Exception e) {
            log.error("Exception while creating user", e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_OTHER);
            return resp;
        }
        code = addSupervisors(pUser, userEntity);
        if (code != ResponseCode.SUCCESS) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(code);
            return resp;
        }
        try {
            addPrincipals(userEntity);
        } catch(EncryptionException e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
            return resp;
        }
        if (userEntity.getUserId() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            return resp;
        }
        log.debug("User id=" + userEntity.getUserId() + " created in openiam repository");

        userMgr.updateUser(userEntity);
        ProvisionUser finalUser = new ProvisionUser(userMgr.getUserDto(userEntity.getUserId()));
        resp.setUser(finalUser);
        return resp;
    }

    private ResponseCode addSupervisors(ProvisionUser u, UserEntity entity) {
        Set<User> superiors = u.getSuperiors();
        if (CollectionUtils.isNotEmpty(superiors)) {
            for (User s : superiors) {
                try {
                    userMgr.addSuperior(s.getUserId(), entity.getUserId());
                    log.info("created user supervisor");

                } catch (Exception e) {
                    return ResponseCode.SUPERVISOR_ERROR;
                }
            }
        }
        return ResponseCode.SUCCESS;
    }

    private void addPrincipals(UserEntity entity) throws EncryptionException {
        if(CollectionUtils.isNotEmpty(entity.getPrincipalList())) {
            for (LoginEntity e: entity.getPrincipalList()) {
                e.setFirstTimeLogin(1);
                e.setIsLocked(0);
                e.setCreateDate(new Date(System.currentTimeMillis()));
                e.setStatus("ACTIVE");
                e.setUserId(entity.getUserId());
                // encrypt the password
//                if (e.getPassword() != null) {
//                    final String pswd = e.getPassword();
//                    e.setPassword(loginManager.encryptPassword(entity.getUserId(), pswd));
//                }
            }
        }
    }

    private ResponseCode addGroups(ProvisionUser user, UserEntity entity, List<IdmAuditLog> logList) {
        Set<Group> groupSet = user.getGroups();

        if (CollectionUtils.isNotEmpty(groupSet)) {
            for ( Group g : groupSet) {
                // check if the group id is valid
                if (g.getGrpId() == null) {
                    return ResponseCode.GROUP_ID_NULL;
                }
                GroupEntity ge = null;
                if (g.getGrpId() != null) {
                    ge = groupManager.getGroup(g.getGrpId());
                }
                if (ge == null) {
                    return ResponseCode.GROUP_ID_NULL;
                }
                entity.getGroups().add(ge);
                // add to audit log
                logList.add( auditHelper.createLogObject("ADD GROUP",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        user.getStatus().toString(),
                        null, null, user.getSessionId(), null, g.getGrpName(),
                        user.getRequestClientIP(), null, null) );

            }
        }
        return ResponseCode.SUCCESS;
    }

    private ResponseCode addRoles(ProvisionUser user, UserEntity entity, List<IdmAuditLog> logList) {
        Set<Role> roleSet = user.getRoles();
        if (CollectionUtils.isNotEmpty(roleSet)) {
            for (Role r: roleSet) {
                if (r.getServiceId() == null){
                    r.setServiceId(sysConfiguration.getDefaultSecurityDomain());
                }
                // check if the roleId is valid
                RoleEntity re = null;
                if (r.getRoleId() != null ) {
                    re = roleDataService.getRole(r.getRoleId());
                }
                if (re == null) {
                    return ResponseCode.ROLE_ID_NULL;
                }
                entity.getRoles().add(re);
                // add to audit log
                logList.add( auditHelper.createLogObject("ADD ROLE",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        user.getStatus().toString(),
                        "NA", null, user.getSessionId(), null, r.getRoleId(),
                        user.getRequestClientIP(), null, null) );

            }
        }
        return ResponseCode.SUCCESS;
    }

    private ResponseCode addAffiliations(ProvisionUser user, UserEntity entity, List<IdmAuditLog> logList) {
        Set<Organization> affiliationSet = user.getAffiliations();
        if (CollectionUtils.isNotEmpty(affiliationSet)) {
            for (Organization org: affiliationSet) {
                // check if the organization Id is valid
                OrganizationEntity oe = null;
                if (org.getId() != null) {
                    oe = organizationService.getOrganization(org.getId(), null);
                }
                if (oe == null) {
                    return ResponseCode.OBJECT_ID_INVALID;
                }
                entity.getAffiliations().add(oe);
                logList.add( auditHelper.createLogObject("ADD AFFILIATION",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        user.getStatus().toString(),
                        "NA", null, user.getSessionId(), null, org.getOrganizationName(),
                        user.getRequestClientIP(), null, null) );

            }
        }
        return ResponseCode.SUCCESS;
    }

    protected Login buildPrimaryPrincipal(Map<String, Object> bindingMap, ScriptIntegration se) {

        List<AttributeMap> policyAttrMap = managedSysService.
                getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());

        log.debug("Building primary identity. ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity = new Login();
            EmailAddress primaryEmail = new EmailAddress();

            // init values
            primaryIdentity.setDomainId(sysConfiguration.getDefaultSecurityDomain());
            primaryIdentity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());

            try {
                for (AttributeMap attr : policyAttrMap) {
                    String output = (String)ProvisionServiceUtil.getOutputFromAttrMap(
                            attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                            if (attr.getAttributeName().equalsIgnoreCase("PRINCIPAL")) {
                                primaryIdentity.setLogin(output);
                            }
                            if (attr.getAttributeName().equalsIgnoreCase("PASSWORD")) {
                                primaryIdentity.setPassword(output);
                            }
                            if (attr.getAttributeName().equalsIgnoreCase("DOMAIN")) {
                                primaryIdentity.setDomainId(output);
                            }
                        }
                        if (objectType.equals("EMAIL")) {
                            primaryEmail.setEmailAddress(output);
                            primaryEmail.setIsDefault(true);
                        }
                    }
                }
            } catch(Exception e) {
                log.error(e);
            }

            return primaryIdentity;

        } else {
            log.debug("- policyAttrMap IS null");
            return null;
        }
    }

    /**
     * Builds the list of principals from the policies that we have defined in the groovy scripts.
     * @param user
     * @param bindingMap
     * @param se
     */
    protected void buildPrimaryPrincipal( ProvisionUser user,
                                       Map<String, Object> bindingMap,
                                       ScriptIntegration se) {

        List<Login> principalList = new ArrayList<Login>();
        List<AttributeMap> policyAttrMap = this.managedSysService.
                getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());

        log.debug("Building primary identity. ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity = new Login();
            EmailAddress primaryEmail = new EmailAddress();

            // init values
            primaryIdentity.setDomainId(sysConfiguration.getDefaultSecurityDomain());
            primaryIdentity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());

            try {
                for (AttributeMap attr : policyAttrMap) {
                    String output = (String)ProvisionServiceUtil.getOutputFromAttrMap(
                            attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                            if (attr.getAttributeName().equalsIgnoreCase("PRINCIPAL")) {
                                primaryIdentity.setLogin(output);
                            }
                            if (attr.getAttributeName().equalsIgnoreCase("PASSWORD")) {
                                primaryIdentity.setPassword(output);
                            }
                            if (attr.getAttributeName().equalsIgnoreCase("DOMAIN")) {
                                primaryIdentity.setDomainId(output);
                            }
                        }
                        if (objectType.equals("EMAIL")) {
                            primaryEmail.setEmailAddress(output);
                            primaryEmail.setIsDefault(true);
                        }
                    }
                }
            } catch(Exception e) {
                log.error(e);
            }
            principalList.add(primaryIdentity);
            user.setPrincipalList(principalList);

           // user.getEmailAddress().add(primaryEmail);

        } else {
            log.debug("- policyAttrMap IS null");
        }
    }

    protected String parseUserPrincipal(List<ExtensibleAttribute> extensibleAttributes) {
        List<AttributeMap> policyAttrMap = managedSysService.
                getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());
        String principalAttributeName = null;
        for (AttributeMap attr : policyAttrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                    if (attr.getAttributeName().equalsIgnoreCase("PRINCIPAL")) {
                        principalAttributeName = attr.getAttributeName();
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(principalAttributeName)) {
            for (ExtensibleAttribute extAttr : extensibleAttributes) {
                if (extAttr.getName().equalsIgnoreCase(principalAttributeName)) {
                    return extAttr.getValue();
                }
            }
        }
        return null;
    }

    /**
     * when a request already contains an identity and password has not been setup, this method generates a password
     * based on our rules.
     * @param user
     * @param bindingMap
     * @param se
     */

    protected void setPrimaryIDPassword( ProvisionUser user,
                                      Map<String, Object> bindingMap,
                                      ScriptIntegration se) {

        // this method should only be the called if the request already contains 1 or more identities

        List<Login> principalList = user.getPrincipalList();
        List<AttributeMap> policyAttrMap = managedSysService.
                getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());
        //List<AttributeMap> policyAttrMap = resourceDataService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId());

        log.debug("setPrimaryIDPassword() ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity =  user.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());

            try {
                for (  AttributeMap attr : policyAttrMap ) {
                    String output = (String)ProvisionServiceUtil.getOutputFromAttrMap(
                            attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                            if (attr.getAttributeName().equalsIgnoreCase("PASSWORD")) {
                                primaryIdentity.setPassword(output);
                            }
                        }
                    }
                }
            }catch(Exception e) {
                log.error(e);
            }
            //primaryIdentity.setId(primaryID);
            //principalList.add(primaryIdentity);
            user.setPrincipalList(principalList);
            //user.getEmailAddress().add(primaryEmail);

        } else {
            log.debug("- policyAttrMap IS null");
        }
    }

    private boolean identityInDomain(String secDomain, List<Login> identityList) {
        for (Login l : identityList) {
            if ( l.getDomainId().equalsIgnoreCase(secDomain)) {
                return true;
            }
        }
        return false;
    }

    protected int callPreProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap ) {

        ProvisionServicePreProcessor addPreProcessScript;
        if ( pUser != null) {
            if (!pUser.isSkipPreprocessor() &&
                    (addPreProcessScript = createProvPreProcessScript(preProcessor, bindingMap)) != null) {
                addPreProcessScript.setMuleContext(MuleContextProvider.getCtx());
                return executeProvisionPreProcess(addPreProcessScript, bindingMap, pUser, null, operation);

            }
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }


    protected int callPostProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap ) {

        ProvisionServicePostProcessor addPostProcessScript;

        if ( pUser != null) {
            if (!pUser.isSkipPostProcessor() &&
                    (addPostProcessScript = createProvPostProcessScript(postProcessor, bindingMap)) != null) {
                addPostProcessScript.setMuleContext(MuleContextProvider.getCtx());
                return executeProvisionPostProcess(addPostProcessScript, bindingMap, pUser, null, operation);

            }
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }

    protected PreProcessor createPreProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PreProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected PostProcessor createPostProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PostProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePreProcessor createProvPreProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (ProvisionServicePreProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePostProcessor createProvPostProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (ProvisionServicePostProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected int executeProvisionPreProcess(ProvisionServicePreProcessor ppScript,
            Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        }

        return 0;
    }

    protected int executeProvisionPostProcess(ProvisionServicePostProcessor ppScript,
                Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        }

        return 0;
    }

    protected int executePreProcess(PreProcessor ppScript,
                Map<String, Object> bindingMap, ProvisionUser user, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap);
        }

        return 0;
    }

    protected int executePostProcess(PostProcessor ppScript,
                Map<String, Object> bindingMap, ProvisionUser user, String operation, boolean success) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.addUser(user, bindingMap, success);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap, success);

        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap, success);

        }

        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap, success);
        }
        
        return 0;
    }

    public void updateEmails(UserEntity origUser, ProvisionUser pUser) {
        // Processing emails
        Set<EmailAddress> emailAddresses = pUser.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            for (EmailAddress e : emailAddresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<EmailAddressEntity> entities = origUser.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (EmailAddressEntity en : entities) {
                            if (en.getEmailId().equals(e.getEmailId())) {
                                origUser.getEmailAddresses().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(origUser);
                    origUser.getEmailAddresses().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<EmailAddressEntity> entities = origUser.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (EmailAddressEntity en : entities) {
                            if (en.getEmailId().equals(e.getEmailId())) {
                                origUser.getEmailAddresses().remove(en);
                                userMgr.evict(en);
                                EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                                entity.setParent(origUser);
                                origUser.getEmailAddresses().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updatePhones(UserEntity origUser, ProvisionUser pUser) {
        // Processing phones
        Set<Phone> phones = pUser.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone e : phones) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<PhoneEntity> entities = origUser.getPhones();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (PhoneEntity en : entities) {
                            if (en.getPhoneId().equals(e.getPhoneId())) {
                                origUser.getPhones().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                    entity.setParent(origUser);
                    origUser.getPhones().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<PhoneEntity> entities = origUser.getPhones();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (PhoneEntity en : entities) {
                            if (en.getPhoneId().equals(e.getPhoneId())) {
                                origUser.getPhones().remove(en);
                                userMgr.evict(en);
                                PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                                entity.setParent(origUser);
                                origUser.getPhones().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateAddresses(UserEntity origUser, ProvisionUser pUser) {
        // Processing addresses
        Set<Address> addresses = pUser.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            for (Address e : addresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<AddressEntity> entities = origUser.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (AddressEntity en : entities) {
                            if (en.getAddressId().equals(e.getAddressId())) {
                                origUser.getAddresses().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(origUser);
                    origUser.getAddresses().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<AddressEntity> entities = origUser.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (AddressEntity en : entities) {
                            if (en.getAddressId().equals(e.getAddressId())) {
                                origUser.getAddresses().remove(en);
                                userMgr.evict(en);
                                AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                                entity.setParent(origUser);
                                origUser.getAddresses().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateUserProperties(UserEntity origUser, ProvisionUser pUser) {
        origUser.updateUser(userDozerConverter.convertToEntity(pUser.getUser(), false));
    }

    public void updateUserAttributes(UserEntity origUser, ProvisionUser pUser) {
        if (pUser.getUserAttributes() != null && !pUser.getUserAttributes().isEmpty()) {
            for (Map.Entry<String, UserAttribute> entry : pUser.getUserAttributes().entrySet()) {
                if (StringUtils.isBlank(entry.getValue().getName())) {
                    throw new IllegalArgumentException("Name can not be empty");
                }
                AttributeOperationEnum operation = entry.getValue().getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.DELETE) {
                    origUser.getUserAttributes().remove(entry.getKey());

                } else if (operation == AttributeOperationEnum.ADD) {
                    if (origUser.getUserAttributes().containsKey(entry.getKey())) {
                        throw new IllegalArgumentException("Attribute with this name alreday exists");
                    }
                    origUser.getUserAttributes().put(entry.getKey(),
                            userAttributeDozerConverter.convertToEntity(entry.getValue(), false));

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    UserAttributeEntity entity = origUser.getUserAttributes().get(entry.getKey());
                    if (entity != null) {
                        origUser.getUserAttributes().remove(entry.getKey());
                        userMgr.evict(entity);
                        origUser.getUserAttributes().put(entry.getKey(),
                                userAttributeDozerConverter.convertToEntity(entry.getValue(), true));
                    }
                }
            }
        }
    }

    public void updateSupervisors(ProvisionUser pUser) {
        // Processing supervisors
        String userId = pUser.getUserId();
        Set<User> superiors = pUser.getSuperiors();

        if (CollectionUtils.isNotEmpty(superiors)) {
            for (User e : superiors) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    List<SupervisorEntity> supervisorList = userMgr.getSupervisors(userId);
                    if (CollectionUtils.isNotEmpty(supervisorList)) {
                        for (SupervisorEntity se : supervisorList) {
                            if (se.getSupervisor().getUserId().equals(e.getUserId())) {
                                userMgr.removeSupervisor(se.getOrgStructureId());
                                log.info(String.format("Removed a supervisor user %s from user %s",
                                        e.getUserId(), userId));
                            }
                        }
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    userMgr.addSuperior(e.getUserId(), userId);
                    log.info(String.format("Adding a supervisor user %s for user %s",
                            e.getUserId(), userId));

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for supervisors");
                }
            }
        }
    }

    public void updateGroups(UserEntity origUser, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getGroups())) {
            for (Group g: pUser.getGroups()) {
                AttributeOperationEnum operation = g.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    GroupEntity groupEntity = groupManager.getGroup(g.getGrpId());
                    origUser.getGroups().add(groupEntity);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    GroupEntity groupEntity = groupManager.getGroup(g.getGrpId());
                    origUser.getGroups().remove(groupEntity);

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for groups");
                }
            }
        }
    }

    public void updateRoles(UserEntity origUser, ProvisionUser pUser,
            Set<Role> roleSet, Set<Role> deleteRoleSet) {
        if (CollectionUtils.isNotEmpty(pUser.getRoles())) {
            for (Role r: pUser.getRoles()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    RoleEntity roleEntity = roleDataService.getRole(r.getRoleId());
                    if (origUser.getRoles().contains(roleEntity)) {
                        throw new IllegalArgumentException("Role with this name alreday exists");
                    }
                    origUser.getRoles().add(roleEntity);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    origUser.getRoles().remove(roleDataService.getRole(r.getRoleId()));
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for roles");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(origUser.getRoles())) {
            for (RoleEntity ure : origUser.getRoles()) {
                roleSet.add(roleDozerConverter.convertToDTO(roleDataService.getRole(ure.getRoleId(),
                        origUser.getUserId()), false));
            }
        }
    }

    /* User Org Affiliation */

    public void updateAffiliations(UserEntity origUser, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getAffiliations())) {
            for (Organization o : pUser.getAffiliations()) {
                AttributeOperationEnum operation = o.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    OrganizationEntity org = organizationService.getOrganization(o.getId(), null);
                    origUser.getAffiliations().add(org);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    Set<OrganizationEntity> affiliations = origUser.getAffiliations();
                    for (OrganizationEntity a : affiliations) {
                        if (o.getId().equals(a.getId())) {
                            origUser.getAffiliations().remove(a);
                            break;
                        }
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for affiliations");
                }
            }
        }
    }

    public void updateResources(UserEntity origUser, ProvisionUser pUser, Set<Resource> resourceSet, Set<Resource> deleteResourceSet) {
        if (CollectionUtils.isNotEmpty(pUser.getResources())) {
            for (Resource r : pUser.getResources()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == null) {
                    return;
                } else if (operation == AttributeOperationEnum.ADD) {
                    ResourceEntity organizationEntity = resourceService.findResourceById(r.getResourceId());
                    origUser.getResources().add(organizationEntity);
                } else if (operation == AttributeOperationEnum.DELETE) {
                    ResourceEntity organizationEntity = resourceService.findResourceById(r.getResourceId());
                    origUser.getResources().remove(organizationEntity);
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for resources");
                }
            }
        }
        for (ResourceEntity rue : origUser.getResources()) {
            ResourceEntity e = resourceService.findResourceById(rue.getResourceId());
            resourceSet.add(resourceDozerConverter.convertToDTO(e, false));
        }
    }

    private Login getPrincipal(String logingId, List<Login> loginList) {
        for (Login lg : loginList ) {
            if (lg.getLoginId().equals(logingId)) {
                return lg;
            }
        }
        return null;
    }

    public void updatePrincipals(UserEntity origUser, ProvisionUser pUser) {
        // Processing principals
        List<Login> principals = pUser.getPrincipalList();
        if (CollectionUtils.isNotEmpty(principals)) {
            for (Login e : principals) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    List<LoginEntity> entities = origUser.getPrincipalList();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (LoginEntity en : entities) {
                            if (en.getLoginId().equals(e.getLoginId())) {
                                origUser.getPrincipalList().remove(en);
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                    try {
                        entity.setPassword(loginManager.encryptPassword(e.getUserId(), e.getPassword()));
                        origUser.getPrincipalList().add(entity);
                    } catch (EncryptionException ee) {
                        log.error(ee);
                        ee.printStackTrace();
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    List<LoginEntity> entities = origUser.getPrincipalList();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (LoginEntity en : entities) {
                            if (en.getLoginId().equals(e.getLoginId())) {
                                origUser.getPrincipalList().remove(en);
                                loginManager.evict(en);
                                LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                                origUser.getPrincipalList().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public LoginEntity getPrimaryIdentity(String managedSysId, List<LoginEntity> principalList) {

        log.debug("Getting identity for ManagedSysId");

        if (principalList == null ||
                principalList.size() == 0) {
            return null;
        }

        log.debug(" - principals ->" + principalList);

        for (LoginEntity l  : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {

                log.debug("getPrimaryIdentity() return ->" + l);

                return l;
            }
        }
        log.debug("getPrimaryIdentity() not found. returning null" );
        return null;
    }

    /**
     * Update the list of attributes with the correct operation values so that they can be
     * passed to the connector
     */
    public ExtensibleUser updateAttributeList(org.openiam.provision.type.ExtensibleUser extUser,
            Map<String,String> currentValueMap ) {
        if (extUser == null) {
            return null;
        }
        log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        log.debug("updateAttributeList: Current attributeMap = " + currentValueMap);


        List<ExtensibleAttribute> extAttrList = extUser.getAttributes();
        if (extAttrList == null) {

            log.debug("Extended user attributes is null");

            return null;
        }

        log.debug("updateAttributeList: New Attribute List = " + extAttrList);
        if ( extAttrList != null && currentValueMap == null) {
            for (ExtensibleAttribute attr  : extAttrList) {
                attr.setOperation(1);
            }
        } else {

            for (ExtensibleAttribute attr  : extAttrList) {
                String nm = attr.getName();
                if (currentValueMap == null) {
                    attr.setOperation(1);
                } else {
                    String curVal = currentValueMap.get(nm);
                    if (curVal == null) {
                        // temp hack
                        if (nm.equalsIgnoreCase("objectclass")) {
                            attr.setOperation(2);
                        } else {
                            log.debug("- Op = 1 - AttrName = " +nm );

                            attr.setOperation(1);
                        }
                    } else {
                        if (curVal.equalsIgnoreCase(attr.getValue())) {
                            log.debug("- Op = 0 - AttrName = " +nm );

                            attr.setOperation(0);
                        } else {

                            log.debug("- Op = 2 - AttrName = " +nm );

                            attr.setOperation(2);
                        }
                    }
                }
            }
        }
        return extUser;
    }

    public ExtensibleUser buildFromRules(ProvisionUser pUser,
                                         Login currentIdentity,
                                         List<AttributeMap> attrMap, ScriptIntegration se,
                                         Map<String, Object> bindingMap) {

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            log.debug("buildFromRules: attrMap IS NOT null");

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }

                Object output = "";
                try {
                    output = ProvisionServiceUtil.getOutputFromAttrMap(attr,
                            bindingMap, se);
                } catch (ScriptEngineException see) {
                    log.error("Error in script = '", see);
                    continue;
                }


                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {

                        log.debug("buildFromRules: OBJECTTYPE="
                                + objectType + " SCRIPT OUTPUT=" + output
                                + " attribute name=" + attr.getAttributeName());

                        if (objectType.equalsIgnoreCase("USER") || objectType.equalsIgnoreCase("PASSWORD")) {
                            if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to the list
                                // the connectors can detect a delete if an attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (String) output, 1, attr.getDataType());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);


                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), 1, attr.getDataType());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            }  else if (output instanceof byte[]) {
                                extUser.getAttributes().add(new ExtensibleAttribute(attr.getAttributeName(), (byte[])output, 1, attr.getDataType()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (List) output, 1, attr.getDataType());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);

                                log.debug("buildFromRules: added attribute to extUser:" + attr.getAttributeName());
                            }
                            }
                        } else if (objectType.equalsIgnoreCase("PRINCIPAL")) {

                            extUser.setPrincipalFieldName(attr.getAttributeName());
                            extUser.setPrincipalFieldDataType(attr.getDataType());

                        }
                }
            }

            if (pUser.getPrincipalList() == null) {
                List<Login> principalList = new ArrayList<Login>();
                principalList.add(currentIdentity);
                pUser.setPrincipalList(principalList);
            } else {
                pUser.getPrincipalList().add(currentIdentity);
            }

        }

        return extUser;
    }

    /* REMOTE VS LOCAL CONNECTORS */

    protected boolean localAdd(Login mLg, String requestId, ManagedSysDto mSys,
                             ManagedSystemObjectMatch matchObj, ExtensibleUser extUser,
                             ProvisionUser user, IdmAuditLog idmAuditLog) {

        CrudRequest<ExtensibleUser> addReqType = new CrudRequest<ExtensibleUser>();

        addReqType.setObjectIdentity(mLg.getLogin());
        addReqType.setRequestID(requestId);
        addReqType.setTargetID(mLg.getManagedSysId());
        addReqType.setExtensibleObject(extUser);
        log.debug("Local connector - Creating identity in target system:" + mLg.getLoginId());
        ObjectResponse resp = connectorAdapter.addRequest(mSys, addReqType, MuleContextProvider.getCtx());

        /*auditHelper.addLog("ADD IDENTITY", user.getRequestorDomain(), user.getRequestorLogin(),
                "IDM SERVICE", user.getCreatedBy(), mLg.getManagedSysId(),
                "USER", user.getUserId(),
                idmAuditLog.getLogId(), resp.getStatus().toString(), idmAuditLog.getLogId(), "IDENTITY_STATUS",
                "SUCCESS",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestorLogin(), mLg.getLogin(), mLg.getDomainId());*/

        return resp.getStatus() != StatusCodeType.FAILURE;
    }

    protected boolean remoteAdd(Login mLg, String requestId, ManagedSysDto mSys,
                                ManagedSystemObjectMatch matchObj, ExtensibleUser extUser,
                                ProvisionConnectorDto connector) {

        log.debug("Calling remote connector " + connector.getName());

        CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
        userReq.setObjectIdentity(mLg.getLogin());
        userReq.setRequestID(requestId);
        userReq.setTargetID(mLg.getManagedSysId());
        userReq.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        userReq.setHostLoginPassword(passwordDecoded);
        userReq.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            userReq.setBaseDN(matchObj.getBaseDn());
        }
        userReq.setOperation("ADD");
        userReq.setExtensibleObject(extUser);

        userReq.setScriptHandler(mSys.getAddHandler());

        ObjectResponse resp = remoteConnectorAdapter.addRequest(mSys, userReq, connector, MuleContextProvider.getCtx());

        if (resp.getStatus() == StatusCodeType.FAILURE) {
            return false;
        }

        return true;
    }

    protected ObjectResponse remoteDelete(
            Login mLg,
            String requestId,
            ManagedSysDto mSys,
            ProvisionConnectorDto connector,
            ManagedSystemObjectMatch matchObj,
            ProvisionUser user,
            IdmAuditLog auditLog
    ) {

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
        if (matchObj != null) {
            request.setBaseDN(matchObj.getBaseDn());
        }
        request.setOperation("DELETE");

        request.setScriptHandler(mSys.getDeleteHandler());

        ObjectResponse resp = remoteConnectorAdapter.deleteRequest(mSys, request, connector, MuleContextProvider.getCtx());

        auditHelper.addLog("DELETE IDENTITY", auditLog.getDomainId(), auditLog.getPrincipal(),
                "IDM SERVICE", user.getCreatedBy(), mLg.getManagedSysId(),
                "IDENTITY", user.getUserId(),
                auditLog.getLogId(), resp.getStatus().toString(), auditLog.getLogId(), "IDENTITY_STATUS",
                "DELETED",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestClientIP(), mLg.getLogin(), mLg.getDomainId());

        return resp;
    }

    protected ResponseType localDelete(Login l, String requestId,
                                     ManagedSysDto mSys,
                                     IdmAuditLog auditLog) {

        log.debug("Local delete for=" + l);

        CrudRequest<ExtensibleUser> reqType = new CrudRequest<ExtensibleUser>();
        reqType.setRequestID(requestId);
        reqType.setObjectIdentity(l.getLogin());

        ResponseType resp = connectorAdapter.deleteRequest(mSys, reqType, MuleContextProvider.getCtx());

        String logid = null;
        String status = null;

        if (resp.getStatus() != null) {
            status = resp.getStatus().toString();
        }

        if (auditLog != null) {
            logid = auditLog.getLogId();
        }

        auditHelper.addLog("DELETE IDENTITY", l.getDomainId(), l.getLogin(),
                "IDM SERVICE", l.getCreatedBy(), l.getManagedSysId(),
                "IDENTITY", l.getUserId(),
                logid, status, logid,
                "IDENTITY_STATUS", "DELETED",
                requestId, resp.getErrorCodeAsStr(), "", resp.getErrorMsgAsStr(),
                l.getLastLoginIP(), l.getLogin(), l.getDomainId());

        return resp;
    }
    
    protected void localResetPassword(final String requestId, 
    								  final LoginEntity login,
    								  final String password, 
    								  final ManagedSysEntity mSys, 
    								  PasswordSync passwordSync) {
    	localResetPassword(requestId, 
    					   loginDozerConverter.convertToDTO(login, true), 
    					   password, 
    					   managedSysDozerConverter.convertToDTO(mSys, true), 
    					   passwordSync);
    }

    protected void localResetPassword(String requestId, Login login,
            String password, ManagedSysDto mSys, PasswordSync passwordSync) {

        PasswordRequest pswdReqType = new PasswordRequest();
        pswdReqType.setObjectIdentity(login.getLogin());
        pswdReqType.setTargetID(mSys.getManagedSysId());
        pswdReqType.setRequestID(requestId);
        pswdReqType.setPassword(password);
        ResponseType respType = connectorAdapter.setPasswordRequest(mSys, pswdReqType, MuleContextProvider.getCtx());

        auditHelper.addLog("RESET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, mSys.getManagedSysId(), "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                null, login.getLogin(), login.getDomainId());
    }
    
    protected void remoteResetPassword(final String requestId, 
    								   final LoginEntity login,
    								   final String password, 
    								   final ManagedSysEntity mSys,
    								   final ManagedSystemObjectMatchEntity matchObj, 
    								   ProvisionConnectorEntity connector,
    								   PasswordSync passwordSync) {
    	remoteResetPassword(requestId, 
    						loginDozerConverter.convertToDTO(login, true),
    						password, 
    						managedSysDozerConverter.convertToDTO(mSys, true), 
    						objectMatchDozerConverter.convertToDTO(matchObj, true),
    						provisionConnectorConverter.convertToDTO(connector, true), 
    						passwordSync);
    }

    protected void remoteResetPassword(String requestId, Login login,
            String password, ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj, ProvisionConnectorDto connector,
            PasswordSync passwordSync) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        req.setBaseDN(matchObj.getBaseDn());
        req.setOperation("RESET_PASSWORD");
        req.setPassword(password);

        req.setScriptHandler(mSys.getPasswordHandler());

        ResponseType respType = remoteConnectorAdapter.resetPasswordRequest(mSys, req, connector, MuleContextProvider.getCtx());

        auditHelper.addLog("RESET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, mSys.getManagedSysId(), "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                passwordSync.getRequestClientIP(), login.getLogin(), login.getDomainId());
    }
    
    protected ResponseType remoteSetPassword(String requestId,
    																	LoginEntity login,
            															PasswordSync passwordSync,
            															ManagedSysEntity mSys,
            															ManagedSystemObjectMatchEntity matchObj,
            															ProvisionConnectorEntity connector) {
    	return remoteSetPassword(requestId,
    			loginDozerConverter.convertToDTO(login, true),
                passwordSync, 
                managedSysDozerConverter.convertToDTO(mSys, true),
                objectMatchDozerConverter.convertToDTO(matchObj, true),
                provisionConnectorConverter.convertToDTO(connector, true));
    }
    protected ResponseType remoteSetPassword(String requestId, Login login,
                                                                      PasswordSync passwordSync,
                                                                      ManagedSysDto mSys,
                                                                      ManagedSystemObjectMatch matchObj,
                                                                      ProvisionConnectorDto connector) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        req.setBaseDN(matchObj.getBaseDn());
        req.setOperation("SET_PASSWORD");
        req.setPassword(passwordSync.getPassword());

        ResponseType respType = remoteConnectorAdapter.setPasswordRequest(mSys, req, connector, MuleContextProvider.getCtx());

        req.setScriptHandler(mSys.getPasswordHandler());

        auditHelper.addLog("SET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, "PASSWORD", "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                passwordSync.getRequestClientIP(), login.getLogin(), login.getDomainId());

        return respType;

    }
    
    protected ResponseType localSetPassword(String requestId, LoginEntity login,
            PasswordSync passwordSync,
            ManagedSysEntity mSys) {
    	return localSetPassword(requestId,
                loginDozerConverter.convertToDTO(login, true), 
                passwordSync, 
                managedSysDozerConverter.convertToDTO(mSys, true));
    }

    protected ResponseType localSetPassword(String requestId, Login login,
                                          PasswordSync passwordSync,
                                          ManagedSysDto mSys) {

        PasswordRequest pswdReqType = new PasswordRequest();
        pswdReqType.setObjectIdentity(login.getLogin());
        pswdReqType.setTargetID(mSys.getManagedSysId());
        pswdReqType.setRequestID(requestId);
        pswdReqType.setPassword(passwordSync.getPassword());

        // add the extensible attributes is they exist

        ResponseType respType = connectorAdapter.setPasswordRequest(mSys, pswdReqType, MuleContextProvider.getCtx());

        auditHelper.addLog("SET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, "PASSWORD", "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                passwordSync.getRequestClientIP(), login.getLogin(), login.getDomainId());
        return respType;
    }

}
