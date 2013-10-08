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
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.base.AbstractBaseService;
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
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
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
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordPolicyProvider;
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
public abstract class AbstractProvisioningService extends AbstractBaseService implements ProvisionService {

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
    protected PasswordPolicyProvider passwordPolicyProvider;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
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
    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;
    @Autowired
    protected ProvisionQueueService provQueueService;

    protected void checkAuditingAttributes(ProvisionUser pUser) {
        if ( pUser.getRequestClientIP() == null || pUser.getRequestClientIP().isEmpty() ) {
            pUser.setRequestClientIP("NA");
        }
        if ( pUser.getRequestorLogin() == null || pUser.getRequestorLogin().isEmpty() ) {
            pUser.setRequestorLogin("NA");
        }
        //if ( pUser.getRequestorDomain() == null || pUser.getRequestorDomain().isEmpty() ) {
        //    pUser.setRequestorDomain("NA");
        //}
        if ( pUser.getCreatedBy() == null || pUser.getCreatedBy().isEmpty() ) {
            pUser.setCreatedBy("NA");
        }
    }

    protected boolean callConnector(Login mLg, String requestId, ManagedSysDto mSys,
                                    ManagedSystemObjectMatch matchObj, ExtensibleUser extUser,
                                    ProvisionConnectorDto connector,
                                    ProvisionUser user) {


            return add(mLg, requestId, mSys, matchObj, extUser);

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
        if (matchObj != null) {
        	reqType.setBaseDN(matchObj.getBaseDn());
        }
        reqType.setExtensibleObject(extUser);
        reqType.setScriptHandler(mSys.getLookupHandler());


        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
        if (lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
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
            return true;
        }

        return false;
    }

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

    protected Login buildPrimaryPrincipal(Map<String, Object> bindingMap, ScriptIntegration se) {

        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId()); // TODO: managedSysId confused with resourceId??
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);

        log.debug("Building primary identity. ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

            Login primaryIdentity = new Login();

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

    protected String parseUserPrincipal(List<ExtensibleAttribute> extensibleAttributes) {
        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId()); // TODO: managedSysId confused with resourceId??
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
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

    protected void buildPrimaryIDPassword(Login primaryIdentity, Map<String, Object> bindingMap,
                                                 ScriptIntegration se) {
        log.debug("setPrimaryIDPassword() ");

        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(sysConfiguration.getDefaultManagedSysId()); // TODO: managedSysId confused with resourceId??
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        if (policyAttrMap != null) {
            log.debug("- policyAttrMap IS NOT null");
            try {
                for (AttributeMap attr : policyAttrMap) {
                    String output = (String)ProvisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (objectType.equalsIgnoreCase("PRINCIPAL")) {
                            if (attr.getAttributeName().equalsIgnoreCase("PASSWORD")) {
                                primaryIdentity.setPassword(output);
                            }
                        }
                    }
                }
            } catch(Exception e) {
                log.error(e);
            }
        } else {
            log.debug("- policyAttrMap IS null");
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

    public void updateEmails(UserEntity userEntity, ProvisionUser pUser) {
        // Processing emails
        Set<EmailAddress> emailAddresses = pUser.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            for (EmailAddress e : emailAddresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (EmailAddressEntity en : entities) {
                            if (en.getEmailId().equals(e.getEmailId())) {
                                userEntity.getEmailAddresses().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getEmailAddresses().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (EmailAddressEntity en : entities) {
                            if (en.getEmailId().equals(e.getEmailId())) {
                                userEntity.getEmailAddresses().remove(en);
                                userMgr.evict(en);
                                EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                userEntity.getEmailAddresses().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updatePhones(UserEntity userEntity, ProvisionUser pUser) {
        // Processing phones
        Set<Phone> phones = pUser.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone e : phones) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (PhoneEntity en : entities) {
                            if (en.getPhoneId().equals(e.getPhoneId())) {
                                userEntity.getPhones().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getPhones().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (PhoneEntity en : entities) {
                            if (en.getPhoneId().equals(e.getPhoneId())) {
                                userEntity.getPhones().remove(en);
                                userMgr.evict(en);
                                PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                userEntity.getPhones().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateAddresses(UserEntity userEntity, ProvisionUser pUser) {
        // Processing addresses
        Set<Address> addresses = pUser.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            for (Address e : addresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (AddressEntity en : entities) {
                            if (en.getAddressId().equals(e.getAddressId())) {
                                userEntity.getAddresses().remove(en);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getAddresses().add(entity);

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (AddressEntity en : entities) {
                            if (en.getAddressId().equals(e.getAddressId())) {
                                userEntity.getAddresses().remove(en);
                                userMgr.evict(en);
                                AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                userEntity.getAddresses().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateUserProperties(UserEntity userEntity, ProvisionUser pUser) {
        userEntity.updateUser(userDozerConverter.convertToEntity(pUser.getUser(), false));
    }

    public void updateUserAttributes(UserEntity userEntity, ProvisionUser pUser) {
        if (pUser.getUserAttributes() != null && !pUser.getUserAttributes().isEmpty()) {
            for (Map.Entry<String, UserAttribute> entry : pUser.getUserAttributes().entrySet()) {
                if (StringUtils.isBlank(entry.getValue().getName())) {
                    throw new IllegalArgumentException("Name can not be empty");
                }
                AttributeOperationEnum operation = entry.getValue().getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.DELETE) {
                    userEntity.getUserAttributes().remove(entry.getKey());

                } else if (operation == AttributeOperationEnum.ADD) {
                    if (userEntity.getUserAttributes().containsKey(entry.getKey())) {
                        throw new IllegalArgumentException("Attribute with this name alreday exists");
                    }
                    UserAttributeEntity e = userAttributeDozerConverter.convertToEntity(entry.getValue(), true);
                    e.setUser(userEntity); // TODO: Maybe it's better to refactor mappings for UserAttributeEntity
                    userEntity.getUserAttributes().put(entry.getKey(), e);

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    UserAttributeEntity entity = userEntity.getUserAttributes().get(entry.getKey());
                    if (entity != null) {
                        userEntity.getUserAttributes().remove(entry.getKey());
                        userMgr.evict(entity);
                        UserAttributeEntity e = userAttributeDozerConverter.convertToEntity(entry.getValue(), true);
                        e.setUser(userEntity);
                        userEntity.getUserAttributes().put(entry.getKey(), e);
                    }
                }
            }
        }
    }

    public void updateSupervisors(UserEntity userEntity, ProvisionUser pUser) {
        // Processing supervisors
        String userId = userEntity.getUserId();
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

    public void updateGroups(UserEntity userEntity, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getGroups())) {
            for (Group g: pUser.getGroups()) {
                AttributeOperationEnum operation = g.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    GroupEntity groupEntity = groupManager.getGroup(g.getGrpId());
                    userEntity.getGroups().add(groupEntity);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    GroupEntity groupEntity = groupManager.getGroup(g.getGrpId());
                    userEntity.getGroups().remove(groupEntity);

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for groups");
                }
            }
        }
    }

    public void updateRoles(UserEntity userEntity, ProvisionUser pUser,
            Set<Role> roleSet, Set<Role> deleteRoleSet) {
        if (CollectionUtils.isNotEmpty(pUser.getRoles())) {
            for (Role r: pUser.getRoles()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    RoleEntity roleEntity = roleDataService.getRole(r.getRoleId());
                    if (userEntity.getRoles().contains(roleEntity)) {
                        throw new IllegalArgumentException("Role with this name alreday exists");
                    }
                    userEntity.getRoles().add(roleEntity);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    RoleEntity re = roleDataService.getRole(r.getRoleId());
                    userEntity.getRoles().remove(re);
                    deleteRoleSet.add(roleDozerConverter.convertToDTO(re, true));

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for roles");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
            for (RoleEntity ure : userEntity.getRoles()) {
                roleSet.add(roleDozerConverter.convertToDTO(roleDataService.getRole(ure.getRoleId(),
                        userEntity.getUserId()), false));
            }
        }
    }

    /* User Org Affiliation */

    public void updateAffiliations(UserEntity userEntity, ProvisionUser pUser) {
        if (CollectionUtils.isNotEmpty(pUser.getAffiliations())) {
            for (Organization o : pUser.getAffiliations()) {
                AttributeOperationEnum operation = o.getOperation();
                if (operation == null) {
                    return;

                } else if (operation == AttributeOperationEnum.ADD) {
                    OrganizationEntity org = organizationService.getOrganization(o.getId(), null);
                    userEntity.getAffiliations().add(org);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    Set<OrganizationEntity> affiliations = userEntity.getAffiliations();
                    for (OrganizationEntity a : affiliations) {
                        if (o.getId().equals(a.getId())) {
                            userEntity.getAffiliations().remove(a);
                            break;
                        }
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for affiliations");
                }
            }
        }
    }

    public void updateResources(UserEntity userEntity, ProvisionUser pUser, Set<Resource> resourceSet, Set<Resource> deleteResourceSet) {
        if (CollectionUtils.isNotEmpty(pUser.getResources())) {
            for (Resource r : pUser.getResources()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == null) {
                    return;
                } else if (operation == AttributeOperationEnum.ADD) {
                    ResourceEntity organizationEntity = resourceService.findResourceById(r.getResourceId());
                    userEntity.getResources().add(organizationEntity);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    ResourceEntity re = resourceService.findResourceById(r.getResourceId());
                    userEntity.getResources().remove(re);
                    deleteResourceSet.add(resourceDozerConverter.convertToDTO(re, true));

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for resources");
                }
            }
        }
        for (ResourceEntity rue : userEntity.getResources()) {
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

    public void updatePrincipals(UserEntity userEntity, ProvisionUser pUser) {
        // Processing principals
        List<Login> principals = pUser.getPrincipalList();
        if (CollectionUtils.isNotEmpty(principals)) {
            for (Login e : principals) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    List<LoginEntity> entities = userEntity.getPrincipalList();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (LoginEntity en : entities) {
                            if (en.getLoginId().equals(e.getLoginId())) {
                                userEntity.getPrincipalList().remove(en);
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                    try {
                        entity.setUserId(userEntity.getUserId());
                        entity.setPassword(loginManager.encryptPassword(userEntity.getUserId(), e.getPassword()));
                        userEntity.getPrincipalList().add(entity);
                    } catch (EncryptionException ee) {
                        log.error(ee);
                        ee.printStackTrace();
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    List<LoginEntity> entities = userEntity.getPrincipalList();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (LoginEntity en : entities) {
                            if (en.getLoginId().equals(e.getLoginId())) {
                                userEntity.getPrincipalList().remove(en);
                                loginManager.evict(en);
                                LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                                userEntity.getPrincipalList().add(entity);
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
                                        (String) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);


                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        sdf.format(d), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            }  else if (output instanceof byte[]) {
                                extUser.getAttributes().add(new ExtensibleAttribute(attr.getAttributeName(),
                                        (byte[])output, 1, attr.getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (List) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);

                                log.debug("buildFromRules: added attribute to extUser:" + attr.getAttributeName());
                            }
                            }
                        } else if (objectType.equalsIgnoreCase("PRINCIPAL")) {

                            extUser.setPrincipalFieldName(attr.getAttributeName());
                            extUser.setPrincipalFieldDataType(attr.getDataType().getValue());

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

    protected boolean add(Login mLg, String requestId, ManagedSysDto mSys,
                                ManagedSystemObjectMatch matchObj, ExtensibleUser extUser) {

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

        ObjectResponse resp = connectorAdapter.addRequest(mSys, userReq, MuleContextProvider.getCtx());

            /*auditHelper.addLog("ADD IDENTITY", user.getRequestorDomain(), user.getRequestorLogin(),
                "IDM SERVICE", user.getCreatedBy(), mLg.getManagedSysId(),
                "USER", user.getUserId(),
                idmAuditLog.getLogId(), resp.getStatus().toString(), idmAuditLog.getLogId(), "IDENTITY_STATUS",
                "SUCCESS",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestorLogin(), mLg.getLogin(), mLg.getDomainId());*/


        return resp.getStatus() != StatusCodeType.FAILURE;
    }

    protected ObjectResponse delete(
            Login mLg,
            String requestId,
            ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj) {

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

        ObjectResponse resp = connectorAdapter.deleteRequest(mSys, request, MuleContextProvider.getCtx());
        /*
        auditHelper.addLog("DELETE IDENTITY", auditLog.getDomainId(), auditLog.getPrincipal(),
                "IDM SERVICE", user.getCreatedBy(), mLg.getManagedSysId(),
                "IDENTITY", user.getUserId(),
                auditLog.getLogId(), resp.getStatus().toString(), auditLog.getLogId(), "IDENTITY_STATUS",
                "DELETED",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestClientIP(), mLg.getLogin(), mLg.getDomainId());
		*/
        return resp;
    }

    protected void resetPassword(String requestId, Login login,
            String password, ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj) {

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

        ResponseType respType = connectorAdapter.resetPasswordRequest(mSys, req, MuleContextProvider.getCtx());
        /*
        auditHelper.addLog("RESET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, mSys.getManagedSysId(), "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                passwordSync.getRequestClientIP(), login.getLogin(), login.getDomainId());
		*/
    }
    
    protected ResponseType setPassword(String requestId, Login login,
                                                                      String newPasswordSync,
                                                                      ManagedSysDto mSys,
                                                                      ManagedSystemObjectMatch matchObj) {

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
        req.setPassword(newPasswordSync);

        ResponseType respType = connectorAdapter.setPasswordRequest(mSys, req, MuleContextProvider.getCtx());

        req.setScriptHandler(mSys.getPasswordHandler());
        /*
        auditHelper.addLog("SET PASSWORD IDENTITY", passwordSync.getRequestorDomain(), passwordSync.getRequestorLogin(),
                "IDM SERVICE", null, "PASSWORD", "PASSWORD", null, null, respType.getStatus().toString(), "NA", null,
                null,
                requestId, respType.getErrorCodeAsStr(), null, respType.getErrorMsgAsStr(),
                passwordSync.getRequestClientIP(), login.getLogin(), login.getDomainId());
		*/
        return respType;

    }

    protected ProvisionUserResponse validatePassword(Login primaryLogin, ProvisionUser user, String requestId) {

        ProvisionUserResponse resp = new ProvisionUserResponse();

        Password password = new Password();
        password.setDomainId(primaryLogin.getDomainId());
        password.setManagedSysId(primaryLogin.getManagedSysId());
        password.setPassword(primaryLogin.getPassword());
        password.setPrincipal(primaryLogin.getLogin());

        Policy passwordPolicy = user.getPasswordPolicy();
        if (passwordPolicy == null) {
            passwordPolicy = passwordPolicyProvider.getPasswordPolicyByUser(primaryLogin.getDomainId(),
                    userDozerConverter.convertToEntity(user.getUser(), true));
        }

        try {
        	PasswordValidationResponse valCode = passwordManager.isPasswordValidForUserAndPolicy(
                    password, userDozerConverter.convertToEntity(
                    user.getUser(), true),
                    loginDozerConverter.convertToEntity(
                            primaryLogin, true), passwordPolicy);
            if (valCode == null || !valCode.isSuccess()) {
            	/*
                auditHelper.addLog("CREATE", user.getRequestorDomain(),
                        user.getRequestorLogin(), "IDM SERVICE",
                        user.getCreatedBy(), "0", "USER", user.getUserId(),
                        null, "FAIL", null, "USER_STATUS", user.getUser()
                        .getStatus().toString(), requestId,
                        ResponseCode.FAIL_DECRYPTION.toString(),
                        user.getSessionId(), "Password validation failed",
                        user.getRequestClientIP(), primaryLogin.getLogin(),
                        primaryLogin.getDomainId());
				*/
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
                return resp;
            }
        } catch (ObjectNotFoundException e) {
        	/*
            auditHelper.addLog("CREATE", user.getRequestorDomain(),
                    user.getRequestorLogin(), "IDM SERVICE",
                    user.getCreatedBy(), "0", "USER", user.getUserId(),
                    null, "FAIL", null, "USER_STATUS", user.getUser()
                    .getStatus().toString(), requestId,
                    ResponseCode.FAIL_DECRYPTION.toString(),
                    user.getSessionId(), e.toString(),
                    user.getRequestClientIP(), primaryLogin.getLogin(),
                    primaryLogin.getDomainId());
			*/
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
            return resp;
        }

        resp.setStatus(ResponseStatus.SUCCESS);
        return resp;
    }

}
