package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.mule.util.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
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
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
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
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
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
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionActionEvent;
import org.openiam.provision.dto.ProvisionActionTypeEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

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
    @Value("${openiam.service_base}")
    protected String serviceHost;
    @Value("${openiam.idm.ws.path}")
    protected String serviceContext;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
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
    protected AuditLogService auditLogService;
    @Autowired
    protected MetadataTypeDAO metadataTypeDAO;

    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

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

    protected String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if (managedSys.getPswd() != null) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

    protected String getDecryptedPassword(final String userId, final String encodedPassword) {
        String result = null;
        if (StringUtils.isNotEmpty(encodedPassword)) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.password.name()), encodedPassword);
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
        }
        return result;
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
                if(primaryIdentityRule != null && primaryPasswordRule != null) {
                    String identityOutput = (String) ProvisionServiceUtil.getOutputFromAttrMap(
                            primaryIdentityRule, bindingMap, se);
                    primaryIdentity.setLogin(identityOutput);
                    bindingMap.put(TARGET_SYSTEM_IDENTITY,identityOutput);

                    String passwordOutput = (String) ProvisionServiceUtil.getOutputFromAttrMap(
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
        log.debug("setPrimaryIDPassword() ");
        ManagedSysEntity defaultManagedSys = managedSystemService.getManagedSysById(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMapEntity> amEList = managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResourceId());
        List<AttributeMap> policyAttrMap = (amEList == null) ? null : attributeMapDozerConverter.convertToDTOList(amEList, true);
        if (policyAttrMap != null) {
            log.debug("- policyAttrMap IS NOT null");
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
        if (pUser != null) {
            log.info("======= call ProvisionServicePreProcessor: isSkipPreprocessor=" + pUser.isSkipPreprocessor() + ", ");
            try {
                if (!pUser.isSkipPreprocessor() &&
                        (addPreProcessScript = createProvPreProcessScript(preProcessor, bindingMap)) != null) {
                    addPreProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPreProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPreProcess(addPreProcessScript, bindingMap, pUser, passwordSync, operation);

                }
            } finally {
                log.info("======= call ProvisionServicePreProcessor: addPreProcessScript=" + addPreProcessScript + ", ");
            }
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }


    protected int callPostProcessor(String operation, ProvisionUser pUser, Map<String, Object> bindingMap, PasswordSync passwordSync) {

        ProvisionServicePostProcessor<ProvisionUser> addPostProcessScript = null;

        if (pUser != null) {
            log.info("======= call ProvisionServicePostProcessor: isSkipPostprocessor=" + pUser.isSkipPostProcessor() + ", ");
            try {
                if (!pUser.isSkipPostProcessor() &&
                        (addPostProcessScript = createProvPostProcessScript(postProcessor, bindingMap)) != null) {
                    addPostProcessScript.setMuleContext(MuleContextProvider.getCtx());
                    addPostProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                    return executeProvisionPostProcess(addPostProcessScript, bindingMap, pUser, passwordSync, operation);
                }
            } finally {
                log.info("======= call ProvisionServicePostProcessor: addPostProcessScript=" + addPostProcessScript + ", ");
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
            return (ProvisionServicePreProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePostProcessor<ProvisionUser> createProvPostProcessScript(String scriptName, Map<String, Object> tmpMap) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            return (ProvisionServicePostProcessor<ProvisionUser>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected int executeProvisionPreProcess(ProvisionServicePreProcessor<ProvisionUser> ppScript,
                                             Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        }

        return 0;
    }

    protected int executeProvisionPostProcess(ProvisionServicePostProcessor<ProvisionUser> ppScript,
                                              Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        }
        return 0;
    }

    static int executePreProcess(PreProcessor<ProvisionUser> ppScript,
                                    Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, LookupRequest lookupRequest,  String operation) {
        log.info("======= call PreProcessor: ppScript=" + ppScript + ", operation="+operation);
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        } else
        if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(lookupRequest);
        }
        return 0;
    }

    static int executePostProcess(PostProcessor<ProvisionUser> ppScript,
                                     Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, SearchResponse searchResponse, String operation,  boolean success) {
        log.info("======= call PostProcessor: ppScript=" + ppScript + ", operation="+operation);
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap, success);
        } else
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap, success);
        } else
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap, success);
        } else
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap, success);
        } else
        if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap, success);
        } else
        if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap, success);
        } else
        if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(searchResponse);
        }
        return 0;
    }

    public void updateEmails(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        // Processing emails
        Set<EmailAddress> emailAddresses = pUser.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
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
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                        entity.setMetadataType(null);
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
                            if (StringUtils.equals(en.getEmailId(), e.getEmailId())) {
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
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.DELETE_PHONE.value());
                                auditLog.setAuditDescription("DELETE Phone: " + e.toString());
                                parentLog.addChild(auditLog);
                                // -------------------------------------
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
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                    auditLog.setAction(AuditAction.ADD_PHONE.value());
                    auditLog.setAuditDescription("ADD Phone: " + e.toString());
                    parentLog.addChild(auditLog);
                    // ----------------------------------------
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    Set<PhoneEntity> entities = userEntity.getPhones();
                    if (CollectionUtils.isNotEmpty(entities)) {
                        for (PhoneEntity en : entities) {
                            if (StringUtils.equals(en.getPhoneId(), e.getPhoneId())) {
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
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                                    entity.setMetadataType(null);
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
                    if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                        entity.setMetadataType(null);
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
                            if (StringUtils.equals(en.getAddressId(), e.getAddressId())) {
                                // Audit Log -----------------------------------------------------------------------------------
                                IdmAuditLog auditLog = new IdmAuditLog();
                                Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                                auditLog.setTargetUser(userEntity.getId(), login != null ? login.getLogin() : StringUtils.EMPTY);
                                auditLog.setAction(AuditAction.REPLACE_ADDRESS.value());
                                auditLog.setAuditDescription("REPLACE Address: " + en.toString() + "\n to:" + e.toString());
                                parentLog.addChild(auditLog);
                                // ---------------------------------------------------------------------------------------------
                                userEntity.getAddresses().remove(en);
                                userMgr.evict(en);
                                AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                                entity.setParent(userEntity);
                                if (org.apache.commons.lang.StringUtils.isBlank(e.getMetadataTypeId())) {
                                    entity.setMetadataType(null);
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
            for (Group g : pUser.getGroups()) {
                AttributeOperationEnum operation = g.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                    GroupEntity groupEntity = groupManager.getGroup(g.getId());
                    userEntity.getGroups().add(groupEntity);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_GROUP.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetGroup(g.getId(), g.getName());
                    auditLog.addCustomRecord("GROUP", g.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------

                } else if (operation == AttributeOperationEnum.DELETE) {
                    GroupEntity ge = groupManager.getGroup(g.getId());
                    userEntity.getGroups().remove(ge);
                    Group dg = groupDozerConverter.convertToDTO(ge, false);
                    dg.setOperation(operation);
                    deleteGroupSet.add(dg);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.DELETE_GROUP.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetGroup(g.getId(), g.getName());
                    auditLog.addCustomRecord("GROUP", g.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for groups");
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
        if (CollectionUtils.isNotEmpty(pUser.getRoles())) {
            for (Role r : pUser.getRoles()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                    RoleEntity roleEntity = roleDataService.getRole(r.getId());
                    if (userEntity.getRoles().contains(roleEntity)) {
                        throw new IllegalArgumentException("Role with this name already exists");
                    }
                    userEntity.getRoles().add(roleEntity);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_ROLE.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetRole(r.getId(), r.getName());
                    auditLog.addCustomRecord("ROLE", r.getName());
                    parentLog.addChild(auditLog);
                    //--------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                    RoleEntity re = roleDataService.getRole(r.getId());
                    userEntity.getRoles().remove(re);
                    Role dr = roleDozerConverter.convertToDTO(re, false);
                    dr.setOperation(operation);
                    deleteRoleSet.add(dr);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.DELETE_ROLE.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetRole(r.getId(), r.getName());
                    auditLog.addCustomRecord("ROLE", r.getName());
                    parentLog.addChild(auditLog);
                    //-----------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for roles");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
            for (RoleEntity ure : userEntity.getRoles()) {
                Role ar = roleDozerConverter.convertToDTO(ure, false);
                for (Role r : pUser.getRoles()) {
                    if (StringUtils.equals(r.getId(),ar.getId())) {
                        ar.setOperation(r.getOperation()); // get operation value from pUser
                        break;
                    }
                }
                roleSet.add(ar);
            }
        }
    }

    /* User Org Affiliation */

    public void updateAffiliations(final UserEntity userEntity, final ProvisionUser pUser, final IdmAuditLog parentLog) {
        if (CollectionUtils.isNotEmpty(pUser.getAffiliations())) {
            for (Organization o : pUser.getAffiliations()) {
                AttributeOperationEnum operation = o.getOperation();
                if (operation == AttributeOperationEnum.ADD) {
                    OrganizationEntity org = organizationService.getOrganizationLocalized(o.getId(), null);
                    userEntity.getAffiliations().add(org);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_USER_TO_ORG.value());
                    Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetOrg(org.getId(), org.getName());
                    auditLog.addCustomRecord("ORG", org.getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                    Set<OrganizationEntity> affiliations = userEntity.getAffiliations();
                    for (OrganizationEntity a : affiliations) {
                        if (StringUtils.equals(o.getId(),a.getId())) {
                            userEntity.getAffiliations().remove(a);
                            // Audit Log ---------------------------------------------------
                            IdmAuditLog auditLog = new IdmAuditLog();
                            auditLog.setAction(AuditAction.REMOVE_USER_FROM_ORG.value());
                            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
                            String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                            auditLog.setTargetUser(pUser.getId(), loginStr);
                            auditLog.setTargetOrg(o.getId(), o.getName());
                            auditLog.addCustomRecord("ORG", o.getName());
                            parentLog.addChild(auditLog);
                            // -------------------------------------------------------------
                            break;
                        }
                    }

                } else if (operation == AttributeOperationEnum.REPLACE) {
                    throw new UnsupportedOperationException("Operation 'REPLACE' is not supported for affiliations");
                }
            }
        }
    }

    public void updateResources(final UserEntity userEntity, final ProvisionUser pUser, final Set<Resource> resourceSet, final Set<Resource> deleteResourceSet, final IdmAuditLog parentLog) {

        Set<Resource> ar = resourceDozerConverter.convertToDTOSet(userEntity.getResources(), false);
        resourceSet.addAll(ar);

        if (CollectionUtils.isNotEmpty(pUser.getResources())) {
            Login login = pUser.getPrimaryPrincipal(sysConfiguration.getDefaultManagedSysId());
            for (Resource r : pUser.getResources()) {
                AttributeOperationEnum operation = r.getOperation();
                if (operation == null) {
                    continue;
                } else if (operation == AttributeOperationEnum.ADD) {
                    ResourceEntity resEntity = resourceService.findResourceByIdNoLocalized(r.getId());
                    userEntity.getResources().add(resEntity);
                    resourceSet.add(r);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.ADD_USER_TO_RESOURCE.value());

                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
                    auditLog.setTargetUser(pUser.getId(), loginStr);
                    auditLog.setTargetResource(resEntity.getId(), resEntity.getName());
                    auditLog.addCustomRecord("RESOURCE", resEntity.getName());
                    parentLog.addChild(auditLog);
                    // --------------------------------------------------------------
                } else if (operation == AttributeOperationEnum.DELETE) {
                    ResourceEntity re = resourceService.findResourceByIdNoLocalized(r.getId());
                    userEntity.getResources().remove(re);
                    resourceSet.remove(r);
                    deleteResourceSet.add(r);
                    // Audit Log ---------------------------------------------------
                    IdmAuditLog auditLog = new IdmAuditLog();
                    auditLog.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE.value());
                    String loginStr = login != null ? login.getLogin() : StringUtils.EMPTY;
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
        for (Login lg : loginList) {
            if (lg.getLoginId().equals(logingId)) {
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
                            if (StringUtils.equals(en.getLoginId(),e.getLoginId())) {
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
                            if (StringUtils.equals(en.getLoginId(),e.getLoginId())) {

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

        List<AttributeMapEntity> attrMapEntities = managedSystemService
                .getAttributeMapsByManagedSysId(managedSysId);
        List<AttributeMap> attrMap = attributeMapDozerConverter.convertToDTOList(attrMapEntities, false);     //need to check if  attr.getDataType().getValue() works
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
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
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

        response = isAdd ? connectorAdapter.addRequest(mSys, userReq, MuleContextProvider.getCtx())
                : connectorAdapter.modifyRequest(mSys, userReq, MuleContextProvider.getCtx());
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
        request.setExtensibleObject(new ExtensibleUser());
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

        return resp;
    }

    protected ResponseType resetPassword(String requestId, Login login,
                                         String password, ManagedSysDto mSys,
                                         ManagedSystemObjectMatch matchObj, ExtensibleUser extensibleUser) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        req.setExtensibleObject(extensibleUser);
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            req.setBaseDN(matchObj.getBaseDn());
        }
        req.setOperation("RESET_PASSWORD");
        req.setPassword(password);

        req.setScriptHandler(mSys.getPasswordHandler());

        log.debug("Reset password request will be sent for user login " + login.getLogin());
        return connectorAdapter.resetPasswordRequest(mSys, req, MuleContextProvider.getCtx());

    }

    protected ResponseType setPassword(String requestId, Login login, String prevDecPassword,
                                       String newDecPasswordSync,
                                       ManagedSysDto mSys,
                                       ManagedSystemObjectMatch matchObj,
                                       ExtensibleUser extensibleUser) {

        PasswordRequest req = new PasswordRequest();
        req.setObjectIdentity(login.getLogin());
        req.setRequestID(requestId);
        req.setTargetID(login.getManagedSysId());
        req.setHostLoginId(mSys.getUserId());
        req.setExtensibleObject(extensibleUser);
        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        req.setHostLoginPassword(passwordDecoded);
        req.setHostUrl(mSys.getHostUrl());
        req.setBaseDN((matchObj != null) ? matchObj.getBaseDn() : null);
        req.setOperation("SET_PASSWORD");
        req.setPassword(newDecPasswordSync);
        req.setScriptHandler(mSys.getPasswordHandler());
        req.setCurrentPassword(prevDecPassword);
        ResponseType respType = connectorAdapter.setPasswordRequest(mSys, req, MuleContextProvider.getCtx());

        return respType;

    }

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

        String passwordDecoded = mSys.getPswd();
        try {
            passwordDecoded = getDecryptedPassword(mSys);
        } catch (ConnectorDataException e) {
            e.printStackTrace();
        }
        resumeReq.setHostLoginPassword(passwordDecoded);
        resumeReq.setHostUrl(mSys.getHostUrl());

        log.debug("Resume request will be sent for user login " + login.getLogin());
        return operation ? connectorAdapter.suspendRequest(mSys, resumeReq, MuleContextProvider.getCtx()) :
                connectorAdapter.resumeRequest(mSys, resumeReq, MuleContextProvider.getCtx());
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
