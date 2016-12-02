package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.*;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
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
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenRequest;
import org.openiam.idm.srvc.pswd.dto.PasswordResetTokenResponse;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordPolicyProvider;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.*;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Base class for the provisioning service
 * User: suneetshah
 */
public abstract class AbstractProvisioningService extends AbstractBaseService {

    protected static final Log log = LogFactory.getLog(AbstractProvisioningService.class);

    public static final String NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION = "NEW_USER_EMAIL_SUPERVISOR";
    public static final String NEW_USER_EMAIL_NOTIFICATION = "NEW_USER_EMAIL";
    public static final String NEW_USER_ACTIVATION_NOTIFICATION = "NEW_USER_ACTIVATION_NOTIFICATION";
    public static final String USER_RESET_PASSWORD_ACTIVATION_NOTIFICATION = "USER_RESET_PASSWORD_ACTIVATION_NOTIFICATION";
    public static final String PASSWORD_EMAIL_NOTIFICATION = "USER_PASSWORD_EMAIL";

    public static final String MATCH_PARAM = "matchParam";
    public static final String TARGET_SYSTEM_IDENTITY_STATUS = "targetSystemIdentityStatus";
    public static final String TARGET_SYSTEM_IDENTITY = "targetSystemIdentity";
    public static final String TARGET_SYSTEM_USER_EXISTS = "targetSystemUserExists";
    public static final String TARGET_SYSTEM_ATTRIBUTES = "targetSystemAttributes";

    public static final String TARGET_SYS_RES_ID = "resourceId";
    public static final String TARGET_SYS_RES = "RESOURCE";
    public static final String TARGET_SYS_MANAGED_SYS_ID = "managedSysId";
    public static final String ATTRIBUTE_MAP = "attributeMap";
    public static final String ATTRIBUTE_DEFAULT_VALUE = "attributeDefaultValue";

    public static final String IDENTITY = "IDENTITY";
    public static final String IDENTITY_NEW = "NEW";
    public static final String IDENTITY_EXIST = "EXIST";
    public static final String USER = "user";
    public static final String USER_ATTRIBUTES = "userAttributes";
    public static final String GROUP = "group";

    @Value("${openiam.audit.default.email.change}")
    protected String saveEmailChange;

    @Value("${openiam.audit.rehire}")
    protected String saveRehireChange;

    @Value("${openiam.audit.default.principal.change}")
    protected String savePrincipalChange;

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

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
    protected UserDataService userMgr;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ManagedSystemWebService managedSysDataService;
    @Autowired
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected GroupDataService groupManager;
    @Autowired
    protected SysConfiguration sysConfiguration;
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
    protected OrganizationDozerConverter organizationDozerConverter;
    @Autowired
    protected UserAttributeDozerConverter userAttributeDozerConverter;
    @Autowired
    protected PhoneDozerConverter phoneDozerConverter;
    @Autowired
    protected MailService mailService;
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

    @Value("${org.openiam.idm.postProcessor.cache}")
    protected boolean cachePostProcessorEnable;
    @Value("${org.openiam.idm.preProcessor.cache}")
    protected boolean cachePreProcessorEnable;
    @Value("${org.openiam.user.activation.uri}")
    private String userActivationUri;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    private Map<String, ProvisionServicePreProcessor> preProcessorInstanceMap = new HashMap<String, ProvisionServicePreProcessor>();
    private Map<String, ProvisionServicePostProcessor> postProcessorInstanceMap = new HashMap<String, ProvisionServicePostProcessor>();

    @Autowired
    private String eventProcessor;
    @Autowired
    protected String preProcessor;
    @Autowired
    protected String postProcessor;
    @Autowired
    protected String resourceOrderProcessor;
    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;
    @Autowired
    protected ProvisionQueueService provQueueService;
    @Autowired
    private BuildUserPolicyMapHelper buildPolicyMapHelper;
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected MetadataTypeDAO metadataTypeDAO;
    @Autowired
    protected MetadataService metadataService;
    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;


    @PostConstruct
    public void populateMovieCache() {
        if (cachePreProcessorEnable) {
            try {
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                ProvisionServicePreProcessor<ProvisionUser> preProcessorInstance = preProcessorInstanceMap.get(preProcessor);
                if (preProcessorInstance == null) {
                    preProcessorInstance = (ProvisionServicePreProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, preProcessor);
                    preProcessorInstanceMap.put(preProcessor, preProcessorInstance);
                }
            } catch (Exception exc) {
                log.error(exc);
            }
        }
        if (cachePostProcessorEnable) {
            try {
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                ProvisionServicePostProcessor<ProvisionUser> postProcessorInstance = postProcessorInstanceMap.get(postProcessor);
                if (postProcessorInstance == null) {
                    postProcessorInstance = (ProvisionServicePostProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, postProcessor);
                    postProcessorInstanceMap.put(postProcessor, postProcessorInstance);
                }
            } catch (Exception exc) {
                log.error(exc);
            }
        }
    }


    protected void checkAuditingAttributes(ProvisionUser pUser) {
        if (pUser.getRequestClientIP() == null || pUser.getRequestClientIP().isEmpty()) {
            pUser.setRequestClientIP("N/A");
        }
        if (pUser.getRequestorLogin() == null || pUser.getRequestorLogin().isEmpty()) {
            pUser.setRequestorLogin("N/A");
        }
        //if ( pUser.getRequestorDomain() == null || pUser.getRequestorDomain().isEmpty() ) {
        //    pUser.setRequestorDomain("N/A");
        //}
        if (pUser.getCreatedBy() == null || pUser.getCreatedBy().isEmpty()) {
            pUser.setCreatedBy("N/A");
        }
    }

    protected void sendResetPasswordToUser(LoginEntity identity, String password) {
        try {
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            UserEntity user = userMgr.getUser(identity.getUserId());
            List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), identity.getUserId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity.getLogin()));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getId());
            notificationRequest.setParamList(msgParams);
            notificationRequest.setNotificationType(PASSWORD_EMAIL_NOTIFICATION);


            client.sendAsync("vm://notifyUserByEmailMessage", notificationRequest, msgProp);

        } catch (MuleException me) {
            log.error(me.toString());
        }

    }


    private void sendActivationLink(User user, Login login, String notificationType) {
        try {

            final PasswordResetTokenRequest tokenRequest = new PasswordResetTokenRequest(login.getLogin(), login.getManagedSysId());
            final PasswordResetTokenResponse tokenResponse = passwordManager.generatePasswordResetToken(tokenRequest);

            if (tokenResponse != null && tokenResponse.isSuccess() && tokenResponse.getPasswordResetToken() != null) {
                String token = tokenResponse.getPasswordResetToken();

                MuleClient client = new MuleClient(MuleContextProvider.getCtx());

                List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
                msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
                msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
                msgParams.add(new NotificationParam(MailTemplateParameters.BASE_URL.value(), userActivationUri));
                msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
                msgParams.add(new NotificationParam(MailTemplateParameters.TOKEN.value(), token));
//                msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
//                msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

                Map<String, String> msgProp = new HashMap<String, String>();
                msgProp.put("SERVICE_HOST", serviceHost);
                msgProp.put("SERVICE_CONTEXT", serviceContext);
                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setUserId(user.getId());
                notificationRequest.setParamList(msgParams);
                notificationRequest.setNotificationType(notificationType);
                client.sendAsync("vm://notifyUserByEmailMessage", notificationRequest, msgProp);
            }

        } catch (MuleException me) {
            log.error(me.toString());
        }
    }

    protected void sendActivationLink(User user, Login login) {
        this.sendActivationLink(user, login, NEW_USER_ACTIVATION_NOTIFICATION);
    }

    protected void sendResetActivationLink(User user, Login login) {
        this.sendActivationLink(user, login, USER_RESET_PASSWORD_ACTIVATION_NOTIFICATION);
    }

    protected void sendCredentialsToUser(User user, String identity, String password) {

        try {
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());

            List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
            msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getId());
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
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
            msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
            msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
            msgParams.add(new NotificationParam(MailTemplateParameters.USER_NAME.value(), name));
            msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
            msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

            Map<String, String> msgProp = new HashMap<String, String>();
            msgProp.put("SERVICE_HOST", serviceHost);
            msgProp.put("SERVICE_CONTEXT", serviceContext);
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getId());
            notificationRequest.setNotificationType(NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION);
            notificationRequest.setParamList(msgParams);
            client.sendAsync("vm://notifyUserByEmailMessage", notificationRequest, msgProp);

        } catch (MuleException me) {
            log.error(me.toString());
        }

    }

    protected Login buildPrimaryPrincipal(Map<String, Object> bindingMap, ScriptIntegration se) {
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResourceId());
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);

        if (log.isDebugEnabled()) {
            log.debug("Building primary identity. ");
        }

        if (policyAttrMap != null) {
            if (log.isDebugEnabled()) {
                log.debug("- policyAttrMap IS NOT null");
            }

            Login primaryIdentity = new Login();
            primaryIdentity.setOperation(AttributeOperationEnum.ADD);
            // init values
            primaryIdentity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            primaryIdentity.setProvStatus(ProvLoginStatusEnum.CREATED);
            try {
                AttributeMap primaryIdentityRule = null;
                AttributeMap primaryPasswordRule = null;

                for (AttributeMap attr : policyAttrMap) {
                    if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                        continue;
                    }
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                            if (attr.getAttributeName().equalsIgnoreCase("PRINCIPAL")) {
                                primaryIdentityRule = attr;
                            } else if (attr.getAttributeName().equalsIgnoreCase("PASSWORD")) {
                                primaryPasswordRule = attr;
                            }
                        }
                    }
                }
                // We must be sure that Identity exists in BindingMap before
                // all other scripts like as Password will be processed.
                // Primary identity must be generated first and must be put into BindingMap first.
                if (primaryIdentityRule != null && primaryPasswordRule != null) {
                    String identityOutput = (String) ProvisionServiceUtil.getOutputFromAttrMap(
                            primaryIdentityRule, bindingMap, se);
                    primaryIdentity.setLogin(identityOutput);
                    bindingMap.put(TARGET_SYSTEM_IDENTITY, identityOutput);

                    String passwordOutput = (String) ProvisionServiceUtil.getOutputFromAttrMap(
                            primaryPasswordRule, bindingMap, se);
                    primaryIdentity.setPassword(passwordOutput);

                }
            } catch (Exception e) {
                log.error(e);
            }

            return primaryIdentity;

        } else {
            if (log.isDebugEnabled()) {
                log.debug("- policyAttrMap IS null");
            }
            return null;
        }
    }

    protected String parseUserPrincipal(List<ExtensibleAttribute> extensibleAttributes) {
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResourceId());
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        String principalAttributeName = null;
        for (AttributeMap attr : policyAttrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
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
        if (log.isDebugEnabled()) {
            log.debug("setPrimaryIDPassword() ");
        }
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResourceId());
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        if (policyAttrMap != null) {
            if (log.isDebugEnabled()) {
                log.debug("- policyAttrMap IS NOT null");
            }
            try {
                for (AttributeMap attr : policyAttrMap) {
                    String output = (String) ProvisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                            if ("PASSWORD".equalsIgnoreCase(attr.getAttributeName())) {
                                primaryIdentity.setPassword(output);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("- policyAttrMap IS null");
            }
        }
    }

    protected List<Resource> orderResources(String operation, ProvisionUser pUser, Set<Resource> resources, Map<String, Object> tmpMap) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            ProvisionServiceResourceOrderProcessor script =
                    (ProvisionServiceResourceOrderProcessor) scriptRunner.instantiateClass(bindingMap, resourceOrderProcessor);
            if ("ADD".equalsIgnoreCase(operation)) {
                return script.orderProvisionResources(pUser, resources, bindingMap);

            } else if ("DELETE".equalsIgnoreCase(operation)) {
                return script.orderDeprovisionResources(pUser, resources, bindingMap);

            }
        } catch (Exception ce) {
            log.error(ce);
        }

        return Collections.EMPTY_LIST;
    }

    protected int callPreProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap, PasswordSync passwordSync) {

        ProvisionServicePreProcessor<ProvisionUser> addPreProcessScript = null;
        if (pUser != null) {
            if (log.isDebugEnabled()) {
                log.debug("======= call ProvisionServicePreProcessor: isSkipPreprocessor=" + pUser.isSkipPreprocessor() + ", ");
            }
            try {
                if (!pUser.isSkipPreprocessor() &&
                        (addPreProcessScript = createProvPreProcessScript(preProcessor, bindingMap)) != null) {
                    addPreProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPreProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPreProcess(addPreProcessScript, bindingMap, pUser, passwordSync, operation);

                }
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("======= call ProvisionServicePreProcessor: addPreProcessScript=" + addPreProcessScript + ", ");
                }
            }
        } else {
            //SIA: call to preProcessor on password change
            try {
                if ((operation.equals("RESET_PASSWORD") || operation.equals("SET_PASSWORD")) &&
                        ((addPreProcessScript = createProvPreProcessScript(preProcessor, bindingMap)) != null)) {
                    addPreProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPreProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPreProcess(addPreProcessScript, bindingMap, pUser, passwordSync, operation);
                }
                // Other case: pre-processor was skipped
            } catch (Exception e) {
                log.error(e);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("======= call ProvisionServicePreProcessor: Password: addPreProcessScript=" + addPreProcessScript + ", ");
                }
            }
        }

        return ProvisioningConstants.SUCCESS;
    }


    protected int callPostProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap, PasswordSync passwordSync) {

        ProvisionServicePostProcessor<ProvisionUser> addPostProcessScript = null;

        if (pUser != null) {
            if (log.isDebugEnabled()) {
                log.debug("======= call ProvisionServicePostProcessor: isSkipPostprocessor=" + pUser.isSkipPostProcessor() + ", ");
            }
            try {
                if (!pUser.isSkipPostProcessor() &&
                        (addPostProcessScript = createProvPostProcessScript(postProcessor, bindingMap)) != null) {
                    addPostProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPostProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPostProcess(addPostProcessScript, bindingMap, pUser, passwordSync, operation);
                }
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("======= call ProvisionServicePostProcessor: addPostProcessScript=" + addPostProcessScript + ", ");
                }
            }
        } else {
            //SIA: call to postProcessor on password change
            try {
                if ((operation.equals("RESET_PASSWORD") || operation.equals("SET_PASSWORD")) &&
                        ((addPostProcessScript = createProvPostProcessScript(postProcessor, bindingMap)) != null)) {
                    addPostProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPostProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPostProcess(addPostProcessScript, bindingMap, pUser, passwordSync, operation);
                }
                // Other case: post-processor was skipped
            } catch (Exception e) {
                log.error(e);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("======= call ProvisionServicePostProcessor Password: addPostProcessScript=" + addPostProcessScript + ", ");
                }
            }
        }

        return ProvisioningConstants.SUCCESS;
    }

    protected PreProcessor<ProvisionUser> createPreProcessScript(String scriptName) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            return (PreProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected PostProcessor<ProvisionUser> createPostProcessScript(String scriptName) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            return (PostProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePreProcessor<ProvisionUser> createProvPreProcessScript(String scriptName, Map<String, Object> tmpMap) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            ProvisionServicePreProcessor<ProvisionUser> preProcessorInstance;
            if (cachePreProcessorEnable) {
                preProcessorInstance = preProcessorInstanceMap.get(scriptName);
            } else {
                preProcessorInstance = (ProvisionServicePreProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
            }
            return preProcessorInstance;
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePostProcessor<ProvisionUser> createProvPostProcessScript(String scriptName, Map<String, Object> tmpMap) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            ProvisionServicePostProcessor<ProvisionUser> postProcessorInstance;
            if (cachePostProcessorEnable) {
                postProcessorInstance = postProcessorInstanceMap.get(scriptName);
            } else {
                postProcessorInstance = (ProvisionServicePostProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
            }
            return postProcessorInstance;
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected int executeProvisionPreProcess(ProvisionServicePreProcessor<ProvisionUser> ppScript,
                                             Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        }

        return 0;
    }

    protected int executeProvisionPostProcess(ProvisionServicePostProcessor<ProvisionUser> ppScript,
                                              Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        }
        return 0;
    }

    static int executePreProcess(PreProcessor<ProvisionUser> ppScript,
                                 Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, LookupRequest lookupRequest, String operation) {
        if (log.isDebugEnabled()) {
            log.debug("======= call PreProcessor: ppScript=" + ppScript + ", operation=" + operation);
        }
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        } else if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(lookupRequest);
        }
        return 0;
    }

    static int executePostProcess(PostProcessor<ProvisionUser> ppScript,
                                  Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, SearchResponse searchResponse, String operation, boolean success) {

        if (log.isDebugEnabled()) {
            log.debug("======= call PostProcessor: ppScript=" + ppScript + ", operation=" + operation);
        }
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap, success);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap, success);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap, success);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap, success);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap, success);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap, success);
        } else if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(searchResponse);
        }
        return 0;
    }

    public void updateEmails(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateEmails called -----");
        }
        // Processing emails
        Set<EmailAddress> emailAddresses = pUser.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
            String requestorId = pUser.getRequestorUserId();
            this.manageDefaultEmails(emailAddresses);
            for (EmailAddress e : emailAddresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (EmailAddressEntity en : entities) {
                            if (StringUtils.equals(en.getEmailId(), e.getEmailId())) {
                                userEntity.getEmailAddresses().remove(en);
                                // Audit Log
                                //--------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.DELETE_EMAIL.value());
                                auditLog.setAuditDescription("Delete Email: " + en.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Delete Email: " + en.toString());
                                }
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                        entity.setMetadataType(null);
                    }
                    userEntity.getEmailAddresses().add(entity);
                    // Audit Log
                    //--------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setAction(AuditAction.ADD_EMAIL.value());
                    auditLog.setAuditDescription("Add Email: " + e.toString());
                    parentLog.addChild(auditLog);
                    // -------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add Email: " + e.toString());
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (EmailAddressEntity en : entities) {
                            if (StringUtils.equals(en.getEmailId(), e.getEmailId())) {
                                if (log.isDebugEnabled()) {
                                    log.debug("--------------- Primary Email : " + en.getMetadataType().getId() + " ---------------------------");
                                    log.debug("--------------- saveEmailChange : " + saveEmailChange + " ----------------------");
                                }
                                if (en.getMetadataType().getId().equalsIgnoreCase("PRIMARY_EMAIL") && saveEmailChange.equalsIgnoreCase("true")) {
                                    if (log.isDebugEnabled()) {
                                        log.debug(" adding email changed log ");
                                    }
                                    IdmAuditLog auditLog = new IdmAuditLog();
                                    auditLog.setRequestorUserId(requestorId);
                                    auditLog.setUserId(parentLog.getUserId());
                                    auditLog.setPrincipal(parentLog.getPrincipal());
                                    auditLog.setTargetUser(pUser.getId(), loginStr);
                                    auditLog.setAction(AuditAction.USER_PRIMARY_EMAIL_CHANGED.value());
                                    auditLog.setAuditDescription("Primary Email changed: " + en.toString() + "\n to:" + e.toString());
                                    parentLog.addChild(auditLog);
                                }
                                userEntity.getEmailAddresses().remove(en);
                                userMgr.evict(en);
                                EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                                    entity.setMetadataType(null);
                                }
                                userEntity.getEmailAddresses().add(entity);
                                // Audit Log
                                //--------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.REPLACE_EMAIL.value());
                                auditLog.setAuditDescription("Replace Email: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Replace Email: " + en.toString() + "\n to:" + e.toString());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updatePhones(UserEntity userEntity, ProvisionUser pUser, IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updatePhones called -----");
        }
        // Processing phones
        Set<Phone> phones = pUser.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
            String requestorId = pUser.getRequestorUserId();

            this.manageDefaultPhone(phones);
            for (Phone e : phones) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (PhoneEntity en : entities) {
                            if (StringUtils.equals(en.getPhoneId(), e.getPhoneId())) {
                                userEntity.getPhones().remove(en);
                                //Audit log
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.DELETE_PHONE.value());
                                auditLog.setAuditDescription("Delete Phone: " + e.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Delete Phone: " + e.toString());
                                }
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                        entity.setMetadataType(null);
                    }
                    userEntity.getPhones().add(entity);
                    // Audit log
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setAction(AuditAction.ADD_PHONE.value());
                    auditLog.setAuditDescription("Add Phone: " + e.toString());
                    parentLog.addChild(auditLog);
                    // ----------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add Phone: " + e.toString());
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (PhoneEntity en : entities) {
                            if (StringUtils.equals(en.getPhoneId(), e.getPhoneId())) {
                                userEntity.getPhones().remove(en);
                                userMgr.evict(en);
                                PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                                    entity.setMetadataType(null);
                                }
                                userEntity.getPhones().add(entity);
                                // Audit Log
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.REPLACE_PHONE.value());
                                auditLog.setAuditDescription("Replace Phone: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                //-------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Replace Phone: " + en.toString() + "\n to:" + e.toString());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateAddresses(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateAddresses called -----");
        }
        // Processing addresses
        Set<Address> addresses = pUser.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
            String requestorId = pUser.getRequestorUserId();

            this.manageDefaultAddresses(addresses);
            for (Address e : addresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (AddressEntity en : entities) {
                            if (StringUtils.equals(en.getAddressId(), e.getAddressId())) {
                                userEntity.getAddresses().remove(en);
                                // Audit Log
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.DELETE_ADDRESS.value());
                                auditLog.setAuditDescription("Delete Address: " + en.toString());
                                parentLog.addChild(auditLog);
                                //-------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Delete Address: " + en.toString());
                                }
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                        entity.setMetadataType(null);
                    }
                    userEntity.getAddresses().add(entity);
                    // Audit Log
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setAction(AuditAction.ADD_ADDRESS.value());
                    auditLog.setAuditDescription("Add Address: " + entity.toString());
                    parentLog.addChild(auditLog);
                    //-------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add Address: " + entity.toString());
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (AddressEntity en : entities) {
                            if (StringUtils.equals(en.getAddressId(), e.getAddressId())) {
                                userEntity.getAddresses().remove(en);
                                userMgr.evict(en);
                                AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                                    entity.setMetadataType(null);
                                }
                                userEntity.getAddresses().add(entity);
                                // Audit Log -----------------------------------------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setAction(AuditAction.REPLACE_ADDRESS.value());
                                auditLog.setAuditDescription("Replace Address: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                // ---------------------------------------------------------------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Replace Address: " + en.toString() + "\n to:" + e.toString());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void manageDefaultAddresses(Set<Address> addresses) {
        for (Address a : addresses) {
            switch (a.getOperation()) {
                case ADD:
                    if (a.getIsDefault()) {
                        for (Address another : addresses) {
                            if (a != another && another.getIsDefault()) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                case REPLACE:
                    if (a.getIsDefault()) {
                        for (Address another : addresses) {
                            if (a != another && another.getIsDefault() &&
                                    !AttributeOperationEnum.ADD.equals(another.getOperation())) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void manageDefaultPhone(Set<Phone> phones) {
        for (Phone phone : phones) {
            switch (phone.getOperation()) {
                case ADD:
                    if (phone.getIsDefault()) {
                        for (Phone another : phones) {
                            if (phone != another && another.getIsDefault()) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                case REPLACE:
                    if (phone.getIsDefault()) {
                        for (Phone another : phones) {
                            if (phone != another && another.getIsDefault() &&
                                    !AttributeOperationEnum.ADD.equals(another.getOperation())) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void manageDefaultEmails(Set<EmailAddress> emailAddresses) {
        for (EmailAddress emailAddress : emailAddresses) {
            switch (emailAddress.getOperation()) {
                case ADD:
                    if (emailAddress.getIsDefault()) {
                        for (EmailAddress another : emailAddresses) {
                            if (emailAddress != another && another.getIsDefault()) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                case REPLACE:
                    if (emailAddress.getIsDefault()) {
                        for (EmailAddress another : emailAddresses) {
                            if (emailAddress != another && another.getIsDefault() &&
                                    !AttributeOperationEnum.ADD.equals(another.getOperation())) {
                                another.setIsDefault(false);
                                if (AttributeOperationEnum.NO_CHANGE.equals(another.getOperation())) {
                                    another.setOperation(AttributeOperationEnum.REPLACE);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public void updateUserProperties(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateUserProperties called -----");
        }

        MetadataTypeEntity type = null;
        MetadataTypeEntity jobCode = null;
        MetadataTypeEntity employeeType = null;
        MetadataTypeEntity userSubType = null;

        if (StringUtils.isNotBlank(pUser.getMdTypeId())) {
            type = metadataService.getById(pUser.getMdTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getJobCodeId())) {
            jobCode = metadataService.getById(pUser.getJobCodeId());
        }
        if (StringUtils.isNotBlank(pUser.getEmployeeTypeId())) {
            employeeType = metadataService.getById(pUser.getEmployeeTypeId());
        }

        if (StringUtils.isNotBlank(pUser.getUserSubTypeId())) {
            userSubType = metadataService.getById(pUser.getUserSubTypeId());
        }

        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
        if (login == null && StringUtils.isNotEmpty(pUser.getId())) {
            login = loginDozerConverter.convertToDTO(loginManager.getByUserIdManagedSys(pUser.getId(), sysConfiguration.getDefaultManagedSysId()), false);
        }

        final String tgId = userEntity.getId();
        final String strLogin = (login != null ? login.getLogin() : StringUtils.EMPTY);
        final String requestorId = pUser.getRequestorUserId();
        final String requestorPrincipal = pUser.getRequestorLogin();

        if (StringUtils.isNotEmpty(pUser.getFirstName()) && !pUser.getFirstName().equals(userEntity.getFirstName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [FirstName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("FirstName", "old=" + userEntity.getFirstName() + "; new=" + pUser.getFirstName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("FirstName old=" + userEntity.getFirstName() + "; new=" + pUser.getFirstName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getLastName()) && !pUser.getLastName().equals(userEntity.getLastName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [LastName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastName", "old=" + userEntity.getLastName() + "; new=" + pUser.getLastName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("LastName old=" + userEntity.getLastName() + "; new=" + pUser.getLastName());
            }
            
            
        }
        if (pUser.getBirthdate() != null && !pUser.getBirthdate().equals(userEntity.getBirthdate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Birthdate]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Birthdate", "old=" + userEntity.getBirthdate() + "; new=" + pUser.getBirthdate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Birthdate old=" + userEntity.getBirthdate() + "; new=" + pUser.getBirthdate());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getCostCenter()) && !pUser.getCostCenter().equals(userEntity.getCostCenter())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [CostCenter]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("CostCenter", "old=" + userEntity.getCostCenter() + "; new=" + pUser.getCostCenter());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("CostCenter old=" + userEntity.getCostCenter() + "; new=" + pUser.getCostCenter());
            }
            
            //
        }
        if (StringUtils.isNotEmpty(pUser.getDisplayName()) && !pUser.getDisplayName().equals(userEntity.getDisplayName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [DisplayName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("DisplayName", "old=" + userEntity.getDisplayName() + "; new=" + pUser.getDisplayName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("DisplayName old=" + userEntity.getDisplayName() + "; new=" + pUser.getDisplayName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getMaidenName()) && !pUser.getMaidenName().equals(userEntity.getMaidenName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [MaidenName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MaidenName", "old=" + userEntity.getMaidenName() + "; new=" + pUser.getMaidenName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("MaidenName old=" + userEntity.getMaidenName() + "; new=" + pUser.getMaidenName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getNickname()) && !pUser.getNickname().equals(userEntity.getNickname())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Nickname]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Nickname", "old=" + userEntity.getNickname() + "; new=" + pUser.getNickname());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Nickname old=" + userEntity.getNickname() + "; new=" + pUser.getNickname());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getMiddleInit()) && !pUser.getMiddleInit().equals(userEntity.getMiddleInit())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [MiddleInit]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MiddleInit", "old=" + userEntity.getMiddleInit() + "; new=" + pUser.getMiddleInit());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("MiddleInit old=" + userEntity.getMiddleInit() + "; new=" + pUser.getMiddleInit());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeId()) && !pUser.getEmployeeId().equals(userEntity.getEmployeeId())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [EmployeeId]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("EmployeeId", "old=" + userEntity.getEmployeeId() + "; new=" + pUser.getEmployeeId());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("EmployeeId old=" + userEntity.getEmployeeId() + "; new=" + pUser.getEmployeeId());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeTypeId()) && (userEntity.getEmployeeType() == null ||
                !pUser.getEmployeeTypeId().equals(userEntity.getEmployeeType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [EmployeeType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataService.getById(pUser.getEmployeeTypeId());
            auditLog.addCustomRecord("EmployeeType", "old=" + (userEntity.getEmployeeType() != null ? userEntity.getEmployeeType() : "N/A") + "; new=" + metadataType);
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("EmployeeType old=" + (userEntity.getEmployeeType() != null ? userEntity.getEmployeeType() : "N/A") + "; new=" + metadataType.getDescription());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getUserTypeInd()) && (userEntity.getType() == null || !pUser.getUserTypeInd().equals(userEntity.getType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [UserType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataService.getById(pUser.getUserTypeInd());
            auditLog.addCustomRecord("UserType", "old=" + (userEntity.getType() != null ? userEntity.getType() : "N/A") + "; new=" + metadataType);
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("UserType old=" + (userEntity.getUserTypeInd() != null ? userEntity.getUserTypeInd() : "N/A") + "; new=" + pUser.getUserTypeInd());
            }
            
            
        }

        if (StringUtils.isNotEmpty(pUser.getJobCodeId()) && (userEntity.getJobCode() == null ||
                !pUser.getJobCodeId().equals(userEntity.getJobCode().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [JobCode]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataService.getById(pUser.getJobCodeId());
            auditLog.addCustomRecord("JobCode", "old=" + (userEntity.getJobCode() != null ? userEntity.getJobCode() : "N/A") + "; new=" + metadataType.getDescription());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("JobCode old=" + (userEntity.getJobCode() != null ? userEntity.getJobCode() : "N/A") + "; new=" + metadataType.getDescription());
            }
            
            
        }
        if (pUser.getStartDate() != null && !pUser.getStartDate().equals(userEntity.getStartDate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [StartDate]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("StartDate", "old=" + userEntity.getStartDate() + "; new=" + pUser.getStartDate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("StartDate old=" + userEntity.getStartDate() + "; new=" + pUser.getStartDate());
            }
            
        }
        if (pUser.getLastDate() != null && !pUser.getLastDate().equals(userEntity.getLastDate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [LastDate]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastDate", "old=" + userEntity.getLastDate() + "; new=" + pUser.getLastDate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("LastDate old=" + userEntity.getLastDate() + "; new=" + pUser.getLastDate());
            }
            
            //
        }
        if (pUser.getStatus() != null && !pUser.getStatus().equals(userEntity.getStatus())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Status]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Status", "old=" + userEntity.getStatus() + "; new=" + pUser.getStatus());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Status old=" + userEntity.getStatus() + "; new=" + pUser.getStatus());
            }
            
            
        }
        if (pUser.getSecondaryStatus() != null && !pUser.getSecondaryStatus().equals(userEntity.getSecondaryStatus())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [SecondaryStatus]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("SecondaryStatus", "old=" + userEntity.getSecondaryStatus() + "; new=" + pUser.getSecondaryStatus());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("SecondaryStatus old=" + userEntity.getSecondaryStatus() + "; new=" + pUser.getSecondaryStatus());
            }
            
            
        }
        if (!StringUtils.equals(pUser.getSuffix(), userEntity.getSuffix())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Suffix]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Suffix", "old=" + userEntity.getSuffix() + "; new=" + pUser.getSuffix());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Suffix old=" + userEntity.getSuffix() + "; new=" + pUser.getSuffix());
            }
            
            
        }
        if (!StringUtils.equals(pUser.getTitle(), userEntity.getTitle())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Title]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Title", "old=" + userEntity.getTitle() + "; new=" + pUser.getTitle());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Title old=" + userEntity.getTitle() + "; new=" + pUser.getTitle());
            }
            
            
        }
        if (!StringUtils.equals(pUser.getClassification(), userEntity.getClassification())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Classification]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Classification", "old=" + userEntity.getClassification() + "; new=" + pUser.getClassification());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Classification old=" + userEntity.getClassification() + "; new=" + pUser.getClassification());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getUserSubTypeId()) && (userEntity.getSubType() == null ||
                !pUser.getUserSubTypeId().equals(userEntity.getSubType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [SubType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataService.getById(pUser.getUserSubTypeId());
            auditLog.addCustomRecord("SubType", "old=" + (userEntity.getSubType() != null ? userEntity.getSubType() : "N/A") + "; new=" + metadataType.getDescription());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("SubType old=" + (userEntity.getSubType() != null ? userEntity.getSubType() : "N/A") + "; new=" + metadataType.getDescription());
            }
            
            //
        }
        if (!StringUtils.equals(pUser.getPrefixLastName(), userEntity.getPrefixLastName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Prefix Last Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Prefix Last Name", "old=" + userEntity.getPrefixLastName() + "; new=" + pUser.getPrefixLastName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Prefix Last Name old=" + userEntity.getPrefixLastName() + "; new=" + pUser.getPrefixLastName());
            }
            
            
        }
        if (!StringUtils.equals(pUser.getPartnerName(), userEntity.getPartnerName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Partner Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Partner Name", "old=" + userEntity.getPartnerName() + "; new=" + pUser.getPartnerName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Partner Name old=" + userEntity.getPartnerName() + "; new=" + pUser.getPartnerName());
            }
            
            
        }
        if (!StringUtils.equals(pUser.getPrefixPartnerName(), userEntity.getPrefixPartnerName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Preffix Partner Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Preffix Partner Name", "old=" + userEntity.getPrefixPartnerName() + "; new=" + pUser.getPrefixPartnerName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Preffix Partner Name old=" + userEntity.getPrefixPartnerName() + "; new=" + pUser.getPrefixPartnerName());
            }
            
            
        }
        if (((pUser.getSecondaryStatus() == null && userEntity.getSecondaryStatus() == UserStatusEnum.DISABLED)
                || (pUser.getStatus() == UserStatusEnum.ACTIVE && userEntity.getStatus() == UserStatusEnum.INACTIVE)
                || (pUser.getStatus() == UserStatusEnum.ACTIVE && userEntity.getStatus() == UserStatusEnum.DELETED)) && saveRehireChange.equalsIgnoreCase("true")) {
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.setAction(AuditAction.USER_REHIRED.value());
            auditLog.setAuditDescription(pUser.getDisplayName() + " User rehired");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug(pUser.getDisplayName() + " User rehired");
            }
            
            
        }
        if (pUser.getStatus() == UserStatusEnum.INACTIVE && saveRehireChange.equalsIgnoreCase("true")) {
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin);
            auditLog.setAction(AuditAction.USER_LEAVER.value());
            auditLog.setAuditDescription(pUser.getDisplayName() + " User inactived");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug(pUser.getDisplayName() + " User inactived");
            }
            
            
        }
        if (pUser.getSecondaryStatus() == UserStatusEnum.DISABLED && saveRehireChange.equalsIgnoreCase("true")) {
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin);
            auditLog.setAction(AuditAction.USER_LEAVER.value());
            auditLog.setAuditDescription(pUser.getDisplayName() + " User disabled");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug(pUser.getDisplayName() + " User disabled");
            }
            
            
        }
        if (pUser.getStatus() == UserStatusEnum.DELETED && saveRehireChange.equalsIgnoreCase("true")) {
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin);
            auditLog.setAction(AuditAction.USER_LEAVER.value());
            auditLog.setAuditDescription(pUser.getDisplayName() + " User deleted");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug(pUser.getDisplayName() + " User deleted");
            }
            
            
        }

        userEntity.updateUser(userDozerConverter.convertToEntity(pUser.getUser(), false));
        userEntity.setSecondaryStatus(pUser.getSecondaryStatus());
        userEntity.setType(type);
        userEntity.setJobCode(jobCode);
        userEntity.setEmployeeType(employeeType);
        userEntity.setSubType(userSubType);
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.warn(mapper.writeValueAsString(parentLog));
        }catch (Exception e){
            log.error(e);
        }

    }

    public void updateUserAttributes(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateUserAttributes called -----");
        }

        if (pUser.getUserAttributes() != null && !pUser.getUserAttributes().isEmpty()) {
            for (Map.Entry<String, UserAttribute> entry : pUser.getUserAttributes().entrySet()) {
                if (StringUtils.isBlank(entry.getValue().getName())) {
                    throw new IllegalArgumentException("Name can not be empty");
                }
                AttributeOperationEnum operation = entry.getValue().getOperation();

                if (operation == AttributeOperationEnum.ADD && userEntity.getUserAttributes().containsKey(entry.getKey())) {
                    log.warn("Attribute with this name alreday exists");
                    entry.getValue().setOperation(AttributeOperationEnum.REPLACE);
                    operation = AttributeOperationEnum.REPLACE;
                }

                if (operation == AttributeOperationEnum.DELETE) {
                    userEntity.getUserAttributes().remove(entry.getKey());
                    // Audit Log -----------------------------------------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(pUser.getRequestorUserId()); //SIA 2015-08-01
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
                    auditLog.addCustomRecord(entry.getKey(), entry.getValue().getValue());
                    parentLog.addChild(auditLog);
                    // ---------------------------------------------------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Delete " + entry.getKey() + "-" + entry.getValue().getValue());
                    }
                } else if (operation == AttributeOperationEnum.ADD) {
                    UserAttributeEntity e = userAttributeDozerConverter.convertToEntity(entry.getValue(), true);
                    e.setUserId(userEntity.getId());
                    userEntity.getUserAttributes().put(entry.getKey(), e);
                    // Audit Log -----------------------------------------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(pUser.getRequestorUserId());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
                    auditLog.addCustomRecord(entry.getKey(), entry.getValue().getValue());
                    parentLog.addChild(auditLog);
                    // ---------------------------------------------------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add " + entry.getKey() + "-" + entry.getValue().getValue());
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    UserAttributeEntity entity = userEntity.getUserAttributes().get(entry.getKey());
                    if (entity != null) {
                        String oldValue = entity.getValue();
                        entity.copyValues(entry.getValue());
                        // Audit Log -----------------------------------------------------------------------------------
                        IdmAuditLog auditLog = new IdmAuditLog();
                        auditLog.setRequestorUserId(pUser.getRequestorUserId());
                        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                        auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                        auditLog.setAction(AuditAction.REPLACE_ATTRIBUTE.value());
                        auditLog.addCustomRecord(entry.getKey(), ("old=" + oldValue + "; new=" + userEntity.getUserAttributes().get(entry.getKey()).getValue()));
                        parentLog.addChild(auditLog);
                        // ---------------------------------------------------------------------------------------------
                        if (log.isDebugEnabled()) {
                            log.debug("Replace old=" + oldValue + "; new=" +
                                    userEntity.getUserAttributes().get(entry.getKey()).getValue());
                        }

                    }
                }
            }
        }
    }

    public void updateSupervisors(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateSupervisors called -----");
        }

        // Processing supervisors
        String userId = userEntity.getId();
        Set<User> superiors = pUser.getSuperiors();

        if (CollectionUtils.isNotEmpty(superiors)) {
            String requestorId = pUser.getRequestorUserId();
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

            for (User e : superiors) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    List<UserEntity> supervisorList = userMgr.getSuperiors(userId, 0, Integer.MAX_VALUE);
                    if (CollectionUtils.isNotEmpty(supervisorList)) {
                        for (UserEntity se : supervisorList) {
                            if (StringUtils.equals(se.getId(), e.getId())) {
                                userMgr.removeSupervisor(se.getId(), userId);
                                if (log.isDebugEnabled()) {
                                    log.debug(String.format("Removed a supervisor user %s from user %s", e.getId(), userId));
                                }

                                // Audit Log
                                //--------------------------------------------------
                                LoginEntity loginSupervisor = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), se.getPrincipalList());
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                //auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.setTargetUser(se.getId(), loginSupervisor != null ? loginSupervisor.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_SUPERVISOR.value());
                                auditLog.addCustomRecord("Delete Supervisor", loginSupervisor != null ? loginSupervisor.getLogin() : se.getId());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                            }
                        }
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    userMgr.addSuperior(e.getId(), userId);
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Adding a supervisor user %s for user %s", e.getId(), userId));
                    }

                    User se = userMgr.getUserDto(e.getId());
                    Login loginSupervisor = UserUtils.getUserManagedSysIdentity(sysConfiguration.getDefaultManagedSysId(), se.getPrincipalList());

                    // Audit Log
                    //--------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(userEntity.getId(), loginStr);
                    auditLog.setAction(AuditAction.ADD_SUPERVISOR.value());
                    auditLog.addCustomRecord("Add Supervisor", loginSupervisor != null ? loginSupervisor.getLogin() : se.getId());
                    parentLog.addChild(auditLog);
                    // -------------------------------------------------

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    throw new UnsupportedOperationException("Operation REPLACE is not supported for supervisors");
                }
            }
        }
    }

    public void updateGroups(final UserEntity userEntity, final ProvisionUser pUser,
                             final Set<Group> groupSet, final Set<Group> deleteGroupSet, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateGroups called -----");
        }

        if (CollectionUtils.isNotEmpty(pUser.getGroups())) {
            String requestorId = pUser.getRequestorUserId();
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

            for (Group g : pUser.getGroups()) {
                AttributeOperationEnum operation = g.getOperation();
                // Audit Log ---------------------------------------------------
                IdmAuditLog auditLog = new IdmAuditLog();
                auditLog.setRequestorUserId(requestorId);
                auditLog.setTargetUser(pUser.getId(), loginStr);
                auditLog.setTargetGroup(g.getId(), g.getName());
                auditLog.addCustomRecord("Group", g.getName());

                if (operation == AttributeOperationEnum.ADD) {
                    auditLog.setAction(AuditAction.ADD_USER_TO_GROUP.value());
                    GroupEntity groupEntity = groupManager.getGroup(g.getId());
                    if (groupEntity != null) {
                        if ((groupEntity.getMaxUserNumber() == null) || (userMgr.getNumOfUsersForGroup(groupEntity.getId(), pUser.getRequestorUserId()) < groupEntity.getMaxUserNumber())) {
                            userEntity.getGroups().add(groupEntity);
                            if (log.isDebugEnabled()) {
                                log.debug("Add group " + g.getName() + " to user");
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Add group " + g.getName() + " to user error: Groups limit of user count exceeded");
                            }
                            auditLog.fail();
                            auditLog.setFailureReason("Groups limit of user count exceeded");
                        }
                    }
                    parentLog.addChild(auditLog);

                } else if (operation == AttributeOperationEnum.DELETE) {
                    GroupEntity ge = groupManager.getGroup(g.getId());
                    userEntity.getGroups().remove(ge);
                    Group dg = groupDozerConverter.convertToDTO(ge, false);
                    dg.setOperation(operation);
                    deleteGroupSet.add(dg);

                    auditLog.setAction(AuditAction.REMOVE_USER_FROM_GROUP.value());
                    parentLog.addChild(auditLog);
                    if (log.isDebugEnabled()) {
                        log.debug("Delete group " + g.getName() + " from user");
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation REPLACE is not supported for groups");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userEntity.getGroups())) {
            for (GroupEntity gre : userEntity.getGroups()) {
                Group gr = groupDozerConverter.convertToDTO(gre, false);
                for (Group g : pUser.getGroups()) {
                    if (StringUtils.equals(g.getId(), gr.getId())) {
                        gr.setOperation(g.getOperation()); // get operation value from pUser
                        break;
                    }
                }
                groupSet.add(gr);
            }
        }
    }

    public void updateRoles(final UserEntity userEntity, final ProvisionUser pUser,
                            final Set<Role> roleSet, final Set<Role> deleteRoleSet, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateRoles called -----");
        }
        /*
         * Lev Bornovalov - for performance improvements, we will first fetch the objects via batch call
    	 */
        final Set<String> roleIdsToFetch = new HashSet<String>();
        for (final Role r : pUser.getRoles()) {
            if (StringUtils.isNotEmpty(r.getId())) {
                roleIdsToFetch.add(r.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(roleIdsToFetch)) {
            final RoleSearchBean sb = new RoleSearchBean();
            sb.setKeys(roleIdsToFetch);
            final List<RoleEntity> entityList = roleDataService.findBeans(sb, null, 0, Integer.MAX_VALUE);
            final Map<String, RoleEntity> roleEntityMap = new HashMap<String, RoleEntity>();
            if (CollectionUtils.isNotEmpty(entityList)) {
                for (final RoleEntity entity : entityList) {
                    roleEntityMap.put(entity.getId(), entity);
                }
            }

            final List<Role> dtoList = roleDozerConverter.convertToDTOList(entityList, false);
            final Map<String, Role> roleDtoMap = new HashMap<String, Role>();
            if (CollectionUtils.isNotEmpty(dtoList)) {
                for (final Role entity : dtoList) {
                    roleDtoMap.put(entity.getId(), entity);
                }
            }

            if (CollectionUtils.isNotEmpty(pUser.getRoles())) {

                final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

                for (final Role r : pUser.getRoles()) {

                    final AttributeOperationEnum operation = r.getOperation();
                    if (operation == AttributeOperationEnum.ADD) {
                        final RoleEntity roleEntity = roleEntityMap.get(r.getId());
                        if (userEntity.getRoles().contains(roleEntity)) {
                            log.warn("Role with this name already exists. Name=" + roleEntity.getName());
                            continue;
                        }
                        userEntity.getRoles().add(roleEntity);
                        // Audit Log ---------------------------------------------------
                        final IdmAuditLog auditLog = new IdmAuditLog();
                        auditLog.setRequestorUserId(pUser.getRequestorUserId()); //SIA 2015-08-01
                        auditLog.setAction(AuditAction.ADD_USER_TO_ROLE.value());
                        auditLog.setTargetUser(pUser.getId(), loginStr);
                        auditLog.setTargetRole(r.getId(), r.getName());
                        auditLog.addCustomRecord("ROLE", r.getName());
                        parentLog.addChild(auditLog);
                        //--------------------------------------------------------------
                        if (log.isDebugEnabled()) {
                            log.debug("Added role " + r.getName());
                        }
                    } else if (operation == AttributeOperationEnum.DELETE) {
                        final RoleEntity re = roleEntityMap.get(r.getId());
                        userEntity.getRoles().remove(re);
                        final Role dr = roleDtoMap.get(r.getId());
                        dr.setOperation(operation);
                        deleteRoleSet.add(dr);
                        // Audit Log ---------------------------------------------------
                        final IdmAuditLog auditLog = new IdmAuditLog();
                        auditLog.setRequestorUserId(pUser.getRequestorUserId()); //SIA 2015-08-01
                        auditLog.setAction(AuditAction.REMOVE_USER_FROM_ROLE.value());
                        auditLog.setTargetUser(pUser.getId(), loginStr);
                        auditLog.setTargetRole(r.getId(), r.getName());
                        auditLog.addCustomRecord("ROLE", r.getName());
                        parentLog.addChild(auditLog);
                        //-----------------------------------------------------------------
                        if (log.isDebugEnabled()) {
                            log.debug("Remove role " + r.getName());
                        }
                    } else if (operation == AttributeOperationEnum.REPLACE) {
                        throw new UnsupportedOperationException("Operation REPLACE is not supported for roles");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
                for (final RoleEntity ure : userEntity.getRoles()) {
                    final Role ar = roleDozerConverter.convertToDTO(ure, false);
                    for (final Role r : pUser.getRoles()) {
                        if (StringUtils.equals(r.getId(), ar.getId())) {
                            ar.setOperation(r.getOperation()); // get operation value from pUser
                            break;
                        }
                    }
                    roleSet.add(ar);
                }
            }
        }
    }

    /* User Org Affiliation */

    public void updateAffiliations(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateAffiliations called -----");
        }

        if (CollectionUtils.isNotEmpty(pUser.getOrganizationUserDTOs())) {
            String requestorId = pUser.getRequestorUserId();
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

            for (OrganizationUserDTO o : pUser.getOrganizationUserDTOs()) {
                AttributeOperationEnum operation = o.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                    if (userEntity.getOrganizationUser() == null)
                        userEntity.setOrganizationUser(new HashSet<OrganizationUserEntity>());
                    userEntity.getOrganizationUser().add(new OrganizationUserEntity(pUser.getId(), o.getOrganization().getId(), o.getMdTypeId()));
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_USER_TO_ORG.value());
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetOrg(o.getOrganization().getId(), o.getOrganization().getName());
                    auditLog.addCustomRecord(AuditTarget.ORG.value(), o.getOrganization().getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add organization " + o.getOrganization().getName() + " to user " + pUser.getId());
                    }
                } else if (operation == AttributeOperationEnum.DELETE) {
                    Set<OrganizationUserEntity> affiliations = userEntity.getOrganizationUser();
                    for (OrganizationUserEntity a : affiliations) {
                        if (a.getOrganization() != null && StringUtils.equals(o.getOrganization().getId(), a.getOrganization().getId())) {
                            userEntity.getOrganizationUser().remove(a);
                            // Audit Log ---------------------------------------------------
                            IdmAuditLog auditLog = new IdmAuditLog();
                            auditLog.setAction(AuditAction.REMOVE_USER_FROM_ORG.value());
                            auditLog.setRequestorUserId(requestorId);
                            auditLog.setTargetUser(pUser.getId(), loginStr);
                            auditLog.setTargetOrg(o.getOrganization().getId(), o.getOrganization().getName());
                            auditLog.addCustomRecord(AuditTarget.ORG.value(), o.getOrganization().getName());
                            parentLog.addChild(auditLog);
                            // -------------------------------------------------------------
                            if (log.isDebugEnabled()) {
                                log.debug("Remove organization " + a.getOrganization().getName() + " from user " + pUser.getId());
                            }
                            break;
                        }
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    Set<OrganizationUserEntity> affiliations = userEntity.getOrganizationUser();
                    for (OrganizationUserEntity a : affiliations) {
                        if (a.getOrganization() != null && StringUtils.equals(o.getOrganization().getId(), a.getOrganization().getId())) {
                            MetadataTypeEntity metadataTypeEntity = new MetadataTypeEntity();
                            metadataTypeEntity.setId(o.getMdTypeId());
                            a.setMetadataTypeEntity(metadataTypeEntity);
                            // Audit Log ---------------------------------------------------
                            IdmAuditLog auditLog = new IdmAuditLog();
                            auditLog.setRequestorUserId(requestorId);
                            auditLog.setAction(AuditAction.REPLACE_USER_FROM_ORG.value());
                            auditLog.setTargetUser(pUser.getId(), loginStr);
                            auditLog.setTargetOrg(o.getOrganization().getId(), o.getOrganization().getName());
                            auditLog.addCustomRecord(AuditTarget.ORG.value(), o.getOrganization().getName());
                            parentLog.addChild(auditLog);
                            // -------------------------------------------------------------
                            if (log.isDebugEnabled()) {
                                log.debug("Replace organization " + o.getOrganization().getName() + " to user " + pUser.getId());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void updateResources(final UserEntity userEntity, final ProvisionUser pUser, final Set<Resource> resourceSet, final Set<Resource> deleteResourceSet, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updateResources called -----");
        }

        Set<Resource> ar = resourceDozerConverter.convertToDTOSet(userEntity.getResources(), false);
        resourceSet.addAll(ar);

        if (CollectionUtils.isNotEmpty(pUser.getResources())) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

            for (Resource r : pUser.getResources()) {
                ResourceEntity resEntity = resourceService.findResourceByIdNoLocalized(r.getId());

                AttributeOperationEnum operation = r.getOperation();
                IdmAuditLog auditLog = new IdmAuditLog();
                auditLog.setRequestorUserId(pUser.getRequestorUserId()); //SIA 2015-08-01
                auditLog.setTargetUser(pUser.getId(), loginStr);
                auditLog.setTargetResource(resEntity.getId(), resEntity.getName());
                auditLog.addCustomRecord("Resource", resEntity.getName());

                if (operation == null) {
                    continue;
                } else if (operation == AttributeOperationEnum.ADD) {
                    userEntity.getResources().add(resEntity);
                    resourceSet.add(r);

                    // Audit Log ---------------------------------------------------
                    auditLog.setAction(AuditAction.ADD_USER_TO_RESOURCE.value());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Add resource " + resEntity.getName() + " to user");
                    }
                } else if (operation == AttributeOperationEnum.DELETE) {
                    userEntity.getResources().remove(resEntity);
                    resourceSet.remove(r);
                    deleteResourceSet.add(r);

                    // Audit Log ---------------------------------------------------
                    auditLog.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE.value());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                    if (log.isDebugEnabled()) {
                        log.debug("Delete resource " + resEntity.getName() + " from user");
                    }
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation REPLACE is not supported for resources");
                }
            }
        }
    }

    private Login getPrincipal(String logingId, List<Login> loginList) {
        for (Login lg : loginList) {
            if (lg.getLoginId().equals(logingId)) {
                return lg;
            }
        }
        return null;
    }

    public void updatePrincipals(UserEntity userEntity, ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- updatePrincipals called -----");
        }

        // Processing principals
        List<Login> principals = pUser.getPrincipalList();
        if (CollectionUtils.isNotEmpty(principals)) {
            String requestorId = pUser.getRequestorUserId();
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;

            for (final Iterator<Login> iter = principals.iterator(); iter.hasNext(); ) {
                final Login e = iter.next();
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    List<LoginEntity> entities = userEntity.getPrincipalList();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (final Iterator<LoginEntity> it = entities.iterator(); it.hasNext(); ) {
                            final LoginEntity en = it.next();
                            if (StringUtils.equals(en.getLoginId(), e.getLoginId())) {
                                it.remove();
                                // Audit Log ---------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setAction(AuditAction.DELETE_PRINCIPAL.value());
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), e.getLogin());
                                parentLog.addChild(auditLog);
                                // --------------------------------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Delete principal " + e.getLogin());
                                }
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    final LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                    try {
                        entity.setUserId(userEntity.getId());
                        userEntity.getPrincipalList().add(entity);
                        entity.setPassword(loginManager.encryptPassword(userEntity.getId(), e.getPassword()));
                    } catch (Exception ee) {
                        log.error(ee);
                    }
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_PRINCIPAL.value());
                    auditLog.setRequestorUserId(requestorId);
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), e.getLogin());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------

                    if (log.isDebugEnabled()) {
                        log.debug("Add principal " + e.getLogin());
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {

                    if (CollectionUtils.isNotEmpty(userEntity.getPrincipalList())) {
                        for (final LoginEntity en : userEntity.getPrincipalList()) {
                            if (StringUtils.equals(en.getLoginId(), e.getLoginId())) {

                                if (!en.getLogin().equals(e.getLogin())) {
                                    e.setOrigPrincipalName(en.getLogin());
                                    if (log.isDebugEnabled()) {
                                        log.debug("--------------- en.getManagedSysId() : " + en.getManagedSysId() + " ---------------");
                                        log.debug("--------------- savePrincipalChange : " + savePrincipalChange + " ---------------");
                                    }
                                    if (savePrincipalChange.equalsIgnoreCase("true")) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("-------------- changing AD User principal ----------");
                                        }
                                        IdmAuditLog auditLog = new IdmAuditLog();
                                        auditLog.setRequestorUserId(requestorId);
                                        auditLog.setUserId(parentLog.getUserId());
                                        auditLog.setPrincipal(parentLog.getPrincipal());
                                        auditLog.setManagedSysId(en.getManagedSysId());
                                        auditLog.setTargetUser(userEntity.getId(), en.getLogin() != null ? en.getLogin() : StringUtils.EMPTY);
                                        auditLog.setAction(AuditAction.USER_PRINCIPAL_CHANGED.value());
                                        auditLog.setAuditDescription("User Principal changed for " + pUser.getDisplayName());
                                        parentLog.addChild(auditLog);
                                    }
                                }
                                String logOld = en.toString();
                                en.copyProperties(e);

                                // Audit Log ---------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setAction(AuditAction.REPLACE_PRINCIPAL.value());
                                auditLog.setRequestorUserId(requestorId);
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), "old=" + logOld + "; new=" + e.toString());
                                parentLog.addChild(auditLog);
                                // --------------------------------------------------------------
                                if (log.isDebugEnabled()) {
                                    log.debug("Replace principal old=" + logOld + "; new=" + e.toString());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    protected ObjectResponse requestAddModify(ExtensibleUser extUser, Login mLg, boolean isAdd,
                                              String requestId, final IdmAuditLog idmAuditLog) {

        ObjectResponse response = new ObjectResponse();

        String managedSysId = mLg.getManagedSysId();
        if (managedSysId == null) {
            log.error("managedSysId is not set for Login");
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return response;
        }
        final ManagedSysEntity mSys = managedSystemService.getManagedSysById(managedSysId);
        final ManagedSysDto mSysDto = managedSysDozerConverter.convertToDTO(mSys, false);

        // SIA: Changed to debug
        log.debug("********************MANAGED SYSTEM DTO ****************");
        log.debug(" NAME       =" + mSys.getName());
        log.debug(" HOST URL   =" + mSys.getHostUrl());
        log.debug(" PORT       =" + mSys.getPort());
        log.debug(" BASE DN    =" + mSys.getSearchScope());
        log.debug(" USER ID    =" + mSys.getUserId());
        log.debug(" ADD HANDLER=" + mSys.getAddHandler());
        log.debug("*******************************************************");

        List<AttributeMap> attrMap = managedSysDataService.getAttributeMapsByManagedSysId(managedSysId);
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())) {
                extUser.setPrincipalFieldName(attr.getAttributeName());
                extUser.setPrincipalFieldDataType(attr.getDataType().getValue());
                break;
            }
        }

        CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
        userReq.setObjectIdentity(mLg.getLogin());
        userReq.setRequestID(requestId);
        userReq.setTargetID(managedSysId);
        userReq.setHostLoginId(mSys.getUserId());
        String passwordDecoded = managedSystemService.getDecryptedPassword(mSysDto);

        userReq.setHostLoginPassword(passwordDecoded);
        userReq.setHostUrl(mSys.getHostUrl());
        if (mSys.getPort() != null) {
            userReq.setHostPort(mSys.getPort().toString());
        }

        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> objList = managedSystemService.managedSysObjectParam(managedSysId,
                ManagedSystemObjectMatch.USER);
        if (CollectionUtils.isNotEmpty(objList)) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(objList.get(0), false);
        }

        if (matchObj != null) {
            userReq.setBaseDN(matchObj.getBaseDn());
        }
        userReq.setOperation(isAdd ? "ADD" : "MODIFY");
        userReq.setExtensibleObject(extUser);
        userReq.setScriptHandler(mSys.getAddHandler());

        response = isAdd ? connectorAdapter.addRequest(mSysDto, userReq)
                : connectorAdapter.modifyRequest(mSysDto, userReq);
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, (isAdd ? "ADD IDENTITY = "
                : "MODIFY IDENTITY = ") + response.getStatus() + " details:" + response.getErrorMsgAsStr());

        IdmAuditLog idmAuditLogChild1 = new IdmAuditLog();
        idmAuditLogChild1.setAction(isAdd ? AuditAction.ADD_USER_TO_RESOURCE.value() : AuditAction.UPDATE_USER_TO_RESOURCE.value());
//        LoginEntity lRequestor = loginManager.getPrimaryIdentity(systemUserId);
        idmAuditLogChild1.setRequestorUserId(systemUserId);
        //// TODO: 4/15/16 we can put it to properies too, but not needed to call DB each time
        idmAuditLogChild1.setRequestorPrincipal("system");
        idmAuditLogChild1.setTargetUser(mLg.getUserId(), mLg.getLogin());
        idmAuditLogChild1.setTargetResource(mSys.getResourceId(), mSys.getName());
        idmAuditLogChild1.setManagedSysId(mSys.getId());
        boolean successResult = response.getStatus() != StatusCodeType.FAILURE;
        if (successResult) {
            idmAuditLogChild1.succeed();
            idmAuditLogChild1.setSuccessReason(StatusCodeType.SUCCESS.value());
        } else {
            idmAuditLogChild1.fail();
            idmAuditLogChild1.setFailureReason(response.getErrorMsgAsStr());
            idmAuditLogChild1.setAuditDescription(response.getErrorMsgAsStr());
            idmAuditLog.setAuditDescription(response.getErrorMsgAsStr());
        }
        idmAuditLog.addChild(idmAuditLogChild1);

        return response;
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

        ExtensibleUser extensibleUser = buildPolicyMapHelper.buildMngSysAttributes(mLg, ProvOperationEnum.DELETE.name());
        request.setExtensibleObject(extensibleUser);
        String passwordDecoded = managedSystemService.getDecryptedPassword(mSys);

        request.setHostLoginPassword(passwordDecoded);
        request.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            request.setBaseDN(matchObj.getBaseDn());
        }
        request.setOperation("DELETE");

        request.setScriptHandler(mSys.getDeleteHandler());

        ObjectResponse resp = connectorAdapter.deleteRequest(mSys, request);

        return resp;
    }

    protected ResponseType resetPassword(String requestId, Login login,
                                         String password, ManagedSysDto mSys,
                                         ManagedSystemObjectMatch matchObj, ExtensibleUser extensibleUser, String operation, boolean forceChangePassword) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        req.setExtensibleObject(extensibleUser);
        req.setForceChange(forceChangePassword);
        String passwordDecoded = managedSystemService.getDecryptedPassword(mSys);

        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            req.setBaseDN(matchObj.getBaseDn());
        }
//        "RESET_PASSWORD"
        req.setOperation(operation);
        req.setPassword(password);

        req.setScriptHandler(mSys.getPasswordHandler());

        if (log.isDebugEnabled()) {
            log.debug("Reset password request will be sent for user login " + login.getLogin());
        }
        return connectorAdapter.resetPasswordRequest(mSys, req);

    }

//    protected ResponseType setPassword(String requestId, Login login, String prevDecPassword,
//                                       String newDecPasswordSync,
//                                       ManagedSysDto mSys,
//                                       ManagedSystemObjectMatch matchObj,
//                                       ExtensibleUser extensibleUser) {
//
//        PasswordRequest req = new PasswordRequest();
//        req.setObjectIdentity(login.getLogin());
//        req.setRequestID(requestId);
//        req.setTargetID(login.getManagedSysId());
//        req.setHostLoginId(mSys.getUserId());
//        req.setExtensibleObject(extensibleUser);
//        String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys);
//
//        req.setHostLoginPassword(passwordDecoded);
//        req.setHostUrl(mSys.getHostUrl());
//        req.setBaseDN((matchObj != null) ? matchObj.getBaseDn() : null);
//        req.setOperation("SET_PASSWORD");
//        req.setPassword(newDecPasswordSync);
//        req.setScriptHandler(mSys.getPasswordHandler());
//        req.setCurrentPassword(prevDecPassword);
//        ResponseType respType = connectorAdapter.setPasswordRequest(mSys, req, MuleContextProvider.getCtx());
//
//        return respType;
//
//    }

    protected ProvisionUserResponse validatePassword(Login primaryLogin, ProvisionUser user, String requestId) {

        ProvisionUserResponse resp = new ProvisionUserResponse();

        Password password = new Password();
        password.setManagedSysId(primaryLogin.getManagedSysId());
        password.setPassword(primaryLogin.getPassword());
        password.setPrincipal(primaryLogin.getLogin());

        Policy passwordPolicy = user.getPasswordPolicy();
        if (passwordPolicy == null) {
            PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
            searchBean.setManagedSystemId(primaryLogin.getManagedSysId());
            searchBean.setUserId(user.getId());
            passwordPolicy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
        }

        try {
            PasswordValidationResponse valCode = passwordManager.isPasswordValidForUserAndPolicy(
                    password, userDozerConverter.convertToEntity(user.getUser(), false),
                    loginDozerConverter.convertToEntity(primaryLogin, false), passwordPolicy);
            if (valCode == null || !valCode.isSuccess()) {
                resp.setStatus(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
                return resp;
            }
        } catch (ObjectNotFoundException e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_NEQ_PASSWORD);
            return resp;
        }

        resp.setStatus(ResponseStatus.SUCCESS);
        return resp;
    }

    protected ResponseType suspend(String requestId, Login login, ManagedSysDto mSys, ExtensibleUser extensibleUser, boolean operation) {
        SuspendResumeRequest resumeReq = new SuspendResumeRequest();
        resumeReq.setObjectIdentity(login.getLogin());
        resumeReq.setTargetID(login.getManagedSysId());
        resumeReq.setRequestID(requestId);
        resumeReq.setScriptHandler(mSys.getSuspendHandler());
        resumeReq.setHostLoginId(mSys.getUserId());
        resumeReq.setExtensibleObject(extensibleUser);

        String passwordDecoded = managedSystemService.getDecryptedPassword(mSys);

        resumeReq.setHostLoginPassword(passwordDecoded);
        resumeReq.setHostUrl(mSys.getHostUrl());
        if (log.isDebugEnabled()) {
            log.debug((operation ? "Suspend" : "Resume") + " request will be sent for user login " + login.getLogin());
        }
        return operation ? connectorAdapter.suspendRequest(mSys, resumeReq) :
                connectorAdapter.resumeRequest(mSys, resumeReq);
    }

    protected Response addEvent(ProvisionActionEvent event, ProvisionActionTypeEnum type) {
        Map<String, Object> bindingMap = new HashMap<>();
        Response response = new Response(ResponseStatus.SUCCESS);
        response.setResponseValue(ProvisionServiceEventProcessor.CONTINUE);
        ProvisionServiceEventProcessor eventProcessorScript = getEventProcessor(bindingMap, eventProcessor);
        if (eventProcessorScript != null) {
            response = eventProcessorScript.process(event, type);
        }
        return response;
    }

    private ProvisionServiceEventProcessor getEventProcessor(Map<String, Object> bindingMap, String scriptName) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(scriptName)) {
            try {
                return (ProvisionServiceEventProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
            } catch (Exception ce) {
                log.error(ce);
            }
        }
        return null;
    }

    //SIA: Method added
    public void addUserProperties(final UserEntity userEntity, final ProvisionUser pUser) {
        if (log.isDebugEnabled()) {
            log.debug("----- addUserProperties called -----");
        }

        MetadataTypeEntity type = null;
        MetadataTypeEntity jobCode = null;
        MetadataTypeEntity employeeType = null;
        MetadataTypeEntity subtype = null;

        MetadataTypeSearchBean sb = new MetadataTypeSearchBean();
        if (StringUtils.isNotBlank(pUser.getMdTypeId())) {
            sb.addKey(pUser.getMdTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getJobCodeId())) {
            sb.addKey(pUser.getJobCodeId());
        }
        if (StringUtils.isNotBlank(pUser.getEmployeeTypeId())) {
            sb.addKey(pUser.getEmployeeTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getUserSubTypeId())) {
            sb.addKey(pUser.getUserSubTypeId());
        }

        List<MetadataTypeEntity> metaDataTypes = metadataService.findEntityBeans(sb, -1, -1, null);
        if (CollectionUtils.isNotEmpty(metaDataTypes)) {
            for (MetadataTypeEntity typeEntity : metaDataTypes) {
                if (typeEntity.getId().equals(pUser.getMdTypeId())) {
                    type = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getJobCodeId())) {
                    jobCode = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getEmployeeTypeId())) {
                    employeeType = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getUserSubTypeId())) {
                    subtype = typeEntity;
                }
            }
        }

        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
        if (login == null && StringUtils.isNotEmpty(pUser.getId())) {
            login = loginDozerConverter.convertToDTO(loginManager.getByUserIdManagedSys(pUser.getId(), sysConfiguration.getDefaultManagedSysId()), false);
        }

        userEntity.updateUser(userDozerConverter.convertToEntity(pUser.getUser(), false));
        userEntity.setType(type);
        userEntity.setJobCode(jobCode);
        userEntity.setEmployeeType(employeeType);
        userEntity.setSubType(subtype);
    }

    //SIA: Method reviewed
    public void auditUserProperties(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (log.isDebugEnabled()) {
            log.debug("----- auditUserProperties called -----");
        }

        // SIA: Not used here
        /*
        MetadataTypeEntity type = null;
        MetadataTypeEntity jobCode = null;
        MetadataTypeEntity employeeType = null;
        MetadataTypeEntity subtype = null;

        MetadataTypeSearchBean sb = new MetadataTypeSearchBean();
        if (StringUtils.isNotBlank(pUser.getMdTypeId())) {
            sb.addKey(pUser.getMdTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getJobCodeId())) {
            sb.addKey(pUser.getJobCodeId());
        }
        if (StringUtils.isNotBlank(pUser.getEmployeeTypeId())) {
            sb.addKey(pUser.getEmployeeTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getUserSubTypeId())) {
            sb.addKey(pUser.getUserSubTypeId());
        }

        List<MetadataTypeEntity> metaDataTypes = metadataService.findEntityBeans(sb, -1, -1, null);
        if (CollectionUtils.isNotEmpty(metaDataTypes)) {
            for (MetadataTypeEntity typeEntity : metaDataTypes) {
                if (typeEntity.getId().equals(pUser.getMdTypeId())) {
                    type = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getJobCodeId())) {
                    jobCode = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getEmployeeTypeId())) {
                    employeeType = typeEntity;
                } else if (typeEntity.getId().equals(pUser.getUserSubTypeId())) {
                    subtype = typeEntity;
                }
            }
        }
        */

        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
        if (login == null && StringUtils.isNotEmpty(pUser.getId())) {
            login = loginDozerConverter.convertToDTO(loginManager.getByUserIdManagedSys(pUser.getId(), sysConfiguration.getDefaultManagedSysId()), false);
        }

        final String tgId = userEntity.getId();
        final String strLogin = (login != null ? login.getLogin() : StringUtils.EMPTY);
        final String requestorId = pUser.getRequestorUserId();
        final String requestorPrincipal = pUser.getRequestorLogin();

        if (StringUtils.isNotEmpty(pUser.getFirstName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [FirstName]");    // More easy to identify into de audit
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("FirstName", "New=" + pUser.getFirstName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("FirstName New=" + pUser.getFirstName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getLastName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [LastName]");    // More easy to identify into de audit
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastName", "New=" + pUser.getLastName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("LastName New=" + pUser.getLastName());
            }
            
            
        }
        if (pUser.getBirthdate() != null) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Birthdate]");    // More easy to identify into de audit
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Birthdate", "New=" + pUser.getBirthdate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Birthdate New=" + pUser.getBirthdate());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getCostCenter())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [CostCenter]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("CostCenter", "New=" + pUser.getCostCenter());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("CostCenter New=" + pUser.getCostCenter());
            }
            
        }
        if (StringUtils.isNotEmpty(pUser.getDisplayName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [DisplayName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("DisplayName", "New=" + pUser.getDisplayName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("DisplayName New=" + pUser.getDisplayName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getMaidenName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [MaidenName]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MaidenName", "New=" + pUser.getMaidenName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("MaidenName New=" + pUser.getMaidenName());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getNickname())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Nickname]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Nickname", "New=" + pUser.getNickname());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Nickname New=" + pUser.getNickname());
            }
            
            
        }

        if (StringUtils.isNotEmpty(pUser.getMiddleInit())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [MiddleInit]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MiddleInit", "New=" + pUser.getMiddleInit());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("MiddleInit New=" + pUser.getMiddleInit());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeId())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [EmployeeId]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("EmployeeId", "New=" + pUser.getEmployeeId());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("EmployeeId New=" + pUser.getEmployeeId());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeTypeId())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [EmployeeType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataTypeDAO.findById(pUser.getEmployeeTypeId());
            auditLog.addCustomRecord("EmployeeType", "New=" + metadataType.getDescription());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("EmployeeType New=" + metadataType.getDescription());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getUserTypeInd())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [UserType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("UserType", "New=" + pUser.getUserTypeInd());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("UserType New=" + pUser.getUserTypeInd());
            }
            
            
        }
        if (userEntity.getJobCode() != null && StringUtils.isNotEmpty(pUser.getJobCodeId())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [JobCode]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataTypeDAO.findById(pUser.getJobCodeId());
            auditLog.addCustomRecord("JobCode", "New=" + metadataType.getDescription());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("JobCode New=" + metadataType.getDescription());
            }
            
            
        }
        if (pUser.getStartDate() != null) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [StartDate]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("StartDate", "New=" + pUser.getStartDate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("StartDate New=" + pUser.getStartDate());
            }
            
        }
        if (pUser.getLastDate() != null) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [LastDate]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastDate", "New=" + pUser.getLastDate());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("LastDate New=" + pUser.getLastDate());
            }
            
            
        }
        if (pUser.getStatus() != null) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Status]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Status", "New=" + pUser.getStatus());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Status New=" + pUser.getStatus());
            }
            
            
        }
        if (pUser.getSecondaryStatus() != null) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [SecondaryStatus]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("SecondaryStatus", "New=" + pUser.getSecondaryStatus());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("SecondaryStatus New=" + pUser.getSecondaryStatus());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getSuffix())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Suffix]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Suffix", "New=" + pUser.getSuffix());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Suffix New=" + pUser.getSuffix());
            }
            
            
        }
        if (StringUtils.isNotEmpty(pUser.getTitle())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Title]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Title", "New=" + pUser.getTitle());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Title New=" + pUser.getTitle());
            }
            
            
        }
        // SIA: Added
        if (StringUtils.isNotEmpty(pUser.getClassification())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Classification]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Classification", "New=" + pUser.getClassification());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Classification New=" + pUser.getClassification());
            }
            
            
        }
        // SIA Added to complete user fields
        if (StringUtils.isNotEmpty(pUser.getUserSubTypeId()) && (userEntity.getSubType() == null ||
                !pUser.getUserSubTypeId().equals(userEntity.getSubType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [SubType]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataService.getById(pUser.getUserSubTypeId());
            auditLog.addCustomRecord("SubType", "New=" + metadataType.getDescription());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("SubType New=" + metadataType.getDescription());
            }
            
        }

        if (!StringUtils.equals(pUser.getPrefixLastName(), userEntity.getPrefixLastName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Prefix Last Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Prefix Last Name", "New=" + pUser.getPrefixLastName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Prefix Last Name New=" + pUser.getPrefixLastName());
            }
            
        }

        if (!StringUtils.equals(pUser.getPartnerName(), userEntity.getPartnerName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Partner Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Partner Name", "New=" + pUser.getPartnerName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Partner Name New=" + pUser.getPartnerName());
            }
            
        }

        if (!StringUtils.equals(pUser.getPrefixPartnerName(), userEntity.getPrefixPartnerName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(requestorId);
            auditLog.setRequestorPrincipal(requestorPrincipal);
            auditLog.setTargetUser(tgId, strLogin + " [Preffix Partner Name]");
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Preffix Partner Name", "New=" + pUser.getPrefixPartnerName());
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
            if (log.isDebugEnabled()) {
                log.debug("Preffix Partner Name New=" + pUser.getPrefixPartnerName());
            }
            
        }
    }

}