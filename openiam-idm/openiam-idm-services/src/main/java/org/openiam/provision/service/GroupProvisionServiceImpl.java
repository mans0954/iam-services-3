package org.openiam.provision.service;


import groovy.lang.MissingPropertyException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupObjectResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("groupProvision")
@WebService(endpointInterface = "org.openiam.provision.service.ObjectProvisionService",
        targetNamespace = "http://www.openiam.org/service/provision",
        portName = "GroupProvisionControllerServicePort",
        serviceName = "GroupProvisionService")
public class GroupProvisionServiceImpl extends AbstractBaseService implements ObjectProvisionService<ProvisionGroup> {
    @Autowired
    protected ManagedSystemWebService managedSysService;

    @Autowired
    protected ValidateConnectionConfig validateConnectionConfig;

    @Autowired
    private UserDozerConverter userDozerConverter;

    protected static final Log log = LogFactory.getLog(GroupProvisionServiceImpl.class);

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Value(",${org.openiam.debug.hidden.attributes},")
    private String hiddenAttributes;

    @Autowired
    protected String preProcessorGroup;

    @Autowired
    protected String postProcessorGroup;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private ManagedSystemWebService managedSystemService;

    @Autowired
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    private GroupDataWebService groupDataWebService;

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
    protected ResourceDataService resourceDataService;

    @Autowired
    protected UserDataService userDataService;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provisionService;

    @Override
    public Response add(@WebParam(name = "group", targetNamespace = "") final ProvisionGroup group) throws Exception {
        return provisioning(group, true);
    }

    @Override
    public Response modify(@WebParam(name = "group", targetNamespace = "") ProvisionGroup group) {
        return provisioning(group, false);
    }

    private Response provisioning(final ProvisionGroup group, final boolean isAdd) {
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE);
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            response = transactionTemplate.execute(new TransactionCallback<Response>() {
                @Override
                public Response doInTransaction(TransactionStatus status) {
                    // bind the objects to the scripting engine
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                    bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());

                    bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
                    bindingMap.put(AbstractProvisioningService.GROUP, group);
                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
                    bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);

                    Response tmpRes = new Response();
                    if (callPreProcessor(isAdd ? "ADD" : "MODIFY", group, bindingMap) != ProvisioningConstants.SUCCESS) {
                        tmpRes.setStatus(ResponseStatus.FAILURE);
                        tmpRes.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                        return tmpRes;
                    }
                    Set<Resource> resources = group.getResources();
                    if (resources != null) {
                        for(Resource res : resources) {
                            if (group.getNotProvisioninResourcesIds().contains(res.getId())) {
                                continue;
                            }
                            ManagedSysDto managedSys = managedSystemService.getManagedSysByResource(res.getId());


                        if (managedSys != null) {
                            String managedSysId = managedSys.getId();
                            // do check if provisioning user has source resource
                            // => we should skip it from double provisioning
                            // reconciliation case
                            if (group.getSrcSystemId() != null && managedSysId.equals(group.getSrcSystemId())) {
                                continue;
                            }
                            IdentityDto groupTargetSysIdentity = identityService.getIdentity(group.getId(), managedSysId);
                            if(groupTargetSysIdentity == null) {
                                List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getId());
                                bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
                                bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
                                bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
                                bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, null);
                                bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, res.getId());
                                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
                                bindingMap.put(AbstractProvisioningService.GROUP, group);
                                ManagedSystemObjectMatch matchObj = null;
                                ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.GROUP);
                                if (matchObjAry != null && matchObjAry.length > 0) {
                                    matchObj = matchObjAry[0];
                                    bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
                                }
                                try {
                                    log.debug(" - Building principal Name for: " + managedSysId);
                                    String newIdentity = ProvisionServiceUtil.buildGroupPrincipalName(attrMap, scriptRunner, bindingMap);

									if (StringUtils.isBlank(newIdentity)) {
										log.debug("Primary identity not found...");
										tmpRes.setStatus(ResponseStatus.FAILURE);
										tmpRes.setErrorCode(ResponseCode.IDENTITY_NOT_FOUND);
										return tmpRes;
									}

									groupTargetSysIdentity = new IdentityDto(IdentityTypeEnum.GROUP);
                                    groupTargetSysIdentity.setIdentity(newIdentity);
                                    groupTargetSysIdentity.setCreateDate(new Date());
                                    groupTargetSysIdentity.setCreatedBy(systemUserId);
                                    groupTargetSysIdentity.setManagedSysId(managedSysId);
                                    groupTargetSysIdentity.setReferredObjectId(group.getId());
                                    groupTargetSysIdentity.setStatus(LoginStatusEnum.ACTIVE);

                                    String groupTargetSysIdentityId = identityService.save(groupTargetSysIdentity);

                                    groupTargetSysIdentity.setId(groupTargetSysIdentityId);
                                } catch (ScriptEngineException e) {
                                    e.printStackTrace();
                                }
                            }

                            //Provisioning Members
                            List<UserEntity> members = userDataService.getUsersForGroup(group.getId(), systemUserId, 0, Integer.MAX_VALUE);
                            for(UserEntity member : members) {
                                if(! isMemberAvailableInResource(member, res.getId())) {
                                    User user = userDozerConverter.convertToDTO(member, true);
                                    user.addResource(res);
                                    provisionService.modifyUser(new ProvisionUser(user));
                                }
                            }

                            // bind the objects to the scripting engine
                            bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
                            bindingMap.put("sysId", managedSysId);
                            bindingMap.put("managedSysId", managedSysId);
                            bindingMap.put("group", group);
                            bindingMap.put("identity", groupTargetSysIdentity);
                            bindingMap.put("requesterId", systemUserId);

                            List<AttributeMap> attrMapEntities = managedSystemService
                                    .getAttributeMapsByManagedSysId(managedSysId);

                            ManagedSystemObjectMatch matchObj = null;
                            ManagedSystemObjectMatch[] objList = managedSystemService.managedSysObjectParam(managedSysId,
                                    ManagedSystemObjectMatch.GROUP);
                            if (objList.length > 0) {
                                matchObj = objList[0];
                            }

                            ExtensibleObject extObj = buildFromRules(attrMapEntities, bindingMap);

                            // get the attributes at the target system
                            // this lookup only for getting attributes from the
                            // system
                            String requestId = "R" + UUIDGen.getUUID();
                            Map<String, String> currentValueMap = new HashMap<String, String>();

                            boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(requestId, groupTargetSysIdentity, extObj, managedSys,
                                    matchObj, currentValueMap);

                            boolean connectorSuccess = false;

                            // pre-processing
                            bindingMap.put("targetSystemAttributes", currentValueMap);

                            ResourceProp preProcessProp = res.getResourceProperty("GROUP_PRE_PROCESS");
                            String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
                            if (StringUtils.isNotBlank(preProcessScript)) {

                                PreProcessor<ProvisionGroup> ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                if (ppScript != null) {
                                    int executePreProcessResult = executePreProcess(ppScript, bindingMap, group,
                                            isExistedInTargetSystem ? "MODIFY" : "ADD");
                                    if (executePreProcessResult == ProvisioningConstants.FAIL) {
                                        tmpRes.setStatus(ResponseStatus.FAILURE);
                                        tmpRes.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                                        return tmpRes;
                                    }
                                }

                            }


                           if (!isExistedInTargetSystem) {

                                connectorSuccess = requestAddModify(groupTargetSysIdentity, requestId, managedSys, matchObj, extObj, true);

                            } else { // if user exists in target system

                                // updates the attributes with the correct operation codes
                                extObj = DefaultProvisioningService.updateAttributeList(extObj, currentValueMap);

                                if (groupTargetSysIdentity.getOperation() == AttributeOperationEnum.REPLACE
                                        && groupTargetSysIdentity.getOrigPrincipalName() != null) {
                                    extObj.getAttributes().add(
                                            new ExtensibleAttribute("ORIG_IDENTITY", groupTargetSysIdentity.getOrigPrincipalName(),
                                                    AttributeOperationEnum.REPLACE.getValue(), "String"));
                                }
                                connectorSuccess = requestAddModify(groupTargetSysIdentity, requestId, managedSys, matchObj, extObj, false);
                            }


                            // post processing
                            ResourceProp postProcessProp = res.getResourceProperty("GROUP_POST_PROCESS");
                            String postProcessScript = postProcessProp != null ? postProcessProp.getValue() : null;
                            if (StringUtils.isNotBlank(postProcessScript)) {
                                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                if (ppScript != null) {
                                    int executePostProcessResult = executePostProcess(ppScript, bindingMap, group,
                                            isExistedInTargetSystem ? "MODIFY" : "ADD", connectorSuccess);

                                    if (executePostProcessResult == ProvisioningConstants.FAIL) {
                                        tmpRes.setStatus(ResponseStatus.FAILURE);
                                        tmpRes.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                                        return tmpRes;
                                    }
                                }
                            }

                            if (connectorSuccess) {
                                tmpRes.setStatus(ResponseStatus.SUCCESS);
                                tmpRes.setErrorCode(ResponseCode.SUCCESS);
                                return tmpRes;
                            }

                            tmpRes.setStatus(ResponseStatus.FAILURE);
                            tmpRes.setErrorCode(ResponseCode.FAIL_CONNECTOR);





                        }  else {
                            // TODO WARNING
                        }}
                    }
                    // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT

                    int callPostProcessorResult = callPostProcessor(isAdd ? "ADD" : "MODIFY", group, bindingMap);
               //     auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "callPostProcessor result="
               //             + (callPostProcessorResult == 1 ? "SUCCESS" : "FAIL"));
                    if (callPostProcessorResult != ProvisioningConstants.SUCCESS) {
                        tmpRes.setStatus(ResponseStatus.FAILURE);
                        tmpRes.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
               //         auditLog.addAttribute(AuditAttributeName.DESCRIPTION, "PostProcessor error.");
                        return tmpRes;
                    }
                    tmpRes.setStatus(ResponseStatus.SUCCESS);
                    return tmpRes;
                }
            });

        } catch(Throwable t){
            t.printStackTrace();
        }
        return response;
    }


    private boolean isMemberAvailableInResource(final UserEntity member, final String resourceId) {
        boolean result = false;
        for(ResourceEntity res : member.getResources()) {
            if(res.getId().equalsIgnoreCase(resourceId)) {
                return true;
            }
        }
        for(RoleEntity re : member.getRoles()) {
           for(ResourceEntity res : re.getResources()) {
               if(res.getId().equalsIgnoreCase(resourceId)) {
                   return true;
               }
           }
        }
        return result;
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
                                    Map<String, Object> bindingMap, ProvisionGroup group, String operation) {
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
            return ppScript.setPassword(bindingMap);
        }

        return 0;
    }

    protected int executePostProcess(PostProcessor<ProvisionGroup> ppScript,
                                     Map<String, Object> bindingMap, ProvisionGroup group, String operation, boolean success) {
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
            return ppScript.setPassword(bindingMap, success);
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
    private boolean requestAddModify(IdentityDto identityDto, String requestId, ManagedSysDto mSys,
                                     ManagedSystemObjectMatch matchObj, ExtensibleObject extensibleObject, boolean isAdd) {

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

        ObjectResponse resp = isAdd ? connectorAdapter.addRequest(mSys, userReq)
                : connectorAdapter.modifyRequest(mSys, userReq);
        /*auditBuilderDispatcherChild.addAttribute(AuditAttributeName.DESCRIPTION, (isAdd ? "ADD IDENTITY = "
                : "MODIFY IDENTITY = ") + resp.getStatus() + " details:" + resp.getErrorMsgAsStr());*/
        return resp.getStatus() != StatusCodeType.FAILURE;
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
                                                   ManagedSysDto mSys, ManagedSystemObjectMatch matchObj, Map<String, String> curValueMap) {

        String identity = identityDto.getIdentity();
        log.debug("Getting the current attributes in the target system for =" + identity);

        log.debug("- IsRename: " + identityDto.getOrigPrincipalName());

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

        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType);
        if (lookupSearchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ExtensibleAttribute> extAttrList = lookupSearchResponse.getObjectList().size() > 0 ? lookupSearchResponse
                    .getObjectList().get(0).getAttributeList()
                    : new LinkedList<ExtensibleAttribute>();

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


    private ExtensibleObject buildFromRules(List<AttributeMap> attrMap,
                                          Map<String, Object> bindingMap) {

        ExtensibleGroup extensibleObject = new ExtensibleGroup();

        if (attrMap != null) {

            log.debug("buildFromRules: attrMap IS NOT null");

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }



                String objectType = attr.getMapForObjectType();
                if (objectType != null) {

                    if (objectType.equalsIgnoreCase("GROUP")) {
                        Object output = "";
                        try {
                            output = ProvisionServiceUtil.getOutputFromAttrMap(attr, bindingMap, scriptRunner);
                        } catch (ScriptEngineException see) {
                            log.error("Error in script = '", see);
                            continue;
                        } catch (MissingPropertyException mpe) {
                            log.error("Error in script = '", mpe);
                            continue;
                        }

                        log.debug("buildFromRules: OBJECTTYPE="+objectType+", ATTRIBUTE=" + attr.getAttributeName() +
                                ", SCRIPT OUTPUT=" +
                                (hiddenAttributes.toLowerCase().contains(","+attr.getAttributeName().toLowerCase()+",")
                                        ? "******" : output));

                        if (output != null) {
                            ExtensibleAttribute newAttr;
                            if (output instanceof String) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), (String) output, 1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        ((Integer) output).toString(), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), 1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extensibleObject.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extensibleObject.getAttributes().add(
                                        new ExtensibleAttribute(attr.getAttributeName(), (byte[]) output, 1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extensibleObject.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), (List) output, 1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extensibleObject.getAttributes().add(newAttr);

                                log.debug("buildFromRules: added attribute to extGroup:" + attr.getAttributeName());
                            }
                        }
                    } else if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(objectType)) {

                        extensibleObject.setPrincipalFieldName(attr.getAttributeName());
                        extensibleObject.setPrincipalFieldDataType(attr.getDataType().getValue());

                    }
                }
            }
        }

        return extensibleObject;
    }


    @Override
    public Response delete(@WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId, @WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "status", targetNamespace = "") UserStatusEnum status, @WebParam(name = "requesterId", targetNamespace = "") String requesterId) {

        log.debug("----deleteGroup called.------");

        Response response = new Response(ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        if (status != UserStatusEnum.DELETED && status != UserStatusEnum.REMOVE && status != UserStatusEnum.LEAVE
                && status != UserStatusEnum.TERMINATED && status != UserStatusEnum.RETIRED) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            return response;
        }

        String requestId = "R" + UUIDGen.getUUID();

        // get the user object associated with this principal
        IdentityDto identityDto = identityService.getIdentity(groupId, managedSystemId);
        if (identityDto == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.PRINCIPAL_NOT_FOUND);
            return response;
        }
        // check if the user active
        if (groupId == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }

        Group groupDto = groupDataWebService.getGroup(groupId, requestId);

        ProvisionGroup pGroup = new ProvisionGroup(groupDto);
        // SET PRE ATTRIBUTES FOR DEFAULT SYS SCRIPT
        bindingMap.put("operation", "DELETE");
        bindingMap.put("sysId", identityDto.getManagedSysId());
        bindingMap.put("group", pGroup);
        bindingMap.put("identity", identityDto.getIdentity());

        if (callPreProcessor("DELETE", pGroup, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }

        if (status != UserStatusEnum.REMOVE
                && (UserStatusEnum.DELETED.getValue().equalsIgnoreCase(pGroup.getStatus()) || UserStatusEnum.TERMINATED.getValue().equalsIgnoreCase(pGroup.getStatus()))) {
            log.debug("User was already deleted. Nothing more to do.");
            return response;
        }

        if (!managedSystemId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {
            // managedSysId point to one of the seconardary identities- just
            // terminate that identity

            // call delete on the connector
            ManagedSysDto mSys = managedSystemService.getManagedSys(managedSystemId);

            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParam(mSys.getId(), ManagedSystemObjectMatch.GROUP);
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }
            // pre-processing


            String resourceId = mSys.getResourceId();
            Resource res = resourceDataService.getResource(resourceId, null);

            bindingMap.put("IDENTITY", identityDto.getIdentity());
            bindingMap.put("RESOURCE", res);
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, identityDto.getIdentity());
            bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, AbstractProvisioningService.IDENTITY_EXIST);
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, resourceId);

            ResourceProp preProcessProp = res.getResourceProperty("GROUP_PRE_PROCESS");
            String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
            if (StringUtils.isNotBlank(preProcessScript)) {

                PreProcessor<ProvisionGroup> ppScript = createPreProcessScript(preProcessScript, bindingMap);
                if (ppScript != null) {
                    executePreProcess(ppScript, bindingMap, pGroup, "DELETE");
                }

            }

            boolean connectorSuccess = false;
            ResponseType resp = delete(identityDto, requestId, mSys, matchObj);

            if (resp.getStatus() == StatusCodeType.SUCCESS) {
                connectorSuccess = true;
                // if REMOVE status: we do physically delete identity for
                // selected managed system after successful provisioning
                // if DELETE status: we don't delete identity from database only
                // set status to INACTIVE
                if (status == UserStatusEnum.REMOVE) {
                    identityService.deleteIdentity(identityDto.getId());
                } else {
                    identityDto.setStatus(LoginStatusEnum.INACTIVE);
                    identityService.updateIdentity(identityDto);
                }
            } else {
                identityDto.setStatus(LoginStatusEnum.INACTIVE);
                identityService.updateIdentity(identityDto);
            }

         //   bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
            ResourceProp postProcessScript = res.getResourceProperty("GROUP_POST_PROCESS");
            String postProcessScriptVal = postProcessScript != null ? postProcessScript.getValue() : null;
            if (postProcessScriptVal != null && !postProcessScriptVal.isEmpty()) {
                PostProcessor<ProvisionGroup> ppScript = createPostProcessScript(postProcessScriptVal, bindingMap);
                if (ppScript != null) {
                    executePostProcess(ppScript, bindingMap, pGroup, "DELETE", connectorSuccess);
                }
            }

        } else {
            // update the identities and set them to inactive
            List<IdentityDto> principalList = identityService.getIdentities(groupId);
            if (principalList != null) {
                for (IdentityDto l : principalList) {
                    // this try-catch block for protection other operations and
                    // other resources if one resource was fall with error
                    try {
                        if (!LoginStatusEnum.INACTIVE.equals(l.getStatus())) {
                            // only add the connectors if its a secondary
                            // identity.
                            if (!l.getManagedSysId().equalsIgnoreCase(this.sysConfiguration.getDefaultManagedSysId())) {

                                ManagedSysDto mSys = managedSystemService.getManagedSys(l.getManagedSysId());

                                ManagedSystemObjectMatch matchObj = null;
                                ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParam(
                                        mSys.getId(), "GROUP");
                                if (matchObjAry != null && matchObjAry.length > 0) {
                                    matchObj = matchObjAry[0];
                                    bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
                                }
                                log.debug("Deleting id=" + l.getIdentity());
                                log.debug("- delete using managed sys id=" + mSys.getId());

                                // pre-processing
                                bindingMap.put(AbstractProvisioningService.IDENTITY, l);
                                bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES, null);

                                Resource resource = null;
                                String resourceId = mSys.getResourceId();

                                // SET PRE ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, l.getIdentity());
                                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
                                bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, resourceId);
                                bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, AbstractProvisioningService.IDENTITY_EXIST);

                                if (resourceId != null) {
                                    resource = resourceDataService.getResource(resourceId, null);
                                    if (resource != null) {
                                        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES, resource);
                                        ResourceProp preProcessProp = resource.getResourceProperty("GROUP_PRE_PROCESS");
                                        String preProcessScript = preProcessProp != null ? preProcessProp.getValue() : null;
                                        if (StringUtils.isNotBlank(preProcessScript)) {
                                            PreProcessor<ProvisionGroup> ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                            if (ppScript != null) {
                                                executePreProcess(ppScript, bindingMap, pGroup, "DELETE");
                                            }
                                        }
                                    }
                                }

                                boolean connectorSuccess = false;

                                ObjectResponse resp = delete(l, requestId,
                                        mSys, matchObj);
                                if (resp.getStatus() == StatusCodeType.SUCCESS) {
                                    connectorSuccess = true;
                                }

                                if (connectorSuccess) {
                                    l.setStatus(LoginStatusEnum.INACTIVE);
                                }
                                // SET POST ATTRIBUTES FOR TARGET SYS SCRIPT
                                bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, null);
                                if (resource != null) {
                                    ResourceProp postProcessScript = resource.getResourceProperty("GROUP_POST_PROCESS");
                                    String postProcessScriptVal = postProcessScript != null ? postProcessScript.getValue() : null;
                                    if (postProcessScriptVal != null && !postProcessScriptVal.isEmpty()) {
                                        PostProcessor<ProvisionGroup> ppScript = createPostProcessScript(postProcessScriptVal, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript, bindingMap, pGroup, "DELETE", connectorSuccess);
                                        }
                                    }
                                }
                                if (status == UserStatusEnum.REMOVE) {
                                    identityService.deleteIdentity(identityDto.getIdentity());
                                }
                            }

                        }
                    } catch (Throwable tw) {
                        log.error(l, tw);
                        tw.printStackTrace();
                        response.setStatus(ResponseStatus.FAILURE);
                        response.setErrorCode(ResponseCode.INTERNAL_ERROR);
                        return response;
                    }
                }
            }
        }
        if (UserStatusEnum.REMOVE.getValue().equalsIgnoreCase(status.getValue())) {

                identityService.deleteIdentity(identityDto.getId());
            try {
                groupDataWebService.deleteGroup(groupId,requesterId);
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
            groupDataWebService.saveGroup(groupDto,requestId);
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

    protected ObjectResponse delete(
            IdentityDto identityDto,
            String requestId,
            ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj) {

        CrudRequest<ExtensibleGroup> request = new CrudRequest<ExtensibleGroup>();
        ExtensibleGroup extensibleGroup =  new ExtensibleGroup();
        request.setExtensibleObject(extensibleGroup);
        request.setObjectIdentity(identityDto.getIdentity());
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

        ObjectResponse resp = connectorAdapter.deleteRequest(mSys, request);

        return resp;
    }

    public LookupObjectResponse getTargetSystemObject(final String principalName,
                                                      final String managedSysId,
                                                  final List<ExtensibleAttribute> extensibleAttributes) {
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.PROVISIONING_LOOKUP.value());

        log.debug("getTargetSystemUser called. for = " + principalName);

        LookupObjectResponse response = new LookupObjectResponse(ResponseStatus.SUCCESS);
        try {
            response.setManagedSysId(managedSysId);
            response.setPrincipalName(principalName);
            // get the connector for the managedSystem

            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);
            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] objList = managedSystemService.managedSysObjectParam(managedSysId,
                    ManagedSystemObjectMatch.GROUP);
            if (objList.length > 0) {
                matchObj = objList[0];
            }

            // do the lookup

            log.debug("Calling lookupRequest ");

            LookupRequest<ExtensibleUser> reqType = new LookupRequest<>();
            String requestId = "R" + UUIDGen.getUUID();
            reqType.setRequestID(requestId);
            reqType.setSearchValue(principalName);

            ExtensibleUser extensibleUser = new ExtensibleUser();
            extensibleUser.setPrincipalFieldName(matchObj.getKeyField());
            extensibleUser.setPrincipalFieldDataType("string");
            extensibleUser.setAttributes(extensibleAttributes);
            reqType.setExtensibleObject(extensibleUser);
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
        List<AttributeMap> policyAttrMap = managedSystemService.getResourceAttributeMaps(defaultManagedSys.getResourceId());
        String principalAttributeName = null;
        for (AttributeMap attr : policyAttrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                    if (attr.getAttributeName().equalsIgnoreCase("GROUP_PRINCIPAL")) {
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

    @Override
    public Response remove(@WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "requesterId", targetNamespace = "") String requesterId) {
        return delete(sysConfiguration.getDefaultManagedSysId(), groupId, UserStatusEnum.REMOVE, requesterId);
    }

    @Override
    public Response deprovisionSelectedResources(@WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "requesterId", targetNamespace = "") String requesterId, @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
