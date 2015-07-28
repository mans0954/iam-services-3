package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
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
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
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
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserToGroupMembershipXref;
import org.openiam.idm.srvc.user.dto.UserToOrganizationMembershipXref;
import org.openiam.idm.srvc.user.dto.UserToResourceMembershipXref;
import org.openiam.idm.srvc.user.dto.UserToRoleMembershipXref;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.*;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Base class for the provisioning service
 * User: suneetshah
 */
public abstract class AbstractProvisioningService extends AbstractBaseService implements ProvisionService {

    protected static final Log log = LogFactory.getLog(AbstractProvisioningService.class);

    public static final String NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION = "NEW_USER_EMAIL_SUPERVISOR";
    public static final String NEW_USER_EMAIL_NOTIFICATION = "NEW_USER_EMAIL";
    public static final String NEW_USER_ACTIVATION_NOTIFICATION="NEW_USER_ACTIVATION_NOTIFICATION";
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
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ManagedSystemWebService managedSysService;

    @Autowired
    protected ManagedSystemService managedSysDataService;
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

    @Autowired
    protected BuildUserPolicyMapHelper buildPolicyMapHelper;
    @Autowired
    ProvisionServiceUtil provisionServiceUtil;
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

    @Value("${org.openiam.idm.eventProcessor.groovy.script}")
    private String eventProcessor;
    
    @Value("${org.openiam.idm.preProcessor.groovy.script}")
    protected String preProcessor;
    
    @Value("${org.openiam.idm.postProcessor.groovy.script}")
    protected String postProcessor;
    
    @Value("${org.openiam.idm.resourceOrderProcessor.groovy.script}")
    protected String resourceOrderProcessor;
    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;
    @Autowired
    protected ProvisionQueueService provQueueService;

    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected MetadataTypeDAO metadataTypeDAO;
    
    @Autowired
    private AccessRightDAO accessRightDAO;

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
            pUser.setRequestClientIP("NA");
        }
        if (pUser.getRequestorLogin() == null || pUser.getRequestorLogin().isEmpty()) {
            pUser.setRequestorLogin("NA");
        }
        //if ( pUser.getRequestorDomain() == null || pUser.getRequestorDomain().isEmpty() ) {
        //    pUser.setRequestorDomain("NA");
        //}
        if (pUser.getCreatedBy() == null || pUser.getCreatedBy().isEmpty()) {
            pUser.setCreatedBy("NA");
        }
    }

    protected void sendResetPasswordToUser(LoginEntity identity, String password) {
        UserEntity user = userMgr.getUser(identity.getUserId());
        List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
        msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), identity.getUserId()));
        msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
        msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity.getLogin()));
        msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
        msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getId());
        notificationRequest.setParamList(msgParams);
        notificationRequest.setNotificationType(PASSWORD_EMAIL_NOTIFICATION);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                mailService.sendNotification(notificationRequest);
            }
        });

    }
    protected void sendActivationLink(User user, Login login){
            final PasswordResetTokenRequest tokenRequest = new PasswordResetTokenRequest(login.getLogin(), login.getManagedSysId(), null);
            final PasswordResetTokenResponse tokenResponse = passwordManager.generatePasswordResetToken(tokenRequest);

            if (tokenResponse != null && tokenResponse.isSuccess() && tokenResponse.getPasswordResetToken() != null) {
                String token = tokenResponse.getPasswordResetToken();

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
                notificationRequest.setNotificationType(NEW_USER_ACTIVATION_NOTIFICATION);
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    public void run() {
                        mailService.sendNotification(notificationRequest);
                    }
                });
            }
    }

    protected void sendCredentialsToUser(User user, String identity, String password) {

        List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
        msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
        msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
        msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
        msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
        msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getId());
        notificationRequest.setParamList(msgParams);
        notificationRequest.setNotificationType(NEW_USER_EMAIL_NOTIFICATION);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                mailService.sendNotification(notificationRequest);
            }
        });

    }

    protected void sendCredentialsToSupervisor(User user, String identity, String password, String name) {

        List<NotificationParam> msgParams = new LinkedList<NotificationParam>();
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_HOST.value(), serviceHost));
        msgParams.add(new NotificationParam(MailTemplateParameters.SERVICE_CONTEXT.value(), serviceContext));
        msgParams.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
        msgParams.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
        msgParams.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
        msgParams.add(new NotificationParam(MailTemplateParameters.USER_NAME.value(), name));
        msgParams.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
        msgParams.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getId());
        notificationRequest.setNotificationType(NEW_USER_EMAIL_SUPERVISOR_NOTIFICATION);
        notificationRequest.setParamList(msgParams);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                mailService.sendNotification(notificationRequest);
            }
        });
    }

    protected Login buildPrimaryPrincipal(Map<String, Object> bindingMap, ScriptIntegration se) {
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = (defaultManagedSys.getResource() != null) ? managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResource().getId()) : null;
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);

        log.debug("Building primary identity. ");

        if (policyAttrMap != null) {

            log.debug("- policyAttrMap IS NOT null");

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
                            if (attr.getName().equalsIgnoreCase("PRINCIPAL")) {
                                primaryIdentityRule = attr;
                            } else if (attr.getName().equalsIgnoreCase("PASSWORD")) {
                                primaryPasswordRule = attr;
                            }
                        }
                    }
                }
                // We must be sure that Identity exists in BindingMap before
                // all other scripts like as Password will be processed.
                // Primary identity must be generated first and must be put into BindingMap first.
                if (primaryIdentityRule != null && primaryPasswordRule != null) {
                    String identityOutput = (String) provisionServiceUtil.getOutputFromAttrMap(
                            primaryIdentityRule, bindingMap, se);
                    primaryIdentity.setLogin(identityOutput);
                    bindingMap.put(TARGET_SYSTEM_IDENTITY, identityOutput);

                    String passwordOutput = (String) provisionServiceUtil.getOutputFromAttrMap(
                            primaryPasswordRule, bindingMap, se);
                    primaryIdentity.setPassword(passwordOutput);

                }
            } catch (Exception e) {
                log.error(e);
            }

            return primaryIdentity;

        } else {
            log.debug("- policyAttrMap IS null");
            return null;
        }
    }

    protected String parseUserPrincipal(List<ExtensibleAttribute> extensibleAttributes) {
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = (defaultManagedSys.getResource() != null) ? managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResource().getId()) : Collections.EMPTY_LIST;
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        String principalAttributeName = null;
        for (AttributeMap attr : policyAttrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                    if (attr.getName().equalsIgnoreCase("PRINCIPAL")) {
                        principalAttributeName = attr.getName();
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
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = (defaultManagedSys != null) ? managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResource().getId()) : Collections.EMPTY_LIST;
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        if (policyAttrMap != null) {
            log.debug("- policyAttrMap IS NOT null");
            try {
                for (AttributeMap attr : policyAttrMap) {
                    String output = (String) provisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, se);
                    String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                            if ("PASSWORD".equalsIgnoreCase(attr.getName())) {
                                primaryIdentity.setPassword(output);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("- policyAttrMap IS null");
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
        if ( pUser != null) {
            log.info("======= callPreProcessor: isSkipPreprocessor="+pUser.isSkipPreprocessor()+", ");
            if (!pUser.isSkipPreprocessor() &&
                    (addPreProcessScript = createProvPreProcessScript(preProcessor, bindingMap)) != null) {
                addPreProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                return executeProvisionPreProcess(addPreProcessScript, bindingMap, pUser, null, operation);

            }
            log.info("======= callPreProcessor: addPreProcessScript="+addPreProcessScript+", ");
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }


    protected int callPostProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap, PasswordSync passwordSync) {

        ProvisionServicePostProcessor<ProvisionUser> addPostProcessScript = null;

        if ( pUser != null) {
            if (!pUser.isSkipPostProcessor() &&
                    (addPostProcessScript = createProvPostProcessScript(postProcessor, bindingMap)) != null) {
                //        //TODO IMPLEMENT
                addPostProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                return executeProvisionPostProcess(addPostProcessScript, bindingMap, pUser, null, operation);

            }
        }
        // pre-processor was skipped
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
        log.info("======= call PreProcessor: ppScript=" + ppScript + ", operation=" + operation);
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
        log.info("======= call PostProcessor: ppScript=" + ppScript + ", operation=" + operation);
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
        // Processing emails
        Set<EmailAddress> emailAddresses = pUser.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            this.manageDefaultEmails(emailAddresses);
            for (EmailAddress e : emailAddresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (EmailAddressEntity en : entities) {
                            if (en.getId().equals(e.getId())) {
                                userEntity.getEmailAddresses().remove(en);
                                // Audit Log
                                //--------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_EMAIL.value());
                                auditLog.setAuditDescription("DELETE Email: " + en.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())) {
                        entity.setType(null);
                    }
                    userEntity.getEmailAddresses().add(entity);
                    // Audit Log
                    //--------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_EMAIL.value());
                    auditLog.setAuditDescription("ADD Email: " + e.toString());
                    parentLog.addChild(auditLog);
                    // -------------------------------------------------

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<EmailAddressEntity> entities = userEntity.getEmailAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (EmailAddressEntity en : entities) {
                            if (StringUtils.equals(en.getId(), e.getId())) {
                                userEntity.getEmailAddresses().remove(en);
                                userMgr.evict(en);
                                EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())) {
                                    entity.setType(null);
                                }
                                userEntity.getEmailAddresses().add(entity);
                                // Audit Log
                                //--------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.REPLACE_EMAIL.value());
                                auditLog.setAuditDescription("REPLACE Email: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updatePhones(UserEntity userEntity, ProvisionUser pUser, IdmAuditLog parentLog) {
        // Processing phones
        Set<Phone> phones = pUser.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            this.manageDefaultPhone(phones);
            for (Phone e : phones) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (PhoneEntity en : entities) {
                            if (en.getId().equals(e.getId())) {
                                userEntity.getPhones().remove(en);
                                //Audit log
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_PHONE.value());
                                auditLog.setAuditDescription("DELETE Phone: "+e.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())) {
                        entity.setType(null);
                    }
                    userEntity.getPhones().add(entity);
                    // Audit log
                    IdmAuditLog auditLog = new IdmAuditLog();
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_PHONE.value());
                    auditLog.setAuditDescription("ADD Phone: "+e.toString());
                    parentLog.addChild(auditLog);
                    // ----------------------------------------
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities))  {
                        for (PhoneEntity en : entities) {
                            if (en.getId().equals(e.getId())) {
                            	/* 
                                 * IDMAPPS-2700
                        		 * TOPT_SECRET disappears from phone when updating from UI
                                 */
                            	e.setTotpSecret(en.getTotpSecret());
                            	
                                // Audit Log
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.REPLACE_PHONE.value());
                                auditLog.setAuditDescription("REPLACE Phone: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                //-------------------------------------
                                userEntity.getPhones().remove(en);
                                userMgr.evict(en);
                                PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())) {
                                    entity.setType(null);
                                }
                                userEntity.getPhones().add(entity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateAddresses(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        // Processing addresses
        Set<Address> addresses = pUser.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            this.manageDefaultAddresses(addresses);
            for (Address e : addresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (AddressEntity en : entities) {
                            if (en.getId().equals(e.getId())) {
                                userEntity.getAddresses().remove(en);
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_ADDRESS.value());
                                auditLog.setAuditDescription("DELETE Address: " + en.toString());

                                parentLog.addChild(auditLog);
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    if(org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())){
                        entity.setType(null);
                    }
                    userEntity.getAddresses().add(entity);
                    IdmAuditLog auditLog = new IdmAuditLog();
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_ADDRESS.value());
                    auditLog.setAuditDescription("ADD Address: " + entity.toString());
                    parentLog.addChild(auditLog);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<AddressEntity> entities = userEntity.getAddresses();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (AddressEntity en : entities) {
                            if (en.getId().equals(e.getId())) {
                                // Audit Log -----------------------------------------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.REPLACE_ADDRESS.value());
                                auditLog.setAuditDescription("REPLACE Address: "+en.toString()+"\n to:"+e.toString());
                                parentLog.addChild(auditLog);
                                // ---------------------------------------------------------------------------------------------
                                userEntity.getAddresses().remove(en);
                                userMgr.evict(en);
                                AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if(org.apache.commons.lang.StringUtils.isBlank(e.getMdTypeId())){
                                    entity.setType(null);
                                }
                                userEntity.getAddresses().add(entity);
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
        MetadataTypeEntity type = null;
        MetadataTypeEntity jobCode = null;
        MetadataTypeEntity employeeType = null;
        if (StringUtils.isNotBlank(pUser.getMdTypeId())) {
            type = metadataTypeDAO.findById(pUser.getMdTypeId());
        }
        if (StringUtils.isNotBlank(pUser.getJobCodeId())) {
            jobCode = metadataTypeDAO.findById(pUser.getJobCodeId());
        }
        if (StringUtils.isNotBlank(pUser.getEmployeeTypeId())) {
            employeeType = metadataTypeDAO.findById(pUser.getEmployeeTypeId());
        }

        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
        if (login == null && StringUtils.isNotEmpty(pUser.getId())) {
            login = loginDozerConverter.convertToDTO(loginManager.getByUserIdManagedSys(pUser.getId(), sysConfiguration.getDefaultManagedSysId()), false);
        }
        if (StringUtils.isNotEmpty(pUser.getFirstName()) && !pUser.getFirstName().equals(userEntity.getFirstName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("FirstName", "old='" + userEntity.getFirstName() + "' new='" + pUser.getFirstName() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getLastName()) && !pUser.getLastName().equals(userEntity.getLastName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastName", "old='" + userEntity.getLastName() + "' new='" + pUser.getLastName() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (pUser.getBirthdate() != null && !pUser.getBirthdate().equals(userEntity.getBirthdate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Birthdate", "old='" + userEntity.getBirthdate() + "' new='" + pUser.getBirthdate() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getCostCenter()) && !pUser.getCostCenter().equals(userEntity.getCostCenter())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("CostCenter", "old='" + userEntity.getCostCenter() + "' new='" + pUser.getCostCenter() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }

        if (StringUtils.isNotEmpty(pUser.getDisplayName()) && !pUser.getDisplayName().equals(userEntity.getDisplayName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("DisplayName", "old='" + userEntity.getDisplayName() + "' new='" + pUser.getDisplayName() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getMaidenName()) && !pUser.getMaidenName().equals(userEntity.getMaidenName())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MaidenName", "old='" + userEntity.getMaidenName() + "' new='" + pUser.getMaidenName() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getNickname()) && !pUser.getNickname().equals(userEntity.getNickname())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Nickname", "old='" + userEntity.getNickname() + "' new='" + pUser.getNickname() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getMiddleInit()) && !pUser.getMiddleInit().equals(userEntity.getMiddleInit())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("MiddleInit", "old='" + userEntity.getMiddleInit() + "' new='" + pUser.getMiddleInit() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeId()) && !pUser.getEmployeeId().equals(userEntity.getEmployeeId())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("EmployeeId", "old='" + userEntity.getEmployeeId() + "' new='" + pUser.getEmployeeId() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getEmployeeTypeId()) && (userEntity.getEmployeeType() == null || !pUser.getEmployeeTypeId().equals(userEntity.getEmployeeType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataTypeDAO.findById(pUser.getEmployeeTypeId());
            auditLog.addCustomRecord("EmployeeType", "old='" + (userEntity.getEmployeeType() != null ? userEntity.getEmployeeType() : "N/A") + "' new='" + metadataType + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getUserTypeInd()) && (userEntity.getType() == null || !pUser.getUserTypeInd().equals(userEntity.getType().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataTypeDAO.findById(pUser.getUserTypeInd());
            auditLog.addCustomRecord("UserType", "old='" + (userEntity.getType() != null ? userEntity.getType() : "N/A") + "' new='" + metadataType + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (StringUtils.isNotEmpty(pUser.getJobCodeId()) && (userEntity.getJobCode() == null || !pUser.getJobCodeId().equals(userEntity.getJobCode().getId()))) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            MetadataTypeEntity metadataType = metadataTypeDAO.findById(pUser.getJobCodeId());
            auditLog.addCustomRecord("JobCode", "old='" + (userEntity.getJobCode() != null ? userEntity.getJobCode() : "N/A") + "' new='" + metadataType + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (pUser.getStartDate() != null && !pUser.getStartDate().equals(userEntity.getStartDate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("StartDate", "old='" + userEntity.getStartDate() + "' new='" + pUser.getStartDate() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (pUser.getLastDate() != null && !pUser.getLastDate().equals(userEntity.getLastDate())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("LastDate", "old='" + userEntity.getLastDate() + "' new='" + pUser.getLastDate() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (pUser.getStatus() != null && !pUser.getStatus().equals(userEntity.getStatus())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Status", "old='" + userEntity.getStatus() + "' new='" + pUser.getStatus() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (pUser.getSecondaryStatus() != null && !pUser.getSecondaryStatus().equals(userEntity.getSecondaryStatus())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("SecondaryStatus", "old='" + userEntity.getSecondaryStatus() + "' new='" + pUser.getSecondaryStatus() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (!StringUtils.equals(pUser.getSuffix(), userEntity.getSuffix())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Suffix", "old='" + userEntity.getSuffix() + "' new='" + pUser.getSuffix() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }
        if (!StringUtils.equals(pUser.getTitle(), userEntity.getTitle())) {
            // Audit Log -----------------------------------------------------------------------------------
            IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.setRequestorUserId(pUser.getRequestorUserId());
            auditLog.setRequestorPrincipal(pUser.getRequestorLogin());
            auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
            auditLog.setAction(AuditAction.REPLACE_PROP.value());
            auditLog.addCustomRecord("Title", "old='" + userEntity.getTitle() + "' new='" + pUser.getTitle() + "'");
            parentLog.addChild(auditLog);
            // ---------------------------------------------------------------------------------------------
        }

        userEntity.updateUser(userDozerConverter.convertToEntity(pUser.getUser(), false));

        userEntity.setType(type);
        userEntity.setJobCode(jobCode);
        userEntity.setEmployeeType(employeeType);
    }

    public void updateUserAttributes(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (pUser.getUserAttributes() != null && !pUser.getUserAttributes().isEmpty()) {
            for (Map.Entry<String, UserAttribute> entry : pUser.getUserAttributes().entrySet()) {
                if (StringUtils.isBlank(entry.getValue().getName())) {
                    throw new IllegalArgumentException("Name can not be empty");
                }
                AttributeOperationEnum operation = entry.getValue().getOperation();
                if (operation == AttributeOperationEnum.DELETE) {
                    userEntity.getUserAttributes().remove(entry.getKey());
                    // Audit Log -----------------------------------------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
                    auditLog.addCustomRecord(entry.getKey(), entry.getValue().getValue());
                    parentLog.addChild(auditLog);
                    // ---------------------------------------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.ADD) {
                    if (userEntity.getUserAttributes().containsKey(entry.getKey())) {
                        throw new IllegalArgumentException("Attribute with this name alreday exists");
                    }
                    UserAttributeEntity e = userAttributeDozerConverter.convertToEntity(entry.getValue(), true);
                    e.setUser(userEntity);
                    userEntity.getUserAttributes().put(entry.getKey(), e);
                    // Audit Log -----------------------------------------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
                    auditLog.addCustomRecord(entry.getKey(), entry.getValue().getValue());
                    parentLog.addChild(auditLog);
                    // ---------------------------------------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    UserAttributeEntity entity = userEntity.getUserAttributes().get(entry.getKey());
                    if (entity != null) {
                        String oldValue = entity.getValue();
                        entity.copyValues(entry.getValue());
                        // Audit Log -----------------------------------------------------------------------------------
                        IdmAuditLog auditLog = new IdmAuditLog();
                        Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                        auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                        auditLog.setAction(AuditAction.REPLACE_ATTRIBUTE.value());
                        auditLog.addCustomRecord(entry.getKey(), ("old= '" + oldValue +
                                "' new= '" + userEntity.getUserAttributes().get(entry.getKey()).getValue() + "'"));
                        parentLog.addChild(auditLog);
                        // ---------------------------------------------------------------------------------------------
                    }
                }
            }
        }
    }

    public void updateSupervisors(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        // Processing supervisors
        String userId = userEntity.getId();
        Set<User> superiors = pUser.getSuperiors();

        if (CollectionUtils.isNotEmpty(superiors)) {
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
                                log.info(String.format("Removed a supervisor user %s from user %s",
                                        e.getId(), userId));
                                // Audit Log
                                //--------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                                LoginEntity loginSupervisor = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), se.getPrincipalList());
                                auditLog.setTargetUser(userEntity.getId(), loginStr);
                                auditLog.setTargetUser(se.getId(), login != null ? loginSupervisor.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_SUPERVISOR.value());

                                auditLog.addCustomRecord("SUPERVISOR", loginSupervisor.getLogin());
                                parentLog.addChild(auditLog);
                                // -------------------------------------------------
                            }
                        }
                    }

                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    userMgr.addSuperior(e.getId(), userId);
                    log.info(String.format("Adding a supervisor user %s for user %s",
                            e.getId(), userId));
                    // Audit Log
                    //--------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    User se = userMgr.getUserDto(e.getId());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    Login loginSupervisor = UserUtils.getUserManagedSysIdentity(sysConfiguration.getDefaultManagedSysId(), se.getPrincipalList());
                    auditLog.setTargetUser(userEntity.getId(), loginStr);
                    auditLog.setTargetUser(se.getId(), login != null ? loginSupervisor.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_SUPERVISOR.value());
                    auditLog.addCustomRecord("SUPERVISOR", loginSupervisor.getLogin());
                    parentLog.addChild(auditLog);
                    // -------------------------------------------------

                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for supervisors");
                }
            }
        }
    }

    public void updateGroups(final UserEntity userEntity, final ProvisionUser pUser,
                             final Set<Group> groupSet, final Set<Group> deleteGroupSet, final IdmAuditLog parentLog) {
        if (CollectionUtils.isNotEmpty(pUser.getGroups())) {
            for (final UserToGroupMembershipXref xref : pUser.getGroups()) {
            	final AttributeOperationEnum operation = xref.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                	final GroupEntity groupEntity = groupManager.getGroupLocalize(xref.getEntityId(), null);
                	userEntity.addGroup(groupEntity, accessRightDAO.findByIds(xref.getAccessRightIds()));
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_GROUP.value());
                    final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetGroup(groupEntity.getId(), groupEntity.getName());
                    auditLog.addCustomRecord("GROUP", groupEntity.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------

                } else if (operation == AttributeOperationEnum.DELETE) {
                	final GroupEntity ge = groupManager.getGroupLocalize(xref.getEntityId(), null);
                	userEntity.removeGroup(ge);
                    final Group dg = groupDozerConverter.convertToDTO(ge, false);
                    dg.setOperation(operation);
                    deleteGroupSet.add(dg);
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.DELETE_GROUP.value());
                    final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetGroup(ge.getId(), ge.getName());
                    auditLog.addCustomRecord("GROUP", ge.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for groups");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userEntity.getGroups())) {
            for (final UserToGroupMembershipXrefEntity xrefEntity : userEntity.getGroups()) {
            	//comment by Lev Bornovalov - why would you do this?  Makes no sense.
                final Group gr = groupDozerConverter.convertToDTO(xrefEntity.getEntity(), false);
                pUser.getGroups().stream().filter(e -> e.getEntityId().equals(xrefEntity.getEntity().getId())).forEach(e -> {
                	gr.setOperation(e.getOperation());
                });
                /*
                for (Group g : pUser.getGroups()) {
                    if (StringUtils.equals(g.getId(), gr.getId())) {
                        gr.setOperation(g.getOperation()); // get operation value from pUser
                        break;
                    }
                }
                */
                groupSet.add(gr);
            }
        }
    }

    public void updateRoles(final UserEntity userEntity, final ProvisionUser pUser,
                            final Set<Role> roleSet, final Set<Role> deleteRoleSet, final IdmAuditLog parentLog) {
        if (CollectionUtils.isNotEmpty(pUser.getRoles())) {
            for (final UserToRoleMembershipXref xref : pUser.getRoles()) {
                final AttributeOperationEnum operation = xref.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                    final RoleEntity roleEntity = roleDataService.getRoleLocalized(xref.getEntityId(), null, null);
                    userEntity.addRole(roleEntity, accessRightDAO.findByIds(xref.getAccessRightIds()));
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_ROLE.value());
                    final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetRole(roleEntity.getId(), roleEntity.getName());
                    auditLog.addCustomRecord("ROLE", roleEntity.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                    final RoleEntity re = roleDataService.getRoleLocalized(xref.getEntityId(), null, null);
                    userEntity.removeRole(re);
                    final Role dr = roleDozerConverter.convertToDTO(re, false);
                    dr.setOperation(operation);
                    deleteRoleSet.add(dr);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.DELETE_ROLE.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetRole(dr.getId(), dr.getName());
                    auditLog.addCustomRecord("ROLE", dr.getName());
                    parentLog.addChild(auditLog);
                    //-----------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for roles");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
            for (final UserToRoleMembershipXrefEntity xrefEntity : userEntity.getRoles()) {
            	//comment by Lev Bornovalov - why would you do this?  Makes no sense.
                final Role ar = roleDozerConverter.convertToDTO(xrefEntity.getEntity(), false);
                pUser.getRoles().stream().filter(e -> e.getEntityId().equals(xrefEntity.getEntity().getId())).forEach(e -> {
                	ar.setOperation(e.getOperation());
                });
                roleSet.add(ar);
            }
        }
    }

    /* User Org Affiliation */

    public void updateAffiliations(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (CollectionUtils.isNotEmpty(pUser.getAffiliations())) {
            for (final UserToOrganizationMembershipXref xref : pUser.getAffiliations()) {
                final AttributeOperationEnum operation = xref.getOperation();
                final OrganizationEntity org = organizationService.getOrganizationLocalized(xref.getEntityId(), null);
                if (operation == AttributeOperationEnum.ADD) {
                    userEntity.addAffiliation(org, accessRightDAO.findByIds(xref.getAccessRightIds()));
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_USER_TO_ORG.value());
                    final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetOrg(org.getId(), org.getName());
                    auditLog.addCustomRecord("ORG", org.getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                	userEntity.removeAffiliation(org);
                    // Audit Log ---------------------------------------------------
                	final IdmAuditLog auditLog = new IdmAuditLog();
                	auditLog.setAction(AuditAction.REMOVE_USER_FROM_ORG.value());
                	final Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                	final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                	auditLog.setTargetUser(pUser.getId(), loginStr);
                	auditLog.setTargetOrg(org.getId(), org.getName());
                	auditLog.addCustomRecord("ORG", org.getName());
                	parentLog.addChild(auditLog);
                	break;
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for affiliations");
                }
            }
        }
    }

    public void updateResources(final UserEntity userEntity, final ProvisionUser pUser, final Set<Resource> resourceSet, final Set<Resource> deleteResourceSet, final IdmAuditLog parentLog) {
    	final Set<ResourceEntity> userEntityResources = (userEntity.getResources() != null) ? 
    			userEntity.getResources().stream().map(e -> e.getEntity()).collect(Collectors.toSet()) : null;
        final Set<Resource> ar = resourceDozerConverter.convertToDTOSet(userEntityResources, false);
        resourceSet.addAll(ar);

        if (CollectionUtils.isNotEmpty(pUser.getResources())) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            for (final UserToResourceMembershipXref xref : pUser.getResources()) {
                final AttributeOperationEnum operation = xref.getOperation();
                final String resourceId = xref.getEntityId();
                final Resource r = resourceDataService.getResource(resourceId, null);
                if (operation == null) {
                    continue;
                } else if (operation == AttributeOperationEnum.ADD) {
                    final ResourceEntity resEntity = resourceService.findResourceById(resourceId);
                    //resEntity.addUser(userEntity, accessRightDAO.findByIds(xref.getAccessRightIds()));
                    userEntity.addResource(resEntity, accessRightDAO.findByIds(xref.getAccessRightIds()));
                    resourceSet.add(r);
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_USER_TO_RESOURCE.value());

                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetResource(resEntity.getId(), resEntity.getName());
                    auditLog.addCustomRecord("RESOURCE", resEntity.getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                	final ResourceEntity re = resourceService.findResourceById(resourceId);
                    userEntity.removeResource(re);
                	//re.removeUser(userEntity);
                    resourceSet.remove(r);
                    deleteResourceSet.add(r);
                    // Audit Log ---------------------------------------------------
                    final IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE.value());
                    final String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetResource(re.getId(), re.getName());
                    auditLog.addCustomRecord("RESOURCE", re.getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for resources");
                }
            }
        }
    }

    private Login getPrincipal(String logingId, List<Login> loginList) {
        for (Login lg : loginList ) {
            if (lg.getId().equals(logingId)) {
                return lg;
            }
        }
        return null;
    }

    public void updatePrincipals(UserEntity userEntity, ProvisionUser pUser, final IdmAuditLog parentLog) {
        // Processing principals
        List<Login> principals = pUser.getPrincipalList();
        if (CollectionUtils.isNotEmpty(principals)) {
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
                            if (en.getId().equals(e.getId())) {
                                it.remove();
                                // Audit Log ---------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setAction(AuditAction.DELETE_PRINCIPAL.value());
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), e.getLogin());
                                parentLog.addChild(auditLog);
                                // --------------------------------------------------------------
                                break;
                            }
                        }
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    LoginEntity entity = loginDozerConverter.convertToEntity(e, false);
                    try {
                        entity.setUserId(userEntity.getId());
                        userEntity.getPrincipalList().add(entity);
                        entity.setPassword(loginManager.encryptPassword(userEntity.getId(), e.getPassword()));
                    } catch (Exception ee) {
                        log.error(ee);
                        ee.printStackTrace();
                    }
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_PRINCIPAL.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), e.getLogin());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {

                    if (CollectionUtils.isNotEmpty(userEntity.getPrincipalList())) {
                        for (final LoginEntity en : userEntity.getPrincipalList()) {
                            if (en.getId().equals(e.getId())) {

                                if (!en.getLogin().equals(e.getLogin())) {
                                    e.setOrigPrincipalName(en.getLogin());
                                }
                                String logOld = en.toString();
                                en.copyProperties(e);

                                // Audit Log ---------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                auditLog.setAction(AuditAction.REPLACE_PRINCIPAL.value());
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                                auditLog.setTargetUser(pUser.getId(), loginStr);
                                auditLog.addCustomRecord(PolicyMapObjectTypeOptions.PRINCIPAL.name(), "old= '" + logOld + "' new='" + e.toString() + "'");
                                parentLog.addChild(auditLog);
                                // --------------------------------------------------------------
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public ObjectResponse requestAddModify(ExtensibleUser extUser, Login mLg, boolean isAdd,
                                           String requestId, final IdmAuditLog idmAuditLog) {

        ObjectResponse response = new ObjectResponse();

        String managedSysId = mLg.getManagedSysId();
        if (managedSysId == null) {
            log.error("managedSysId is not set for Login");
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.INVALID_MANAGED_SYS_ID);
            return response;
        }
        ManagedSysDto mSys = managedSysDozerConverter.convertToDTO(
                managedSystemService.getManagedSysById(managedSysId), true);

        MngSysPolicyDto mngSysPolicy = managedSystemService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSysId, "USER_OBJECT");
        List<AttributeMap> attrMap = managedSystemService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());
           //need to check if  attr.getDataType().getValue() works
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())) {
                extUser.setPrincipalFieldName(attr.getName());
                extUser.setPrincipalFieldDataType(attr.getDataType().getValue());
                break;
            }
        }

        CrudRequest<ExtensibleUser> userReq = new CrudRequest<ExtensibleUser>();
        userReq.setObjectIdentity(mLg.getLogin());
        userReq.setRequestID(requestId);
        userReq.setTargetID(managedSysId);
        userReq.setHostLoginId(mSys.getUserId());
        String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys);

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

        response = isAdd ? connectorAdapter.addRequest(mSys, userReq)
              : connectorAdapter.modifyRequest(mSys, userReq);

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, (isAdd ? "ADD IDENTITY = "
                : "MODIFY IDENTITY = ") + response.getStatus() + " details:" + response.getErrorMsgAsStr());

        IdmAuditLog idmAuditLogChild1 = new IdmAuditLog();
        idmAuditLogChild1.setAction(isAdd ? AuditAction.ADD_USER_TO_RESOURCE.value() : AuditAction.UPDATE_USER_TO_RESOURCE.value());
        LoginEntity lRequestor = loginManager.getPrimaryIdentity(systemUserId);
        idmAuditLogChild1.setRequestorUserId(lRequestor.getUserId());
        idmAuditLogChild1.setRequestorPrincipal(lRequestor.getLogin());
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

        String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys);

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
                                         ManagedSystemObjectMatch matchObj, ExtensibleUser extensibleUser, String operation) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        req.setExtensibleObject(extensibleUser);
        String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys);

        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            req.setBaseDN(matchObj.getBaseDn());
        }
        req.setOperation(operation);
        req.setPassword(password);

        req.setScriptHandler(mSys.getPasswordHandler());

        log.debug("Reset password request will be sent for user login " + login.getLogin());
        return connectorAdapter.resetPasswordRequest(mSys, req);
    }

    protected ProvisionUserResponse validatePassword(Login primaryLogin, ProvisionUser user, String requestId) {

        ProvisionUserResponse resp = new ProvisionUserResponse();

        Password password = new Password();
        password.setManagedSysId(primaryLogin.getManagedSysId());
        password.setPassword(primaryLogin.getPassword());
        password.setPrincipal(primaryLogin.getLogin());

        Policy passwordPolicy = user.getPasswordPolicy();
        if (passwordPolicy == null) {
        	final PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        	searchBean.setUserId(user.getUser().getId());
        	passwordPolicy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);
        }

        try {
            PasswordValidationResponse valCode = passwordManager.isPasswordValidForUserAndPolicy(
                    password, userDozerConverter.convertToEntity(
                            user.getUser(), true),
                    loginDozerConverter.convertToEntity(
                            primaryLogin, true), passwordPolicy);
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

        String passwordDecoded = managedSysDataService.getDecryptedPassword(mSys);

        resumeReq.setHostLoginPassword(passwordDecoded);
        resumeReq.setHostUrl(mSys.getHostUrl());

        log.debug("Resume request will be sent for user login " + login.getLogin());
        return operation ? connectorAdapter.suspendRequest(mSys, resumeReq) :
                connectorAdapter.resumeRequest(mSys, resumeReq);
    }

    @Override
    public Response addEvent(ProvisionActionEvent event, ProvisionActionTypeEnum type) {
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


}
