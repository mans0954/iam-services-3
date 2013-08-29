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
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for the provisioning service
 * User: suneetshah
 */
public abstract class AbstractProvisioningService implements ProvisionService, ApplicationContextAware {

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
    protected DeprovisionSelectedResourceHelper deprovisionSelectedResource;
    @Autowired
    protected UserDozerConverter userDozerConverter;
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
    protected UserRoleDozerConverter userRoleDozerConverter;
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

    protected String preProcessor;
    protected String postProcessor;

    // used to inject the application context into the groovy scripts
    protected static ApplicationContext ac;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

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

        public ProvisionUserResponse createUser(ProvisionUser user, List<IdmAuditLog> logList) {

            ProvisionUserResponse resp = new ProvisionUserResponse();
            resp.setStatus(ResponseStatus.SUCCESS);
            ResponseCode code;

            User newUser = user.getUser();
            UserEntity userEntity = userDozerConverter.convertToEntity(newUser, true);
            if(MapUtils.isNotEmpty(userEntity.getUserAttributes())) {
            	for(final UserAttributeEntity entity : userEntity.getUserAttributes().values()) {
            		if(entity != null) {
            			entity.setUser(userEntity);
                        entity.getElement();
            		}
            	}
            }
            try {
                userMgr.addUser(userEntity);
                newUser.setUserId(userEntity.getUserId());
            } catch (Exception e) {
                log.error("Exception while creating user", e);
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_OTHER);
                return resp;
            }

            if (newUser == null || newUser.getUserId() == null) {
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
                return resp;
            }
            user.setUserId(newUser.getUserId());
            log.debug("User id=" + newUser.getUserId() + " created in openiam repository");

            code = addSupervisors(user);
            if (code != ResponseCode.SUCCESS) {
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(code);
                return resp;
            }

            try {
                addPrincipals(user, user.getUserId());
            } catch(EncryptionException e) {
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

    private boolean containsEmail(String name, Set<EmailAddress> emailAddressSet) {

        if (emailAddressSet == null || emailAddressSet.isEmpty()) {
            return false;
        }

        for (EmailAddress e : emailAddressSet) {
            if (e.getName() != null && e.getName().equalsIgnoreCase(name)) {
                return true;
            }

        }
        return false;

    }

    private boolean containsPhone(String name, Set<Phone> phoneSet) {

        if (phoneSet == null || phoneSet.isEmpty()) {
            return false;
        }

        for (Phone p : phoneSet) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return true;
            }

        }
        return false;

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

    private void addPrincipals(final ProvisionUser u, final String userId) throws EncryptionException {
        List<Login> principalList = u.getPrincipalList();
        if(CollectionUtils.isNotEmpty(principalList)) {
            for (final Login lg: principalList) {
                lg.setFirstTimeLogin(1);
                lg.setIsLocked(0);
                lg.setCreateDate(new Date(System.currentTimeMillis()));
                lg.setUserId(userId);
                lg.setStatus("ACTIVE");
                // encrypt the password
                if (lg.getPassword() != null) {
                    final String pswd = lg.getPassword();
                    lg.setPassword(loginManager.encryptPassword(userId, pswd));
                }
                loginManager.addLogin(loginDozerConverter.convertToEntity(lg, true));
            }
        }
    }

    private ResponseCode addGroups(ProvisionUser user, String newUserId, List<IdmAuditLog> logList) {
        List<Group> groupList = user.getMemberOfGroups();

        if (groupList != null) {
            for ( Group g : groupList) {
                // check if the group id is valid
                if (g.getGrpId() == null) {
                    return ResponseCode.GROUP_ID_NULL;
                }
                if ( groupManager.getGroup(g.getGrpId()) == null)  {
                    if (g.getGrpId() == null) {
                        return ResponseCode.GROUP_ID_NULL;
                    }
                }
                g.setOperation(AttributeOperationEnum.ADD);
                groupManager.addUserToGroup(g.getGrpId(), newUserId);
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

    private ResponseCode addRoles(ProvisionUser user, String newUserId, List<IdmAuditLog> logList) {
        List<Role> roleList = user.getMemberOfRoles();
        log.debug("Role list = " + roleList);
        if (roleList != null && roleList.size() > 0) {
            for (Role r: roleList) {
                // check if the roleId is valid
                if (r.getServiceId() == null || r.getRoleId() == null) {
                    return ResponseCode.ROLE_ID_NULL;
                }
                if (roleDataService.getRole(r.getRoleId()) == null ) {
                    return ResponseCode.ROLE_ID_INVALID;
                }

                UserRole ur = new UserRole(newUserId, r.getRoleId());

                if ( r.getStartDate() != null) {
                    ur.setStartDate(r.getStartDate());
                }
                if ( r.getEndDate() != null ) {
                    ur.setEndDate(r.getEndDate());
                }
                roleDataService.assocUserToRole(userRoleDozerConverter.convertToEntity(ur, true));

                logList.add( auditHelper.createLogObject("ADD ROLE",
                        user.getRequestorDomain(), user.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        user.getStatus().toString(),
                        "NA", null, user.getSessionId(), null, ur.getRoleId(),
                        user.getRequestClientIP(), null, null) );

            }
        }
        return ResponseCode.SUCCESS;
    }

    private ResponseCode addAffiliations(ProvisionUser user, String newUserId, List<IdmAuditLog> logList) {
        List<Organization> affiliationList = user.getUserAffiliations();
        log.debug("addAffiliations:Affiliation List list = " + affiliationList);
        if (affiliationList != null && affiliationList.size() > 0) {
            for (Organization org: affiliationList) {
                // check if the roleId is valid
                if (org.getId() == null) {
                    return ResponseCode.OBJECT_ID_INVALID;
                }
                orgManager.addUserToOrg(org.getId(), user.getUserId());

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

    private void addIdentity(String secDomain, Login primaryIdentity) {
        if ( loginManager.getLoginByManagedSys(secDomain,
                primaryIdentity.getLogin(), primaryIdentity.getManagedSysId()) == null ){

            Login newLg = new Login();

            newLg.setDomainId(secDomain);
            newLg.setLogin(primaryIdentity.getLogin());
            newLg.setManagedSysId(primaryIdentity.getManagedSysId());
            newLg.setAuthFailCount(0);
            newLg.setFirstTimeLogin(primaryIdentity.getFirstTimeLogin());
            newLg.setIsLocked(primaryIdentity.getIsLocked());
            newLg.setLastAuthAttempt(primaryIdentity.getLastAuthAttempt());
            newLg.setGracePeriod(primaryIdentity.getGracePeriod());
            newLg.setPassword(primaryIdentity.getPassword());
            newLg.setPasswordChangeCount(primaryIdentity.getPasswordChangeCount());
            newLg.setStatus(primaryIdentity.getStatus());
            newLg.setIsLocked(primaryIdentity.getIsLocked());
            newLg.setOrigPrincipalName(primaryIdentity.getOrigPrincipalName());
            newLg.setUserId(primaryIdentity.getUserId());
            newLg.setResetPassword(primaryIdentity.getResetPassword());

            log.debug("Adding identity = " + newLg);

            loginManager.addLogin(loginDozerConverter.convertToEntity(newLg, true));
        }
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

    protected String updateUser(ProvisionUser user, User origUser) {

        String requestId = UUIDGen.getUUID();

        log.debug("ModifyUser: updateUser called.");

        User newUser = user.getUser();

        updateUserObject(origUser, newUser);

        log.debug("User object pending update:" + origUser);

        userMgr.updateUserFromDto(origUser);

        return requestId;
    }

    public void updateUserObject(User origUser, User newUser) {

        origUser.updateUser(newUser);

        updateUserEmail(origUser, newUser);
        updatePhone(origUser, newUser);
        updateAddress(origUser, newUser);
    }

    private void updateUserEmail(User origUser, User newUser) {
        Set<EmailAddress> origEmailSet = origUser.getEmailAddresses();
        Set<EmailAddress> newEmailSet = newUser.getEmailAddresses();

        if (origEmailSet == null && newEmailSet != null) {
            log.debug("New email list is not null");
            origEmailSet = new HashSet<EmailAddress>();
            origEmailSet.addAll(newEmailSet);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (EmailAddress em  : newEmailSet) {
                em.setOperation(AttributeOperationEnum.ADD);
            }
            return;
        }

        if ( (origEmailSet != null && origEmailSet.size() > 0 ) && (newEmailSet == null || newEmailSet.size() == 0 )) {
            log.debug("orig email list is not null and nothing was passed in for the newEmailSet - ie no change");
            for (EmailAddress em  : origEmailSet) {
                em.setOperation(AttributeOperationEnum.DELETE);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        if (newEmailSet != null) {
            for (EmailAddress em : newEmailSet) {
                if (em.getOperation() == AttributeOperationEnum.DELETE) {
                    // get the email object from the original set of emails so that we can remove it
                    EmailAddress e = getEmailAddress(em.getEmailId(), origEmailSet);
                    if (e != null) {
                        origEmailSet.remove(e);
                    }

                } else {
                    // check if this address is in the current list
                    // if it is - see if it has changed
                    // if it is not - add it.
                    EmailAddress origEmail =  getEmailAddress(em.getEmailId(), origEmailSet);
                    if (origEmail == null) {
                        em.setOperation(AttributeOperationEnum.ADD);
                        origEmailSet.add(em);

                        log.debug("EMAIL ADDRESS -> ADD NEW ADDRESS = " + em.getEmailAddress() );

                    } else {
                        if (em.equals(origEmail)) {
                            // not changed
                            em.setOperation(AttributeOperationEnum.NO_CHANGE);
                            log.debug("EMAIL ADDRESS -> NO CHANGE = " + em.getEmailAddress() );
                        } else {
                            // object changed
                            origEmail.updateEmailAddress(em);
                            origEmailSet.add(origEmail);
                            origEmail.setOperation(AttributeOperationEnum.REPLACE);
                            log.debug("EMAIL ADDRESS -> REPLACE = " + em.getEmailAddress() );
                        }
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        for (EmailAddress e : origEmailSet) {
            if (e.getEmailId() != null) {
                EmailAddress newEmail =  getEmailAddress(e.getEmailId(), newEmailSet);
                if (newEmail == null) {
                    e.setOperation(AttributeOperationEnum.DELETE);
                }
            }
        }
    }

    private void updatePhone(User origUser, User newUser) {
        Set<Phone> origPhoneSet = origUser.getPhones();
        Set<Phone> newPhoneSet = newUser.getPhones();

        if (origPhoneSet == null && newPhoneSet != null) {
            log.debug("New email list is not null");
            origPhoneSet = new HashSet<Phone>();
            origPhoneSet.addAll(newPhoneSet);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (Phone ph : newPhoneSet) {
                ph.setOperation(AttributeOperationEnum.ADD);
            }
            return;
        }

        if ( (origPhoneSet != null && origPhoneSet.size() > 0 ) && (newPhoneSet == null || newPhoneSet.size() == 0 )) {
            log.debug("orig phone list is not null and nothing was passed in for the newPhoneSet - ie no change");
            for (Phone ph  : origPhoneSet) {
                ph.setOperation(AttributeOperationEnum.DELETE);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        if ( newPhoneSet != null) {
            for (Phone ph : newPhoneSet) {
                if (ph.getOperation() == AttributeOperationEnum.DELETE) {

                    // get the email object from the original set of emails so that we can remove it
                    Phone e = getPhone(ph.getPhoneId(), origPhoneSet);
                    if (e != null) {
                        origPhoneSet.remove(e);
                    }
                } else {
                    // check if this address is in the current list
                    // if it is - see if it has changed
                    // if it is not - add it.

                    Phone origPhone =  getPhone(ph.getPhoneId(), origPhoneSet);
                    if (origPhone == null) {
                        ph.setOperation(AttributeOperationEnum.ADD);
                        origPhoneSet.add(ph);
                    } else {
                        if (ph.equals(origPhone)) {
                            // not changed
                            ph.setOperation(AttributeOperationEnum.NO_CHANGE);

                        } else {
                            // object changed
                            origPhone.updatePhone(ph);
                            origPhoneSet.add(origPhone);
                            origPhone.setOperation(AttributeOperationEnum.REPLACE);

                        }
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        for (Phone ph : origPhoneSet) {
            if (ph.getPhoneId() != null) {
                Phone newPhone =  getPhone(ph.getPhoneId(), newPhoneSet);
                if (newPhone == null) {
                    ph.setOperation(AttributeOperationEnum.DELETE);

                }
            }
        }
    }

    private void updateAddress(User origUser, User newUser) {
        Set<Address> origAddressSet = origUser.getAddresses();
        Set<Address> newAddressSet = newUser.getAddresses();

        if (origAddressSet == null && newAddressSet != null) {
            log.debug("New email list is not null");
            origAddressSet = new HashSet<Address>();
            origAddressSet.addAll(newAddressSet);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (Address ph : newAddressSet) {
                ph.setOperation(AttributeOperationEnum.ADD);

            }
            return;
        }

        if ( (origAddressSet != null && origAddressSet.size() > 0 ) &&
                (newAddressSet == null || newAddressSet.size() == 0 )) {
            log.debug("orig Address list is not null and nothing was passed in for the newAddressSet - ie no change");
            for (Address ph  : origAddressSet) {
                ph.setOperation(AttributeOperationEnum.DELETE);

            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        for (Address ph : newAddressSet) {
            if (ph.getOperation() == AttributeOperationEnum.DELETE) {

                // get the email object from the original set of emails so that we can remove it
                Address e = getAddress(ph.getAddressId(), origAddressSet);
                if (e != null) {
                    origAddressSet.remove(e);
                }

            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Address");
                Address origAddress =  getAddress(ph.getAddressId(), origAddressSet);
                if (origAddress == null) {
                    ph.setOperation(AttributeOperationEnum.ADD);
                    origAddressSet.add(ph);

                } else {
                    if (ph.equals(origAddress)) {
                        // not changed
                        ph.setOperation(AttributeOperationEnum.NO_CHANGE);

                    } else {
                        // object changed
                        origAddress.updateAddress(ph);
                        origAddressSet.add(origAddress);
                        origAddress.setOperation(AttributeOperationEnum.REPLACE);

                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        for (Address a : origAddressSet) {
            if (a.getAddressId() != null) {
                Address newAddress =  getAddress(a.getAddressId(), newAddressSet);
                if (newAddress == null) {
                    a.setOperation(AttributeOperationEnum.DELETE);

                }
            }
        }
    }


    private EmailAddress getEmailAddress(String id, Set<EmailAddress> emailSet) {
        Iterator<EmailAddress> emailIt = emailSet.iterator();
        while (emailIt.hasNext()) {
            EmailAddress email = emailIt.next();
            if (email.getEmailId() != null) {
                if (email.getEmailId().equals(id) && (id != null && id.length() > 0)) {
                    return email;
                }
            }
        }
        return null;

    }

    private Phone getPhone(String id, Set<Phone> phoneSet) {
        Iterator<Phone> phoneIt = phoneSet.iterator();
        while (phoneIt.hasNext()) {
            Phone phone = phoneIt.next();
            if (phone.getPhoneId() != null) {
                if (phone.getPhoneId().equals(id) && (id != null && id.length() > 0)) {
                    return phone;
                }
            }
        }
        return null;

    }

    private Address getAddress(String id, Set<Address> addressSet) {
        Iterator<Address> addressIt = addressSet.iterator();
        while (addressIt.hasNext()) {
            Address adr = addressIt.next();
            if (adr.getAddressId() != null  ) {
                if (adr.getAddressId().equals(id) && (id != null && id.length() > 0)) {
                    return adr;
                }
            }
        }
        return null;

    }

    protected void addMissingUserComponents(ProvisionUser user, User origUser) {

        log.debug("addMissingUserComponents() called.");

        // if the new object is empty, then restore the values that we currently have for the user.
        // allow the scripts to function

        user.updateMissingUserAttributes(origUser);

        /*
        // check addresses
        Set<Address> addressSet = user.getAddresses();

        if (addressSet == null || addressSet.isEmpty()) {

            log.debug("- Adding original addressSet to the user object");

            List<AddressEntity> addressList = userMgr.getAddressList(user.getUserId());
            if (addressList != null && !addressList.isEmpty()) {

                user.setAddresses(new HashSet<Address>(addressDozerConverter.convertToDTOList(addressList, false)));

            }
        }

        // check email addresses
        Set<EmailAddress> emailAddressSet =  user.getEmailAddresses();
        if (emailAddressSet == null || emailAddressSet.isEmpty()) {

            log.debug("- Adding original emailSet to the user object");

            List<EmailAddressEntity> emailList =  userMgr.getEmailAddressList(user.getUserId());
            if ( emailList != null && !emailList.isEmpty() ) {

                user.setEmailAddresses( new HashSet<EmailAddress>(emailAddressDozerConverter.
                        convertToDTOList(emailList, false)) );

            }
        }

        // check the phone objects
        Set<Phone> phoneSet = user.getPhones();
        if (phoneSet == null || phoneSet.isEmpty()) {

            log.debug("- Adding original phoneSet to the user object");

            List<PhoneEntity> phoneList  = userMgr.getPhoneList(user.getUserId());
            if ( phoneList != null && !phoneList.isEmpty()) {

                user.setPhones(new HashSet<Phone>(phoneDozerConverter.convertToDTOList(phoneList, false)));

            }
        }
        */

        // check the user attributes
        Map<String, UserAttribute> userAttrSet = user.getUserAttributes();
        if (userAttrSet == null || userAttrSet.isEmpty() ) {

            log.debug("- Adding original user attributes to the user object");

            UserEntity u =  userMgr.getUser(user.getUserId());
            if (  u.getUserAttributes() != null) {
                HashMap<String, UserAttribute> userAttributeMap = new HashMap<String, UserAttribute>();
                for(Map.Entry<String, UserAttributeEntity> entry : u.getUserAttributes().entrySet()) {
                    userAttributeMap.put(entry.getKey(),
                            userAttributeDozerConverter.convertToDTO(entry.getValue(), true));
                }
                user.setUserAttributes(userAttributeMap);
            }

        }


        // the affiliations
        List<Organization> affiliationList =  user.getUserAffiliations();
        if (affiliationList == null || affiliationList.isEmpty()){

            log.debug("- Adding original affiliationList to the user object");

            List<Organization> userAffiliations = orgManager.getOrganizationsForUser(user.getUserId(), null, 0, Integer.MAX_VALUE);
            if (userAffiliations != null && !userAffiliations.isEmpty())  {

                user.setUserAffiliations(userAffiliations);
            }

        }

        // add roles if not part of the request
        List<Role> userRoleList = user.getMemberOfRoles();
        if ( userRoleList == null || userRoleList.isEmpty()) {
            List<RoleEntity> curRoles = roleDataService.getUserRoles(user.getUserId(), null, 0, Integer.MAX_VALUE);
            user.setMemberOfRoles(roleDozerConverter.convertToDTOList(curRoles, false));
        }

    }

    public void updateSupervisors(User user, Set<User> superiors) {

        if (superiors == null) {
            return;
        }

        List<SupervisorEntity> supervisorList = userMgr.getSupervisors(user.getUserId());

        if (CollectionUtils.isNotEmpty(superiors)) {
            for (User u : superiors) {
                if (user.getUserId().equals(u.getUserId())) {
                    log.info("User can't be a superior for himself");
                    continue;
                }

                boolean isToAdd = true;
                for (SupervisorEntity s : supervisorList) {
                    if (s.getSupervisor().getUserId().equals(u.getUserId())) {
                        isToAdd = false; // already exists
                        break;
                    } else if (s.getEmployee().getUserId().equals(u.getUserId())) {
                        isToAdd = false;
                        log.info(String.format("User with id='%s' is a subordinate of User with id='%s'",
                                u.getUserId(), s.getSupervisor().getUserId()));
                        break;
                    }
                }
                if (isToAdd) {
                    try {
                        userMgr.addSuperior(u.getUserId(), user.getUserId());
                        log.info(String.format("Adding a supervisor user %s for user %s",
                                u.getUserId(), user.getUserId()));
                    } catch (Exception e) {
                        log.info(String.format("Can't add a supervisor user %s for user %s",
                                u.getUserId(), user.getUserId()));
                    }
                }
            }
        }

        for (SupervisorEntity s : supervisorList) {
            boolean isToRemove = true;
            if (CollectionUtils.isNotEmpty(superiors)) {
                for (User u : superiors) {
                    if (s.getSupervisor().getUserId().equals(u.getUserId())) {
                        if (u.getOperation() != AttributeOperationEnum.DELETE ) {
                            isToRemove = false;
                            break;
                        }
                    }
                }
            }
            if (isToRemove) {
                userMgr.removeSupervisor(s.getOrgStructureId());
                log.info(String.format("Removed a supervisor user %s from user %s",
                        s.getSupervisor().getUserId(), user.getUserId()));
            }
        }
    }

    public void updateGroupAssociation(String userId, List<Group> origGroupList,  List<Group> newGroupList) {

        log.debug("updating group associations..");
        log.debug("origGroupList =" + origGroupList);
        log.debug("newGroupList=" + newGroupList);

        if ( (origGroupList == null || origGroupList.size() == 0 )  &&
                (newGroupList == null || newGroupList.size() == 0 )) {
            return;
        }

        if ( (origGroupList == null || origGroupList.size() == 0 )  &&
                (newGroupList != null || newGroupList.size() > 0 )) {

            log.debug("New group list is not null");
            origGroupList = new ArrayList<Group>();
            origGroupList.addAll(newGroupList);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (Group g : newGroupList) {
                g.setOperation(AttributeOperationEnum.ADD);
                groupManager.addUserToGroup(g.getGrpId(), userId);
            }
            return;
        }

        if ( (origGroupList != null && origGroupList.size() > 0 ) && (newGroupList == null || newGroupList.size() == 0 )) {
            log.debug("orig group list is not null and nothing was passed in for the newGroupList - ie no change");
            for (Group g  : origGroupList) {
                g.setOperation(AttributeOperationEnum.NO_CHANGE);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        for (Group g : newGroupList) {
            if (g.getOperation() == AttributeOperationEnum.DELETE) {
                log.debug("removing Group :" + g.getGrpId() );
                // get the email object from the original set of emails so that we can remove it
                Group grp = getGroup(g.getGrpId(), origGroupList);
                if (grp != null) {
                    this.groupManager.removeUserFromGroup(grp.getGrpId(), userId);
                }
            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Group");
                Group origGroup =  getGroup(g.getGrpId(), origGroupList);
                if (origGroup == null) {
                    g.setOperation(AttributeOperationEnum.ADD);
                    groupManager.addUserToGroup(g.getGrpId(), userId);
                } else {
                    if (g.getGrpId().equals(origGroup.getGrpId())) {
                        // not changed
                        g.setOperation(AttributeOperationEnum.NO_CHANGE);
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        for (Group g : origGroupList) {
            Group newGroup =  getGroup(g.getGrpId(), newGroupList);
            if (newGroup == null) {
                g.setOperation(AttributeOperationEnum.NO_CHANGE);
            }
        }
    }

    public void updateRoleAssociation(String userId, List<Role> origRoleList,
                                      List<Role> newRoleList, List<IdmAuditLog> logList,
                                      ProvisionUser pUser, Login primaryIdentity,
                                      List<Role> activeRoleList, List<Role> deleteRoleList) {

        log.debug("updateRoleAssociation():");
        log.debug("-origRoleList =" + origRoleList);
        log.debug("-newRoleList=" + newRoleList);

        List<UserRole> currentUserRole = userRoleDozerConverter.convertToDTOList(
                roleDataService.getUserRolesForUser(userId, 0, Integer.MAX_VALUE), false);
        UserEntity user = userMgr.getUser(userId);

        if ( (origRoleList == null || origRoleList.size() == 0 )  &&
                (newRoleList == null || newRoleList.size() == 0 )) {
            return;
        }

        // scneario where the original role list is empty but new roles are passed in on the request
        if ( (origRoleList == null || origRoleList.size() == 0 )  &&
                (newRoleList != null || newRoleList.size() > 0 )) {

            log.debug("New Role list is not null");
            origRoleList = new ArrayList<Role>();
            origRoleList.addAll(newRoleList);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (Role rl : newRoleList) {
                rl.setOperation(AttributeOperationEnum.ADD);
                activeRoleList.add(rl);

                UserRole ur = new UserRole(userId,
                        rl.getRoleId());

                if ( rl.getStartDate() != null) {
                    ur.setStartDate(rl.getStartDate());
                }
                if ( rl.getEndDate() != null ) {
                    ur.setEndDate(rl.getEndDate());
                }
                roleDataService.assocUserToRole(userRoleDozerConverter.convertToEntity(ur, true));

                logList.add( auditHelper.createLogObject("ADD ROLE",
                        pUser.getRequestorDomain(),  pUser.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        user.getStatus().toString(),
                        "NA", null, null, null, ur.getRoleId(),
                        pUser.getRequestClientIP(), primaryIdentity.getLogin(), primaryIdentity.getDomainId()));

                //roleDataService.addUserToRole(rl.getId().getServiceId(), rl.getId().getRoleId(), userId);
            }
            return;
        }

        // roles were originally assigned to this user, but this request does not have any roles.
        // need to ensure that old roles are marked with the no-change operation code.
        if ( (origRoleList != null && origRoleList.size() > 0 ) && (newRoleList == null || newRoleList.size() == 0 )) {
            log.debug("orig Role list is not null and nothing was passed in for the newRoleList - ie no change");
            for (Role r  : origRoleList) {
                r.setOperation(AttributeOperationEnum.NO_CHANGE);
                activeRoleList.add(r);
            }
            return;
        }

        // if in new roleList, but not in old, then add it with operation 1
        // else add with operation 2
        for (Role r : newRoleList) {
            if (r.getOperation() == AttributeOperationEnum.DELETE) {

                log.debug("removing Role :" + r.getRoleId() );

                // get the email object from the original set of emails so that we can remove it
                Role rl = getRole(r.getRoleId(), origRoleList);
                if (rl != null) {
                    roleDataService.removeUserFromRole(rl.getRoleId(), userId);

                    logList.add( auditHelper.createLogObject("REMOVE ROLE",
                            pUser.getRequestorDomain(), pUser.getRequestorLogin(),
                            "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                            null, "SUCCESS", null, "USER_STATUS",
                            user.getStatus().toString(),
                            "NA", null, null, null, rl.getRoleId(),
                            pUser.getRequestClientIP(), primaryIdentity.getLogin(), primaryIdentity.getDomainId()));

                }
                log.debug("Adding role to deleteRoleList =" + rl);
                deleteRoleList.add(rl);

                // need to pass on to connector that a role has been removed so that
                // the connector can also take action on this event.

                activeRoleList.add(r);

            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("Evaluate Role: " + r.getRoleId());

                Role origRole =  getRole(r.getRoleId(), origRoleList);

                log.debug("OrigRole found=" + origRole);

                if (origRole == null) {
                    r.setOperation(AttributeOperationEnum.ADD);
                    activeRoleList.add(r);

                    UserRole ur = new UserRole(userId, r.getRoleId());

                    if ( r.getStartDate() != null) {
                        ur.setStartDate(r.getStartDate());
                    }
                    if ( r.getEndDate() != null ) {
                        ur.setEndDate(r.getEndDate());
                    }
                    roleDataService.assocUserToRole(userRoleDozerConverter.convertToEntity(ur, true));

                    logList.add( auditHelper.createLogObject("ADD ROLE", pUser.getRequestorDomain(), pUser.getRequestorLogin(),
                            "IDM SERVICE", user.getCreatedBy(), "0", "USER", user.getUserId(),
                            null, "SUCCESS", null, "USER_STATUS",
                            user.getStatus().toString(),
                            "NA", null, null, null, ur.getRoleId(),
                            pUser.getRequestClientIP(), primaryIdentity.getLogin(), primaryIdentity.getDomainId()));

                    //roleDataService.addUserToRole(r.getId().getServiceId(), r.getId().getRoleId(), userId);
                } else {
                    // get the user role object
                    log.debug("checking if no_change or replace");
                    //if (r.equals(origRole)) {
                    //UserRole uRole = userRoleAttrEq(r, currentUserRole);
                    if (r.getRoleId().equals(origRole.getRoleId()) && userRoleAttrEq(r, currentUserRole)  ) {
                        // not changed
                        log.debug("- no_change ");
                        r.setOperation(AttributeOperationEnum.NO_CHANGE);
                        activeRoleList.add(r);
                    } else {
                        log.debug("- Attr not eq - replace");
                        r.setOperation(AttributeOperationEnum.REPLACE);
                        activeRoleList.add(r);

                        // object changed
                        //UserRole ur = new UserRole(userId, r.getId().getServiceId(),
                        //		r.getId().getRoleId());
                        UserRole ur = getUserRole(r, currentUserRole);
                        if ( ur != null) {
                            if ( r.getStartDate() != null) {
                                ur.setStartDate(r.getStartDate());
                            }
                            if ( r.getEndDate() != null ) {
                                ur.setEndDate(r.getEndDate());
                            }
                            if ( r.getStatus() != null ) {
                                ur.setStatus(r.getStatus());
                            }
                            roleDataService.updateUserRoleAssoc(userRoleDozerConverter.convertToEntity(ur, true));
                        } else {
                            UserRole usrRl = new UserRole(user.getUserId(), r.getRoleId());
                            roleDataService.assocUserToRole(userRoleDozerConverter.convertToEntity(usrRl, true));

                        }
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        for (Role rl : origRoleList) {
            Role newRole =  getRole(rl.getRoleId(), newRoleList);
            if (newRole == null) {
                rl.setOperation(AttributeOperationEnum.NO_CHANGE);
                activeRoleList.add(rl);
            }
        }
    }

    public List<Role> getActiveRoleList(List<Role> activeRoleList, List<Role> deleteRoleList ) {

        List<Role> rList = new ArrayList<Role>();
        // create a list of roles that are not in the deleted list
        for ( Role r : activeRoleList) {

            boolean found =false;

            log.debug("- Evaluating Role=" + r);

            if (deleteRoleList != null && !deleteRoleList.isEmpty()) {
                for ( Role delRl : deleteRoleList) {

                    log.debug("- Evaluating deleted Role = " + delRl);
                    if ( delRl != null) {

                        if (!found && r.getRoleId().equalsIgnoreCase(delRl.getRoleId())) {
                            found = true;

                            log.debug("- - Deleted Role found = " + delRl);
                        }

                    }
                }
            }
            if (!found) {
                log.debug("- Adding Role to Active Role List=" + r);
                rList.add(r);
            }
        }
        return rList;
    }

    /* User Org Affiliation */

    public void updateUserOrgAffiliation(String userId, List<Organization> newOrgList) {
        List<Organization>  currentOrgList = orgManager.getOrganizationsForUser(userId, null, 0, Integer.MAX_VALUE);

        if (newOrgList == null) {
            return;
        }

        for ( Organization o : newOrgList ) {

            boolean inCurList = isCurrentOrgInNewList(o,currentOrgList);

            if (o.getOperation() == null ||
                    o.getOperation() == AttributeOperationEnum.ADD ||
                    o.getOperation() == AttributeOperationEnum.NO_CHANGE) {

                if (!inCurList) {
                    orgManager.addUserToOrg(o.getId(),userId);
                }

            } else if ( o.getOperation() == AttributeOperationEnum.DELETE ) {
                if (inCurList) {
                    orgManager.removeUserFromOrg(o.getId(),userId);
                }
            }
        }
    }

    private boolean isCurrentOrgInNewList(Organization newOrg, List<Organization> curOrgList) {
        if (curOrgList != null) {
            for ( Organization o : curOrgList) {
                if (o.getId().equals(newOrg.getId())) {

                    return true;
                }
            }
        }

        return false;
    }

    private Group getGroup(String grpId, List<Group> origGroupList) {
        for (Group g : origGroupList ) {
            if (g.getGrpId().equalsIgnoreCase(grpId)) {
                return g;
            }
        }
        return null;
    }

    private Role getRole(String roleId, List<Role> roleList) {
        for (Role rl : roleList ) {
            if (rl.getRoleId().equals(roleId)) {
                return rl;
            }
        }
        return null;
    }

    private Login getPrincipal(String managedSysId, List<Login> loginList) {
        for (Login lg : loginList ) {
            if (lg.getManagedSysId().equals(managedSysId)) {
                return lg;
            }
        }
        return null;
    }

    private boolean notInDeleteResourceList(Login l, List<Resource> deleteResourceList) {
        if (deleteResourceList == null) {
            return true;
        }
        for ( Resource r : deleteResourceList) {
            if (l.getManagedSysId().equalsIgnoreCase(r.getManagedSysId())) {
                return false;
            }
        }
        return true;
    }

    private UserRole getUserRole(Role r, List<UserRole> currentUserRole) {
        //boolean retval = true;

        if (currentUserRole == null) {
            return null;
        }

        // get the user role object
        for (UserRole u : currentUserRole) {
            if (r.getRoleId().equalsIgnoreCase(u.getRoleId())) {
                return u;
            }
        }

        return null;
    }

    private boolean userRoleAttrEq(Role r, List<UserRole> currentUserRole) {
        //boolean retval = true;

        if (currentUserRole == null) {
            return false;
        }

        UserRole ur = null;
        // get the user role object
        for (UserRole u : currentUserRole) {
            if (r.getRoleId().equalsIgnoreCase(u.getRoleId())) {
                ur = u;
            }
        }
        if (ur == null) {
            return false;
        }
        if (r.getStatus() != null) {
            if ( !r.getStatus().equalsIgnoreCase(ur.getStatus()) ) {
                return false;
            }
        }
        if (r.getStartDate() != null) {
            if ( !r.getStartDate().equals(ur.getStartDate()) ){
                return false;
            }
        }
        if (r.getEndDate() != null) {
            if ( !r.getEndDate().equals(ur.getEndDate()) ){
                return false;
            }
        }
        return true;
    }


    /* Update Principal List */

    public List<Login> updatePrincipalList(String userId, List<Login> origLoginList,
                                    List<Login> newLoginList,
                                    List<Resource> deleteResourceList) {

        List<Login> principalList = new ArrayList<Login>();
        log.debug("** updating Principals in modify User.");
        log.debug("- origPrincpalList =" + origLoginList);
        log.debug("- newPrincipalList=" + newLoginList);

        if ( (origLoginList == null || origLoginList.size() == 0 )  &&
                (newLoginList == null || newLoginList.size() == 0 )) {
            return Collections.EMPTY_LIST;
        }

        if ( (origLoginList == null || origLoginList.size() == 0 )  &&
                (newLoginList != null || newLoginList.size() > 0 )) {

            log.debug("New Principal list is not null, but Original Principal List is null");
            origLoginList = new ArrayList<Login>();
            origLoginList.addAll(newLoginList);
            // update the instance variable so that it can passed to the connector with the right operation code
            for (Login lg : newLoginList) {
                lg.setOperation(AttributeOperationEnum.ADD);
                lg.setUserId(userId);
                principalList.add(lg);
                loginManager.addLogin(loginDozerConverter.convertToEntity(lg, true));
            }
            return principalList;
        }

        if ( (origLoginList != null && origLoginList.size() > 0 ) &&
                (newLoginList == null || newLoginList.size() == 0 )) {
            log.debug("orig Principal list is not null and nothing was passed in for the newPrincipal list - ie no change");
            for (Login l  : origLoginList) {
                l.setOperation(AttributeOperationEnum.NO_CHANGE);
                if (notInDeleteResourceList(l,deleteResourceList)) {
                    l.setStatus("ACTIVE");
                    l.setAuthFailCount(0);
                    l.setIsLocked(0);
                    l.setPasswordChangeCount(0);
                    // reset the password from the primary identity
                    // get the primary identity for this user
                    LoginEntity primaryIdentity = loginManager.getPrimaryIdentity(l.getUserId());
                    if (primaryIdentity != null) {
                        log.debug("Identity password reset to: " + primaryIdentity.getPassword());
                        l.setPassword( primaryIdentity.getPassword() );
                    }

                    loginManager.updateLogin(loginDozerConverter.convertToEntity(l, true));
                }
                principalList.add(l);
            }
            return principalList;
        }

        // if in new login, but not in old, then add it with operation 1
        // else add with operation 2
        log.debug("New Principal List is not null and OriginalList is not null - Compare the list of identities.");

        for (Login l : newLoginList) {

            if (l.getOperation() == AttributeOperationEnum.DELETE) {

                log.debug("removing Login :" + l.getLoginId() );
                // get the email object from the original set of emails so that we can remove it
                Login lg = getPrincipal(l.getLoginId(), origLoginList);

                if (lg != null) {
                    lg.setStatus("INACTIVE");
                    loginManager.updateLogin(loginDozerConverter.convertToEntity(lg, true));

                    log.debug("Login updated with status of INACTIVE in IdM database.  ");
                }
                principalList.add(l);

            } else {

                // check if this login is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Login");
                Login origLogin =  getPrincipal(l.getLoginId(), origLoginList);
                log.debug("OrigLogin found=" + origLogin);
                if (origLogin == null) {
                    l.setOperation(AttributeOperationEnum.ADD);
                    l.setUserId(userId);
                    principalList.add(l);
                    loginManager.addLogin(loginDozerConverter.convertToEntity(l, true));

                } else {
                    if (l.getLoginId().equals(origLogin.getLoginId())) {
                        // not changed
                        log.debug("Identities are equal - No Change");
                        log.debug("OrigLogin status=" + origLogin.getStatus());

                        // if the request contains a password, then set the password
                        // as part of the modify request

                        if (l.getPassword() != null && !l.getPassword().equals(origLogin.getPassword())) {
                            // update the password

                            log.debug("Password change detected during synch process");

                            Login newLg = loginDozerConverter.convertDTO(origLogin, true);
                            try {
                                newLg.setPassword(loginManager.encryptPassword(l.getUserId(), l.getPassword()));
                            } catch (EncryptionException e) {
                                log.error(e);
                                e.printStackTrace();
                            }
                            loginManager.changeIdentityName(newLg.getLogin(), newLg.getPassword(),
                                    newLg.getUserId(), newLg.getManagedSysId(), newLg.getDomainId());
                            principalList.add(newLg);
                        } else {
                            log.debug("Updating Identity in IDM repository");
                            if (l.getOperation() == AttributeOperationEnum.REPLACE) {
                                // user set the replace flag
                                loginManager.updateLogin(loginDozerConverter.convertToEntity(l, true));
                                principalList.add(l);
                            } else {

                                log.debug("Flagged identity with NO_CHANGE attribute");

                                l.setOperation(AttributeOperationEnum.NO_CHANGE);
                                principalList.add(l);
                            }
                        }

                    } else {
                        log.debug("Identity changed - RENAME");


                        // clone the object
                        Login newLg = loginDozerConverter.convertDTO(origLogin, true);
                        // add it back with the changed identity
                        newLg.setOperation(AttributeOperationEnum.REPLACE);
                        newLg.setLogin(l.getLogin());

                        //encrypt the password and save it
                        String newPassword = l.getPassword();
                        if (newPassword == null) {
                            newLg.setPassword(null);
                        } else {
                            try {
                                newLg.setPassword(loginManager.encryptPassword(l.getUserId(), newPassword));
                            }catch(EncryptionException e) {
                                log.error(e);
                                e.printStackTrace();
                            }
                        }
                        loginManager.changeIdentityName(newLg.getLogin(), newLg.getPassword(),
                                newLg.getUserId(), newLg.getManagedSysId(), newLg.getDomainId());
                        //loginManager.addLogin(newLg);


                        // we cannot send the encrypted password to the connector
                        // set the password back
                        newLg.setPassword(newPassword);
                        // used the match up the
                        newLg.setOrigPrincipalName(origLogin.getLogin());
                        principalList.add(newLg);
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it on
        log.debug("Check if a value is in the original principal list but not in the new Principal List");
        for (Login lg : origLoginList) {
            Login newLogin =  getPrincipal(lg.getLoginId(), newLoginList);
            if (newLogin == null) {
                lg.setOperation(AttributeOperationEnum.NO_CHANGE);
                principalList.add(lg);
            }
        }

        return principalList;
    }

    public LoginEntity getPrimaryIdentity(String managedSysId, List<LoginEntity> principalList) {

        log.debug("Getting identity for ManagedSysId");

        if (	principalList == null ||
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
     * If the user has selected roles that are in multiple domains, we need to make sure that they identities for
     * each of these domains
     * @param primaryIdentity
     * @param roleList
     */

    public void validateIdentitiesExistforSecurityDomain(Login primaryIdentity, List<Role> roleList) {

        log.debug("validateIdentitiesExistforSecurityDomain");

        List<LoginEntity> identityList = loginManager.getLoginByUser(primaryIdentity.getUserId());
        String managedSysId = primaryIdentity.getManagedSysId();

        log.debug("Identitylist =" + identityList);

        if (roleList != null) {
            for (Role r : roleList) {
                String secDomain = r.getServiceId();
                if (!identityInDomain(secDomain, managedSysId ,identityList)) {

                    log.debug("Adding identity to :" + secDomain);

                    addIdentity(secDomain, primaryIdentity);
                }
            }
        }

        // determine if we should remove an identity
        if (identityList != null) {
            for (LoginEntity l : identityList) {
                if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                    boolean found = false;

                    // possible to have a user with no roles.
                    if (roleList != null) {
                        for ( Role r : roleList) {
                            if (r.getServiceId().equalsIgnoreCase(l.getDomainId())) {
                                found = true ;
                            }
                        }
                    }
                    //TODO check this case was added when StaterBros debug: the ACTIVE status was awerrided with INACTIVE from this condition
                    if (!found) {
                        if ( l.getManagedSysId().equalsIgnoreCase( "0" )) {
                            // primary identity - do not delete. Just disable its status
                            log.debug("Primary identity - chagne its status");
//                            l.setStatus("INACTIVE");
//                            loginManager.updateLogin(l);

                        } else {

                            log.debug("Removing identity for  :" + l.getLoginId() );
                            loginManager.removeLogin(l.getDomainId(), l.getLogin(), l.getManagedSysId());
                        }
                    }
                }
            }
        }
    }

    private boolean identityInDomain(String secDomain, String managedSysId,  List<LoginEntity> identityList) {

        log.debug("IdentityinDomain =" + secDomain + "-" + managedSysId);

        for (LoginEntity l : identityList) {
            if ( l.getDomainId().equalsIgnoreCase(secDomain) &&
                    l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                return true;
            }
        }
        return false;
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

    public void setPreProcessor(String preProcessor) {
        this.preProcessor = preProcessor;
    }

    public void setPostProcessor(String postProcessor) {
        this.postProcessor = postProcessor;
    }
}
