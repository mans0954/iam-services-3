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
            code = addRoles(user, userEntity, logList);
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
                userMgr.addUserToGroup(g.getGrpId(), newUserId);
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

    private ResponseCode addRoles(ProvisionUser user, UserEntity userEntity, List<IdmAuditLog> logList) {
        List<Role> roleList = user.getMemberOfRoles();
        log.debug("Role list = " + roleList);
        if (roleList != null && roleList.size() > 0) {
            for (Role r: roleList) {
                if (r.getServiceId() == null){
                    r.setServiceId(sysConfiguration.getDefaultSecurityDomain());
                }
                // check if the roleId is valid
                if (r.getRoleId() == null) {
                    return ResponseCode.ROLE_ID_NULL;
                }
                if (roleDataService.getRole(r.getRoleId()) == null ) {
                    return ResponseCode.ROLE_ID_INVALID;
                }
                RoleEntity re = roleDataService.getRole(r.getRoleId());
                userEntity.getRoles().add(re);


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

    public void updateUserEmails(UserEntity origUser, ProvisionUser pUser) {
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

    /*
    public void updateSupervisors(ProvisionUser pUser) {

        String userId = pUser.getUserId();
        Set<User> superiors = pUser.getSuperiors();

        if (superiors == null) {
            return;
        }

        List<SupervisorEntity> supervisorList = userMgr.getSupervisors(userId);

        if (CollectionUtils.isNotEmpty(superiors)) {
            for (User u : superiors) {
                if (userId.equals(u.getUserId())) {
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
                        userMgr.addSuperior(u.getUserId(), userId);
                        log.info(String.format("Adding a supervisor user %s for user %s",
                                u.getUserId(), userId));
                    } catch (Exception e) {
                        log.info(String.format("Can't add a supervisor user %s for user %s",
                                u.getUserId(), userId));
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
                        s.getSupervisor().getUserId(), userId));
            }
        }
    }
    */

    public void updateGroups(UserEntity origUser, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getMemberOfGroups())) {
            for (Group g: pUser.getMemberOfGroups()) {
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
        if (CollectionUtils.isNotEmpty(pUser.getMemberOfRoles())) {
            for (Role r: pUser.getMemberOfRoles()) {
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

   /* public void updateRoleAssociation(String userId, List<Role> origRoleList,
                                      List<Role> newRoleList, List<IdmAuditLog> logList,
                                      ProvisionUser pUser, Login primaryIdentity,
                                      List<Role> activeRoleList, List<Role> deleteRoleList) {

        log.debug("updateRoleAssociation():");
        log.debug("-origRoleList =" + origRoleList);
        log.debug("-newRoleList=" + newRoleList);


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

                // roleDataService.assocUserToRole(userRoleDozerConverter.convertToEntity(ur, true));

                logList.add( auditHelper.createLogObject("ADD ROLE",
                        pUser.getRequestorDomain(),  pUser.getRequestorLogin(),
                        "IDM SERVICE", pUser.getCreatedBy(), "0", "USER", pUser.getUserId(),
                        null, "SUCCESS", null, "USER_STATUS",
                        pUser.getStatus().toString(),
                        "NA", null, null, null, r.getRoleId(),
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
                            "IDM SERVICE", pUser.getCreatedBy(), "0", "USER", pUser.getUserId(),
                            null, "SUCCESS", null, "USER_STATUS",
                            pUser.getStatus().toString(),
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
                            "IDM SERVICE", pUser.getCreatedBy(), "0", "USER", pUser.getUserId(),
                            null, "SUCCESS", null, "USER_STATUS",
                            pUser.getStatus().toString(),
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
                            UserRole usrRl = new UserRole(userId, r.getRoleId());
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
    }*/

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

    public void updateUserOrgAffiliations(UserEntity origUser, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getUserAffiliations())) {
            for (Organization o : pUser.getUserAffiliations()) {
                AttributeOperationEnum operation = o.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    OrganizationEntity org = organizationService.getOrganization(o.getId(), origUser.getUserId());
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

    public void updateResources(UserEntity origUser, ProvisionUser pUser, Set<Resource> resourceSet, Set<Resource> deleteResourceSet) {
        if (CollectionUtils.isNotEmpty(pUser.getUserResourceList())) {
            for (UserResourceAssociation ura : pUser.getUserResourceList()) {
                AttributeOperationEnum operation = ura.getOperation();
                if (operation == null) {
                    return;
                } else if (operation == AttributeOperationEnum.ADD) {
                    ResourceEntity organizationEntity = resourceService.findResourceById(ura.getResourceId());
                    origUser.getResources().add(organizationEntity);
                } else if (operation == AttributeOperationEnum.DELETE) {
                    ResourceEntity organizationEntity = resourceService.findResourceById(ura.getResourceId());
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

    private Login getPrincipal(String logingId, List<Login> loginList) {
        for (Login lg : loginList ) {
            if (lg.getLoginId().equals(logingId)) {
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

    private boolean userRoleAttrEq(Role r, Role origRole) {
        //boolean retval = true;

        if (origRole == null || r ==null) {
            return false;
        }


        if (r.getStatus() != null) {
            if ( !r.getStatus().equalsIgnoreCase(origRole.getStatus()) ) {
                return false;
            }
        }
        if (r.getStartDate() != null) {
            if ( !r.getStartDate().equals(origRole.getStartDate()) ){
                return false;
            }
        }
        if (r.getEndDate() != null) {
            if ( !r.getEndDate().equals(origRole.getEndDate()) ){
                return false;
            }
        }
        return true;
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
            return origLoginList;
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

}
