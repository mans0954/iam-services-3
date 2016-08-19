package org.openiam.provision.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.response.LookupObjectResponse;
import org.openiam.base.response.ObjectResponse;
import org.openiam.base.response.ResponseType;
import org.openiam.base.response.SearchResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.PostProcessor;
import org.openiam.provision.PreProcessor;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.request.CrudRequest;
import org.openiam.provision.request.LookupRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.utils.ProvisionUtils;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("groupProvisionDataService")
public class GroupProvisionDataServiceImpl implements ObjectProvisionDataService<ProvisionGroup> {
    @Autowired
    protected ManagedSystemService managedSysService;

    @Autowired
    protected ValidateConnectionConfig validateConnectionConfig;

    @Autowired
    private UserDozerConverter userDozerConverter;

    protected static final Log log = LogFactory.getLog(GroupProvisionDataServiceImpl.class);

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Value(",${org.openiam.debug.hidden.attributes},")
    private String hiddenAttributes;

    @Value("${org.openiam.idm.preProcessorGroup.groovy.script}")
    protected String preProcessorGroup;

    @Value("${org.openiam.idm.postProcessorGroup.groovy.script}")
    protected String postProcessorGroup;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private ManagedSystemService managedSystemService;

    @Autowired
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    private GroupDataService groupManager;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Autowired
    protected LoginDataService loginManager;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    protected ConnectorAdapter connectorAdapter;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    protected UserDataService userDataService;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    protected ProvisioningDataService provisionService;

    @Autowired
    protected ProvisionQueueService provQueueService;
    @Autowired
    ProvisionServiceUtil provisionServiceUtil;
    @Autowired
    protected AuditLogService auditLogService;

    @Override
    public Response add(final ProvisionGroup group) throws Exception {
        return provisioning(group, true);
    }

    @Override
    public Response modify(ProvisionGroup group) {
        return provisioning(group, false);
    }

    @Override
    public Response modifyIdentity(IdentityDto identity) {
        Response response = new Response(ResponseStatus.FAILURE);
        final IdentityDto identityDto = identity;
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            response = transactionTemplate.execute(new TransactionCallback<Response>() {
                @Override
                public Response doInTransaction(TransactionStatus status) {
                    Group group = groupManager.getGroupDTO(identityDto.getReferredObjectId());
                    ProvisionGroup pGroup = new ProvisionGroup(group);
                    ManagedSysDto managedSys = managedSystemService.getManagedSys(identityDto.getManagedSysId());
                    Resource res = resourceService.getResourceDTO(managedSys.getResourceId());
                    return provisioningIdentity(identityDto, pGroup, managedSys, res, false);
                }
            });

        } catch(Throwable t){
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.setStatus(ResponseStatus.FAILURE);
            t.printStackTrace();
        }
        return response;
    }

    private Response provisioning(final ProvisionGroup group, final boolean isAdd) {
        Response response = new Response(ResponseStatus.FAILURE);
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            response = transactionTemplate.execute(new TransactionCallback<Response>() {
                @Override
                public Response doInTransaction(TransactionStatus status) {
                    return provisionGroup(group, isAdd);
                }
            });

        } catch(Throwable t){
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.setStatus(ResponseStatus.FAILURE);
            t.printStackTrace();
        }
        return response;
    }

    private Response provisionGroup(final ProvisionGroup pGroup, final boolean isAdd) {
        // bind the objects to the scripting engine
        Map<String, Object> bindingMap = new HashMap<>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
        bindingMap.put(AbstractProvisioningService.GROUP, pGroup);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);

        Response response = new Response();
        if (callPreProcessor(isAdd ? "ADD" : "MODIFY", pGroup, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }
        // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT

        int callPostProcessorResult = callPostProcessor(isAdd ? "ADD" : "MODIFY", pGroup, bindingMap);
        //     auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "callPostProcessor result="
        //             + (callPostProcessorResult == 1 ? "SUCCESS" : "FAIL"));
        if (callPostProcessorResult != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            //         auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "PostProcessor error.");
            return response;
        }
        response.setStatus(ResponseStatus.SUCCESS);
        response.setErrorCode(ResponseCode.SUCCESS);
        return response;
    }

    protected Response provisioningIdentity(IdentityDto groupTargetIdentity, ProvisionGroup pGroup,
                                           ManagedSysDto managedSys, Resource res, boolean isAdd) {

        Map<String, Object> bindingMap = new HashMap<>();
        Response response = new Response(ResponseStatus.SUCCESS);

        if(groupTargetIdentity == null) {
            // Generate new identity
            MngSysPolicyDto mngSysPolicy = managedSysService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSys.getId(), "GROUP_OBJECT");
            List<AttributeMap> attrMap = managedSysService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());
            bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
            bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, managedSys.getResourceId());
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSys.getId());
            bindingMap.put(AbstractProvisioningService.GROUP, pGroup);
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParamDTO(managedSys.getId(), ManagedSystemObjectMatch.GROUP);
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }
            try {
            	if(log.isDebugEnabled()) {
            		log.debug(" - Building principal Name for: " + managedSys.getId());
            	}
                String newIdentity = provisionServiceUtil.buildGroupPrincipalName(attrMap, scriptRunner, bindingMap);

                if (StringUtils.isBlank(newIdentity)) {
                	if(log.isDebugEnabled()) {
                		log.debug("Primary identity not found...");
                	}
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.IDENTITY_NOT_FOUND);
                    return response;
                }

                groupTargetIdentity = new IdentityDto(IdentityTypeEnum.GROUP);
                groupTargetIdentity.setIdentity(newIdentity);
                groupTargetIdentity.setCreateDate(new Date());
                groupTargetIdentity.setCreatedBy(systemUserId);
                groupTargetIdentity.setManagedSysId(managedSys.getId());
                groupTargetIdentity.setReferredObjectId(pGroup.getId());
                groupTargetIdentity.setStatus(LoginStatusEnum.ACTIVE);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.INTERNAL_ERROR);
                return response;
            }
        } else {
            // Rename identity id required
            if (StringUtils.isNotEmpty(groupTargetIdentity.getId())) {
                IdentityDto groupOriginalIdentity = identityService.getIdentity(groupTargetIdentity.getId());
                if (groupOriginalIdentity != null) {
                    if (!StringUtils.equals(groupOriginalIdentity.getIdentity(), groupTargetIdentity.getIdentity())) {
                        groupTargetIdentity.setOrigPrincipalName(groupOriginalIdentity.getIdentity());
                    }
                }
            }
        }

        String groupTargetIdentityId = identityService.save(groupTargetIdentity);
        groupTargetIdentity.setId(groupTargetIdentityId);

        // bind the objects to the scripting engine
        final boolean isDelete = LoginStatusEnum.INACTIVE.equals(groupTargetIdentity.getStatus());
        final String operation = isDelete ? "DELETE" : isAdd ? "ADD" : "MODIFY";
        bindingMap.put("operation", operation);
        bindingMap.put("sysId", managedSys.getId());
        bindingMap.put("managedSysId", managedSys.getId());
        bindingMap.put("group", pGroup);
        bindingMap.put("identity", groupTargetIdentity);
        bindingMap.put("requesterId", systemUserId);

        MngSysPolicyDto mngSysPolicy = managedSysService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSys.getId(), "GROUP_OBJECT");
        List<AttributeMap> attrMap = managedSysService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] objList = managedSystemService.managedSysObjectParamDTO(managedSys.getId(),
                ManagedSystemObjectMatch.GROUP);
        if (objList != null && objList.length > 0) {
            matchObj = objList[0];
        }

        ExtensibleObject extObj = buildFromRules(attrMap, bindingMap);

        // get the attributes at the target system
        // this lookup only for getting attributes from the
        // system
        String requestId = "R" + UUIDGen.getUUID();
        Map<String, ExtensibleAttribute> currentValueMap = new HashMap<>();

        boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(requestId, groupTargetIdentity, extObj, managedSys,
                matchObj, currentValueMap, res);

        boolean connectorSuccess = false;
       // pre-processing
        bindingMap.put("targetSystemAttributes", currentValueMap);
        ResourceProp preProcessProp = res.getResourceProperty("GROUP_PRE_PROCESS");
        String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(preProcessScript)) {

            PreProcessor<ProvisionGroup> ppScript = createPreProcessScript(preProcessScript, bindingMap);
            if (ppScript != null) {
                int executePreProcessResult = executePreProcess(ppScript, bindingMap, pGroup, null,
                        isDelete ? "DELETE" : isExistedInTargetSystem ? "MODIFY" : "ADD");
                if (executePreProcessResult == ProvisioningConstants.FAIL) {
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                    return response;
                }
            }
        }

        if (isDelete && isExistedInTargetSystem) { // delete existing group

            connectorSuccess = requestDelete(groupTargetIdentity, requestId, managedSys, matchObj)
                    .getStatus() != StatusCodeType.FAILURE;

        } else if (isDelete && !isExistedInTargetSystem) { // delete not existing group

            connectorSuccess = true;
            log.warn("Requested to delete not existing identity: " + groupTargetIdentity);

        } else if (!isExistedInTargetSystem) { // if group doesn't exist in target system

            // updates the attributes with the correct operation codes
            extObj = ProvisionUtils.updateAttributeList(extObj, null);

            connectorSuccess = requestAddModify(groupTargetIdentity, requestId, managedSys, matchObj, extObj, true)
                    .getStatus() != StatusCodeType.FAILURE;

        } else { // if group exists in target system

            // updates the attributes with the correct operation codes
            extObj = ProvisionUtils.updateAttributeList(extObj, currentValueMap);

            if (groupTargetIdentity.getOrigPrincipalName() != null) {
                extObj.getAttributes().add(
                        new ExtensibleAttribute("ORIG_IDENTITY", groupTargetIdentity.getOrigPrincipalName(),
                                AttributeOperationEnum.REPLACE.getValue(), "String"));
            }
            connectorSuccess = requestAddModify(groupTargetIdentity, requestId, managedSys, matchObj, extObj, false)
                    .getStatus() != StatusCodeType.FAILURE;

            if (!connectorSuccess) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_CONNECTOR);
            }
        }

        // post processing
        ResourceProp postProcessProp = res.getResourceProperty("GROUP_POST_PROCESS");
        String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(postProcessScript)) {
            PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
            if (ppScript != null) {
                int executePostProcessResult = executePostProcess(ppScript, bindingMap, pGroup, null,
                        isExistedInTargetSystem ? "MODIFY" : "ADD", connectorSuccess);

                if (executePostProcessResult == ProvisioningConstants.FAIL) {
                    response.setStatus(ResponseStatus.FAILURE);
                    response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                    return response;
                }
            }
        }

        return response;
    }

    private boolean isMemberAvailableInResource(final UserEntity member, final String resourceId) {
        boolean result = false;
        for(final UserToResourceMembershipXrefEntity xref : member.getResources()) {
            if(xref.getEntity().getId().equalsIgnoreCase(resourceId)) {
                return true;
            }
        }
        return (member.getRoles().stream().map(e -> e.getEntity()).filter(e -> e.getResource(resourceId) != null).count() > 0);
    }

    protected int callPreProcessor(String operation, ProvisionGroup pGroup, Map<String, Object> bindingMap ) {

        ProvisionServicePreProcessor addPreProcessScript = null;
        if ( pGroup != null) {
            System.out.println("======= callPreProcessor: isSkipPreprocessor="+pGroup.isSkipPreprocessor()+", ");
            if (!pGroup.isSkipPreprocessor() &&
                    (addPreProcessScript = createProvPreProcessScript(preProcessorGroup, bindingMap)) != null) {

                addPreProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                return executeProvisionPreProcess(addPreProcessScript, bindingMap, pGroup, null, operation);

            }
            System.out.println("======= callPreProcessor: addPreProcessScript="+addPreProcessScript+", ");
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }


    protected int callPostProcessor(String operation, ProvisionGroup pGroup, Map<String, Object> bindingMap ) {

        ProvisionServicePostProcessor addPostProcessScript;

        if ( pGroup != null) {
            if (!pGroup.isSkipPostProcessor() &&
                    (addPostProcessScript = createProvPostProcessScript(postProcessorGroup, bindingMap)) != null) {

                addPostProcessScript.setApplicationContext(SpringContextProvider.getApplicationContext());
                return executeProvisionPostProcess(addPostProcessScript, bindingMap, pGroup, null, operation);

            }
        }
        // pre-processor was skipped
        return ProvisioningConstants.SUCCESS;
    }
    protected int executeProvisionPreProcess(ProvisionServicePreProcessor<ProvisionGroup> ppScript,
                                             Map<String, Object> bindingMap, ProvisionGroup pGroup, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(pGroup, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(pGroup, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(pGroup, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        }

        return 0;
    }

    protected int executeProvisionPostProcess(ProvisionServicePostProcessor<ProvisionGroup> ppScript,
                                              Map<String, Object> bindingMap, ProvisionGroup pGroup, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(pGroup, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(pGroup, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(pGroup, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        }

        return 0;
    }

    protected PreProcessor<ProvisionGroup> createPreProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PreProcessor<ProvisionGroup>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected PostProcessor<ProvisionGroup> createPostProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (PostProcessor<ProvisionGroup>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected int executePreProcess(PreProcessor<ProvisionGroup> ppScript,
                                    Map<String, Object> bindingMap, ProvisionGroup group, PasswordSync passwordSync, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(group, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(group, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(group, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        }

        return 0;
    }

    protected int executePostProcess(PostProcessor<ProvisionGroup> ppScript,
                                     Map<String, Object> bindingMap, ProvisionGroup group, PasswordSync passwordSync, String operation, boolean success) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(group, bindingMap, success);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(group, bindingMap, success);

        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(group, bindingMap, success);

        }

        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap, success);
        }

        return 0;
    }


    protected ProvisionServicePreProcessor<ProvisionGroup> createProvPreProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (ProvisionServicePreProcessor<ProvisionGroup>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    protected ProvisionServicePostProcessor<ProvisionGroup> createProvPostProcessScript(String scriptName, Map<String, Object> bindingMap) {
        try {
            return (ProvisionServicePostProcessor<ProvisionGroup>) scriptRunner.instantiateClass(bindingMap, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }
    private ObjectResponse requestAddModify(IdentityDto identityDto, String requestId, ManagedSysDto mSys,
                                     ManagedSystemObjectMatch matchObj, ExtensibleObject extensibleObject, boolean isAdd) {

        ObjectResponse resp = new ObjectResponse();

        if (mSys.getSkipGroupProvision()) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.SKIP_PROVISIONING);
            if(log.isDebugEnabled()) {
            	log.debug("GroupProvision:requestAddModify skipped: SkipGroupProvision flag TRUE");
            }
            return resp;
        }
        CrudRequest<ExtensibleObject> userReq = new CrudRequest<>();
        userReq.setObjectIdentity(identityDto.getIdentity());
        userReq.setRequestID(requestId);
        userReq.setTargetID(identityDto.getManagedSysId());
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
        userReq.setOperation(isAdd ? "ADD" : "MODIFY");
        userReq.setExtensibleObject(extensibleObject);
        userReq.setScriptHandler(mSys.getAddHandler());

        resp = isAdd ? connectorAdapter.addRequest(mSys, userReq)
                : connectorAdapter.modifyRequest(mSys, userReq);
        /*auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, (isAdd ? "ADD IDENTITY = "
                : "MODIFY IDENTITY = ") + resp.getStatus() + " details:" + resp.getErrorMsgAsStr());*/
        return resp;
    }

    protected String getDecryptedPassword(ManagedSysDto managedSys) throws ConnectorDataException {
        String result = null;
        if( managedSys.getPswd()!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()),managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

    private boolean getCurrentObjectAtTargetSystem(String requestId, IdentityDto identityDto, ExtensibleObject extensibleObject,
                                                   ManagedSysDto mSys, ManagedSystemObjectMatch matchObj, Map<String, ExtensibleAttribute> curValueMap, Resource res) {

        String identity = identityDto.getIdentity();
        if(log.isDebugEnabled()) {
        	log.debug("Getting the current attributes in the target system for =" + identity);

        	log.debug("- IsRename: " + identityDto.getOrigPrincipalName());
        }
        if (identityDto.getOrigPrincipalName() != null && !identityDto.getOrigPrincipalName().isEmpty()) {
            identity = identityDto.getOrigPrincipalName();
        }

        LookupRequest<ExtensibleObject> reqType = new LookupRequest<ExtensibleObject>();
        reqType.setRequestID(requestId);
        reqType.setSearchValue(identity);

        reqType.setTargetID(identityDto.getManagedSysId());
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
        reqType.setExtensibleObject(extensibleObject);
        reqType.setScriptHandler(mSys.getLookupHandler());

//PRE processor
        Map<String, Object> bindingMap = new HashMap<>();
        ResourceProp preProcessProp = res.getResourceProperty("GROUP_PRE_PROCESS");
        String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(preProcessScript)) {
            PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
            if (ppScript != null) {
                int executePreProcessResult = ProvisionUtils.executePreProcess(ppScript, bindingMap, null, null, reqType,
                        "LOOKUP");
                if (executePreProcessResult == ProvisioningConstants.FAIL) {
                    return false;
                }
            }
        }
        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType);
//POST processor
        ResourceProp postProcessProp = res.getResourceProperty("GROUP_POST_PROCESS");
        String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(postProcessScript)) {
            PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
            if (ppScript != null) {
                ProvisionUtils.executePostProcess(ppScript, bindingMap, null, null, lookupSearchResponse,
                        "LOOKUP", lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS);
            }
        }

        if (lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ExtensibleAttribute> extAttrList = lookupSearchResponse.getObjectList().size() > 0 ? lookupSearchResponse
                    .getObjectList().get(0).getAttributeList()
                    : new LinkedList<ExtensibleAttribute>();

            if (extAttrList != null) {
                for (ExtensibleAttribute obj : extAttrList) {
                    curValueMap.put(obj.getName(), obj);
                }
            } else {
                log.debug(" - NO attributes found in target system lookup ");
            }
            return true;
        }

        return false;
    }


    private ExtensibleObject buildFromRules(List<AttributeMap> attrMap,
                                          Map<String, Object> bindingMap) {

        ExtensibleGroup extensibleObject = new ExtensibleGroup();

        if (attrMap != null) {
        	if(log.isDebugEnabled()) {
        		log.debug("buildFromRules: attrMap IS NOT null");
        	}

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }



                String objectType = attr.getMapForObjectType();
                if (objectType != null) {

                    if (objectType.equalsIgnoreCase("GROUP")) {
                        Object output = "";
                        try {
                            output = provisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, scriptRunner);
                        } catch (ScriptEngineException see) {
                            log.error("Error in script = '", see);
                            continue;
                        } catch (Exception mpe) {
                            log.error("Error in script = '", mpe);
                            continue;
                        }
                        if(log.isDebugEnabled()) {
                        log.debug("buildFromRules: OBJECTTYPE="+objectType+", ATTRIBUTE=" + attr.getName() +
                                ", SCRIPT OUTPUT=" +
                                (hiddenAttributes.toLowerCase().contains(","+attr.getName().toLowerCase()+",")
                                        ? "******" : output));
                        }
                        if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getName(), (String) output, -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getName(),
                                        ((Integer) output).toString(), -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getName(), sdf.format(d), -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extensibleObject.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extensibleObject.getAttributes().add(
                                        new ExtensibleAttribute(attr.getName(), (byte[]) output, -1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getName(),
                                        (BaseAttributeContainer) output, -1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getName(), (List) output, -1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extensibleObject.getAttributes().add(newAttr);

                                log.debug("buildFromRules: added attribute to extGroup:" + attr.getName());
                            }
                        }
                    } else if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(objectType)) {

                        extensibleObject.setPrincipalFieldName(attr.getName());
                        extensibleObject.setPrincipalFieldDataType(attr.getDataType().getValue());

                    }
                }
            }
        }

        return extensibleObject;
    }


    @Override
    public Response delete(String managedSystemId, String groupId, UserStatusEnum status, String requesterId) {
    	if(log.isDebugEnabled()) {
    		log.debug("----deleteGroup called.------");
    	}

        Response response = new Response(ResponseStatus.SUCCESS);

        if (status != UserStatusEnum.DELETED && status != UserStatusEnum.REMOVE) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            return response;
        }

        String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        IdentityDto identityDto = identityService.getIdentityByManagedSys(groupId, managedSystemId);
        if (identityDto == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.IDENTITY_NOT_FOUND);
            return response;
        }
        // check if the user active
        if (groupId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
            return response;
        }

        Group groupDto = groupManager.getGroupDTO(groupId);

        ProvisionGroup pGroup = new ProvisionGroup(groupDto);
        // SET PRE ATTRIBUTES FOR DEFAULT SYS SCRIPT
        Map<String, Object> bindingMap = new HashMap<>();
        bindingMap.put("operation", "DELETE");
        bindingMap.put("sysId", identityDto.getManagedSysId());
        bindingMap.put("group", pGroup);
        bindingMap.put("identity", identityDto.getIdentity());

        if (callPreProcessor("DELETE", pGroup, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }

        if (!managedSystemId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {
            // managedSysId point to one of the secondary identities,
            // terminating only this identity
            deleteIdentity(identityDto, pGroup, status, requestId);

        } else {
            // update the identities and set them to inactive
            List<IdentityDto> principalList = identityService.getIdentities(groupId);
            if (principalList != null) {
                for (IdentityDto identity : principalList) {
                    // this try-catch block for protection other operations and
                    // other resources if one resource was fall with error
                    try {
                        if (identity.getStatus() != LoginStatusEnum.INACTIVE) {
                            // only add the connectors if its a secondary identity.
                            if (!identity.getManagedSysId().equals(sysConfiguration.getDefaultManagedSysId())) {
                                deleteIdentity(identity, pGroup, status, requestId);
                            }
                        } else {
                            if (status == UserStatusEnum.REMOVE) {
                                identityService.deleteIdentity(identity.getId());
                            }
                        }
                    } catch (Throwable tw) {
                        log.error(identity, tw);
                        tw.printStackTrace();
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.INTERNAL_ERROR);
                        return response;
                    }
                }
            }
        }

        if (status == UserStatusEnum.REMOVE) {
            identityService.deleteIdentity(identityDto.getId());
            try {
                groupManager.deleteGroup(groupId);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_SQL_ERROR);
                return response;
            }
        } else {
            groupDto.setStatus(status.getValue());
            groupDto.setLastUpdatedBy(requesterId);
            groupDto.setLastUpdate(new Date(System.currentTimeMillis()));
            groupManager.saveGroup(groupDto, null);
        }
        // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT

        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, identityDto.getIdentity());
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, null);

        if (callPostProcessor("DELETE", pGroup, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return response;
        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    protected void deleteIdentity(IdentityDto identity, ProvisionGroup pGroup, UserStatusEnum status, String requestId) {

        Map<String, Object> bindingMap = new HashMap<>();

        ManagedSysDto mSys = managedSystemService.getManagedSys(identity.getManagedSysId());
        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParamDTO(mSys.getId(),
                ManagedSystemObjectMatch.GROUP);
        if (matchObjAry != null && matchObjAry.length > 0) {
            matchObj = matchObjAry[0];
            bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
        }

        if(log.isDebugEnabled()) {
	        log.debug("Deleting identity: " + identity.getIdentity());
	        log.debug(" - managed sys id: " + mSys.getId());
        }
        // pre-processing
        String resourceId = mSys.getResourceId();
        Resource res = resourceService.findResourceDtoById(resourceId, null);
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES, res);

        bindingMap.put("operation", "DELETE");
        bindingMap.put("sysId", identity.getManagedSysId());
        bindingMap.put("group", pGroup);
        bindingMap.put("identity", identity.getIdentity());
        bindingMap.put(AbstractProvisioningService.IDENTITY, identity);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, identity.getIdentity());
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, AbstractProvisioningService.IDENTITY_EXIST);
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, resourceId);

        ResourceProp preProcessProp = res.getResourceProperty("GROUP_PRE_PROCESS");
        String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
        if (StringUtils.isNotBlank(preProcessScript)) {

            PreProcessor<ProvisionGroup> ppScript = createPreProcessScript(preProcessScript, bindingMap);
            if (ppScript != null) {
                executePreProcess(ppScript, bindingMap, pGroup, null, "DELETE");
            }

        }

        boolean connectorSuccess = false;
        ResponseType resp = requestDelete(identity, requestId, mSys, matchObj);

        if (resp.getStatus() == StatusCodeType.SUCCESS) {
            connectorSuccess = true;
            // if REMOVE status: we do physically delete identity for
            // selected managed system after successful provisioning
            // if DELETE status: we don't delete identity from database only
            // set status to INACTIVE
            if (status == UserStatusEnum.REMOVE) {
                identityService.deleteIdentity(identity.getId());
            } else {
                identity.setStatus(LoginStatusEnum.INACTIVE);
                identityService.updateIdentity(identity);
            }
        } else {
            identity.setStatus(LoginStatusEnum.INACTIVE);
            identityService.updateIdentity(identity);
        }

        //   bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        ResourceProp postProcessScript = res.getResourceProperty("GROUP_POST_PROCESS");
        String postProcessScriptVal = postProcessScript != null ? postProcessScript.getValue() : null;
        if (postProcessScriptVal != null && !postProcessScriptVal.isEmpty()) {
            PostProcessor<ProvisionGroup> ppScript = createPostProcessScript(postProcessScriptVal, bindingMap);
            if (ppScript != null) {
                executePostProcess(ppScript, bindingMap, pGroup, null, "DELETE", connectorSuccess);
            }
        }
    }

    protected ObjectResponse requestDelete(
            IdentityDto identityDto,
            String requestId,
            ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj) {
        ObjectResponse resp = new ObjectResponse();

        if (mSys.getSkipGroupProvision()) {
            resp.setStatus(StatusCodeType.FAILURE);
            resp.setError(ErrorCode.SKIP_PROVISIONING);
            if(log.isDebugEnabled()) {
            	log.debug("GroupProvision:requestDelete skipped: SkipGroupProvision flag TRUE");
            }
            return resp;
        }
        CrudRequest<ExtensibleGroup> request = new CrudRequest<>();
        request.setExtensibleObject(new ExtensibleGroup());
        final String identity = StringUtils.isNotEmpty(identityDto.getOrigPrincipalName())
                ? identityDto.getOrigPrincipalName()
                : identityDto.getIdentity();
        request.setObjectIdentity(identity);

        request.setRequestID(requestId);
        request.setTargetID(identityDto.getManagedSysId());
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

        resp = connectorAdapter.deleteRequest(mSys, request);

        return resp;
    }

    public LookupObjectResponse getTargetSystemObject(final String principalName,
                                                      final String managedSysId,
                                                  final List<ExtensibleAttribute> extensibleAttributes) {
        final IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_LOOKUP.value());

        if(log.isDebugEnabled()) {
        	log.debug("getTargetSystemUser called. for = " + principalName);
        }

        LookupObjectResponse response = new LookupObjectResponse(ResponseStatus.SUCCESS);
        try {
            response.setManagedSysId(managedSysId);
            response.setPrincipalName(principalName);
            // get the connector for the managedSystem

            ManagedSysDto mSys = managedSystemService.getManagedSys(managedSysId);
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] objList = managedSystemService.managedSysObjectParamDTO(managedSysId,
                    ManagedSystemObjectMatch.GROUP);
            if (objList.length > 0) {
                matchObj = objList[0];
            }

            // do the lookup
            if(log.isDebugEnabled()) {
            	log.debug("Calling lookupRequest ");
            }

            LookupRequest<ExtensibleGroup> reqType = new LookupRequest<>();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);
            reqType.setSearchValue(principalName);

            ExtensibleGroup extensibleGroup = new ExtensibleGroup();
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getKeyField())) {
                extensibleGroup.setPrincipalFieldName(matchObj.getKeyField());
            }
            extensibleGroup.setPrincipalFieldDataType("string");
            extensibleGroup.setAttributes(extensibleAttributes);
            reqType.setExtensibleObject(extensibleGroup);
            reqType.setTargetID(managedSysId);
            reqType.setHostLoginId(mSys.getUserId());
            if (matchObj != null && StringUtils.isNotEmpty(matchObj.getSearchBaseDn())) {
                reqType.setBaseDN(matchObj.getSearchBaseDn());
            }
            String passwordDecoded;
            try {
                passwordDecoded = getDecryptedPassword(mSys);
            } catch (ConnectorDataException e) {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.FAIL_ENCRYPTION);
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_ENCRYPTION);
                return response;
            }
            reqType.setHostLoginPassword(passwordDecoded);
            reqType.setHostUrl(mSys.getHostUrl());
            reqType.setScriptHandler(mSys.getLookupHandler());

            SearchResponse responseType = connectorAdapter.lookupRequest(mSys, reqType);
            if (responseType.getStatus() == StatusCodeType.FAILURE || responseType.getObjectList().size() == 0) {
                response.setStatus(ResponseStatus.FAILURE);
                return response;
            }

            String targetPrincipalName = responseType.getObjectList().get(0).getObjectIdentity() != null
                    ? responseType.getObjectList().get(0).getObjectIdentity()
                    : parseGroupPrincipal(responseType.getObjectList().get(0).getAttributeList());
            response.setPrincipalName(targetPrincipalName);
            response.setAttrList(responseType.getObjectList().get(0).getAttributeList());
            response.setResponseValue(responseType.getObjectList().get(0));

            idmAuditLog.succeed();

        } finally {
            auditLogService.enqueue(idmAuditLog);
        }

        return response;
    }

    protected String parseGroupPrincipal(List<ExtensibleAttribute> extensibleAttributes) {
        ManagedSysDto defaultManagedSys = managedSystemService.getManagedSys(sysConfiguration.getDefaultManagedSysId());
        List<AttributeMap> policyAttrMap = managedSystemService.getResourceAttributeMapsDTO(defaultManagedSys.getResourceId());
        String principalAttributeName = null;
        for (AttributeMap attr : policyAttrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                    if (attr.getName().equalsIgnoreCase("GROUP_PRINCIPAL")) {
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

    @Override
    public Response remove(String groupId, String requesterId) {
        return delete(sysConfiguration.getDefaultManagedSysId(), groupId, UserStatusEnum.REMOVE, requesterId);
    }

    @Override
    public Response deprovisionSelectedResources(String groupId, String requesterId, List<String> resourceList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

	@Override
	public Response addResourceToGroup(final ProvisionGroup pGroup, String resourceId) {
		final Resource res = resourceService.getResourceDTO(resourceId);
		res.setOperation(AttributeOperationEnum.ADD);
		final Response response = new Response();
		try {
			if(res != null) {
	            if (!pGroup.getNotProvisioninResourcesIds().contains(res.getId())) {
	            	final ManagedSysDto managedSys = managedSystemService.getManagedSysDtoByResource(res.getId());
		            if (managedSys != null) {
		                if (!managedSys.getSkipGroupProvision()) {
			                final String managedSysId = managedSys.getId();
			                // do check if provisioning user has source resource
			                // => we should skip it from double provisioning
			                // reconciliation case
			                if(!StringUtils.equals(managedSysId, pGroup.getSrcSystemId())) {
				                final IdentityDto groupTargetIdentity = identityService.getIdentityByManagedSys(pGroup.getId(), managedSysId);
				                if (groupTargetIdentity != null && res.getOperation() == AttributeOperationEnum.ADD
				                        && groupTargetIdentity.getStatus() == LoginStatusEnum.INACTIVE) {
				                    groupTargetIdentity.setStatus(LoginStatusEnum.ACTIVE);
				                }
				                final Response provIdentityResponse = provisioningIdentity(groupTargetIdentity, pGroup, managedSys, res, true);
				                if (!provIdentityResponse.isSuccess()) {
				                    return provIdentityResponse;
				                }
				                //Provisioning Members
				                if (pGroup.getUpdateManagedSystemMembers().contains(managedSysId)) {
				                	final UserSearchBean sb = new UserSearchBean();
				                	sb.addGroupId(pGroup.getId());
				                    List<UserEntity> members = userDataService.findBeans(sb, 0, Integer.MAX_VALUE);
				                    for (UserEntity member : members) {
				                        if (!isMemberAvailableInResource(member, res.getId())) {
				                            User user = userDozerConverter.convertToDTO(member, true);
				                            provisionService.modifyUser(new ProvisionUser(user));
				                        }
				                    }
				                }
				
				            }  else {
				                // TODO WARNING
				            }
		                }
		            }
		        }
			}
		} catch(BasicDataServiceException e) {
			response.fail();
			response.setErrorCode(e.getCode());
			log.error("Can't perform operation", e);
		}
		response.succeed();
		return response;
	}

}
