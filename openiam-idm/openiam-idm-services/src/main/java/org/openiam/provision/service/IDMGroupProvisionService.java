package org.openiam.provision.service;


import groovy.lang.MissingPropertyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
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
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
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
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.resp.ProvisionGroupResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.*;

@WebService(endpointInterface = "org.openiam.provision.service.GroupProvisionService", targetNamespace = "http://www.openiam.org/service/provision", portName = "IDMObjectProvisionControllerServicePort", serviceName = "IDMGroupProvisioningService")
@Component("idmGroupProvision")
public class IDMGroupProvisionService extends AbstractBaseService implements GroupProvisionService {

    @Autowired
    protected ValidateConnectionConfig validateConnectionConfig;

    protected static final Log log = LogFactory.getLog(IDMGroupProvisionService.class);

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

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
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private KeyManagementService keyManagementService;

    @Override
    public ProvisionGroupResponse addGroup(@WebParam(name = "group", targetNamespace = "") final ProvisionGroup group) throws Exception {
        return provisioning(group, true);
    }


    @Override
    public ProvisionGroupResponse modifyGroup(@WebParam(name = "group", targetNamespace = "") ProvisionGroup group) {
        return provisioning(group, false);
    }

    private ProvisionGroupResponse provisioning(final ProvisionGroup group, final boolean isAdd) {
        ProvisionGroupResponse response = new ProvisionGroupResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {

            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            response = transactionTemplate.execute(new TransactionCallback<ProvisionGroupResponse>() {
                @Override
                public ProvisionGroupResponse doInTransaction(TransactionStatus status) {

                    ProvisionGroupResponse tmpRes = new ProvisionGroupResponse();
                    // TODO Pre-Processing
                   //int callPreProcessor = callPreProcessor(isAdd ? "ADD" : "MODIFY", group, bindingMap);

                    Set<Resource> resources = group.getResources();
                    for(Resource res : resources) {
                        if(group.getNotProvisioninResourcesIds().contains(res.getId())) {
                            continue;
                        }



                        ManagedSysDto managedSys = managedSystemService.getManagedSysByResource(res.getId());
                        if (managedSys != null) {
                            String managedSysId = managedSys.getId();
                            IdentityDto groupTargetSysIdentity = identityService.getIdentity(group.getId(), managedSysId);
                            if(groupTargetSysIdentity == null) {
                                //TODO: create by policy map PRONCIPAL field
                                // stub: temporary use the same as Group Name
                                String identity = group.getName();

                                groupTargetSysIdentity = new IdentityDto(IdentityTypeEnum.GROUP);
                                groupTargetSysIdentity.setIdentity(identity);
                                groupTargetSysIdentity.setCreateDate(new Date());
                                groupTargetSysIdentity.setCreatedBy(systemUserId);
                                groupTargetSysIdentity.setManagedSysId(managedSysId);
                                groupTargetSysIdentity.setReferredObjectId(group.getId());
                                groupTargetSysIdentity.setStatus(LoginStatusEnum.PENDING_CREATE);

                                String groupTargetSysIdentityId = identityService.save(groupTargetSysIdentity);
                                groupTargetSysIdentity.setId(groupTargetSysIdentityId);
                            }


                            // bind the objects to the scripting engine
                            Map<String, Object> bindingMap = new HashMap<String, Object>();
                            bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
                            bindingMap.put("sysId", managedSysId);
                            bindingMap.put("group", group);
                            bindingMap.put("identity", groupTargetSysIdentity);

                            List<AttributeMap> attrMapEntities = managedSystemService
                                    .getAttributeMapsByManagedSysId(managedSysId);

                            ManagedSystemObjectMatch matchObj = null;
                            ManagedSystemObjectMatch[] objList = managedSystemService.managedSysObjectParam(managedSysId,
                                    "GROUP");
                            if (objList.length > 0) {
                                matchObj = objList[0];
                            }

                            ExtensibleObject extObj = buildFromRules(attrMapEntities, bindingMap);
                            extObj.setPrincipalFieldName(groupTargetSysIdentity.getIdentity());
                            extObj.setPrincipalFieldDataType(groupTargetSysIdentity.getIdentity());

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

                            ResourceProp preProcessProp = res.getResourceProperty("PRE_PROCESS");
                            //TODO enable pre processor
                            String preProcessScript = preProcessProp != null ? preProcessProp.getPropValue() : null;
                       /*     if (StringUtils.isNotBlank(preProcessScript)) {

                                PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                if (ppScript != null) {
                                    int executePreProcessResult = executePreProcess(ppScript, bindingMap, group,
                                            isExistedInTargetSystem ? "MODIFY" : "ADD");
                                    if (executePreProcessResult == ProvisioningConstants.FAIL) {
                                        response.setStatus(ResponseStatus.FAILURE);
                                        response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
                                        return response;
                                    }
                                }

                            }*/


                           if (!isExistedInTargetSystem) {

                                connectorSuccess = requestAddModify(groupTargetSysIdentity, requestId, managedSys, matchObj, extObj, true, null);

                            } else { // if user exists in target system

                                // updates the attributes with the correct operation codes
                                extObj = DefaultProvisioningService.updateAttributeList(extObj, currentValueMap);

                                if (groupTargetSysIdentity.getOperation() == AttributeOperationEnum.REPLACE
                                        && groupTargetSysIdentity.getOrigPrincipalName() != null) {
                                    extObj.getAttributes().add(
                                            new ExtensibleAttribute("ORIG_IDENTITY", groupTargetSysIdentity.getOrigPrincipalName(),
                                                    AttributeOperationEnum.REPLACE.getValue(), "String"));
                                }
                                connectorSuccess = requestAddModify(groupTargetSysIdentity, requestId, managedSys, matchObj, extObj, false, null);
                            }


                            // post processing
                            ResourceProp postProcessProp = res.getResourceProperty("POST_PROCESS");
                            String postProcessScript = postProcessProp != null ? postProcessProp.getPropValue() : null;
                          /*  if (StringUtils.isNotBlank(postProcessScript)) {
                                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                if (ppScript != null) {
                                    int executePostProcessResult = executePostProcess(ppScript, bindingMap, group,
                                            isExistedInTargetSystem ? "MODIFY" : "ADD", connectorSuccess);

                                    if (executePostProcessResult == ProvisioningConstants.FAIL) {
                                        response.setStatus(ResponseStatus.FAILURE);
                                        response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
                                        return response;
                                    }
                                }
                            }*/

                            if (connectorSuccess) {
                                tmpRes.setStatus(ResponseStatus.SUCCESS);
                                tmpRes.setErrorCode(ResponseCode.SUCCESS);
                                return tmpRes;
                            }

                            tmpRes.setStatus(ResponseStatus.FAILURE);
                            tmpRes.setErrorCode(ResponseCode.FAIL_CONNECTOR);





                        }  else {
                            // TODO WARNING
                        }
                    }
                    //TODO Post-Processing

                    tmpRes.setStatus(ResponseStatus.SUCCESS);
                    return tmpRes;
                }
            });

        } catch(Throwable t){
            t.printStackTrace();
        }
        return response;
    }



    private boolean requestAddModify(IdentityDto identityDto, String requestId, ManagedSysDto mSys,
                                     ManagedSystemObjectMatch matchObj, ExtensibleObject extensibleObject, boolean isAdd,
                                     final AuditLogBuilder auditBuilderDispatcherChild) {

        CrudRequest<ExtensibleObject> userReq = new CrudRequest<ExtensibleObject>();
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

        ObjectResponse resp = isAdd ? connectorAdapter.addRequest(mSys, userReq, MuleContextProvider.getCtx())
                : connectorAdapter.modifyRequest(mSys, userReq, MuleContextProvider.getCtx());
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
        MuleContext muleContext = MuleContextProvider.getCtx();
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

        SearchResponse lookupSearchResponse = connectorAdapter.lookupRequest(mSys, reqType, muleContext);
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

        ExtensibleObject extensibleObject = new ExtensibleObject();

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
                        log.debug("buildFromRules: OBJECTTYPE=" + objectType + " SCRIPT OUTPUT=" + output
                                + " attribute name=" + attr.getAttributeName());
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
                    } /*else if (objectType.equalsIgnoreCase("PRINCIPAL")) {

                        extensibleObject.setPrincipalFieldName(attr.getAttributeName());
                        extensibleObject.setPrincipalFieldDataType(attr.getDataType().getValue());

                    }*/
                }
            }
        }

        return extensibleObject;
    }


    @Override
    public ProvisionGroupResponse deleteGroup(@WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId, @WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "status", targetNamespace = "") UserStatusEnum status, @WebParam(name = "requestorId", targetNamespace = "") String requestorId) {

        log.debug("----deleteUser called.------");

        ProvisionGroupResponse response = new ProvisionGroupResponse(ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        if (status != UserStatusEnum.DELETED && status != UserStatusEnum.REMOVE && status != UserStatusEnum.LEAVE
                && status != UserStatusEnum.TERMINATE && status != UserStatusEnum.RETIRED) {
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

      /*  if (callPreProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_PREPROCESSOR);
            return response;
        }
*/
        if (status != UserStatusEnum.REMOVE
                && (UserStatusEnum.DELETED.getValue().equalsIgnoreCase(pGroup.getStatus()) || UserStatusEnum.TERMINATE.getValue().equalsIgnoreCase(pGroup.getStatus()))) {
            log.debug("User was already deleted. Nothing more to do.");
            return response;
        }

        if (!managedSystemId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {
            // managedSysId point to one of the seconardary identities- just
            // terminate that identity

            // call delete on the connector
            ManagedSysDto mSys = managedSystemService.getManagedSys(managedSystemId);

            ManagedSystemObjectMatch matchObj = null;
            ManagedSystemObjectMatch[] matchObjAry = managedSystemService.managedSysObjectParam(mSys.getId(), "GROUP");
            if (matchObjAry != null && matchObjAry.length > 0) {
                matchObj = matchObjAry[0];
                bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
            }
            // pre-processing

            Resource res = null;
            String resourceId = mSys.getResourceId();

            bindingMap.put("IDENTITY", identityDto.getIdentity());
            bindingMap.put("RESOURCE", res);
            /*bindingMap.put(TARGET_SYSTEM_IDENTITY, identityDto.getIdentity());
            bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, IDENTITY_EXIST);
            bindingMap.put(TARGET_SYS_RES_ID, resourceId);
*/
           /* if (resourceId != null) {
                res = resourceDataService.getResource(resourceId);
                if (res != null) {
                    String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                    if (preProcessScript != null && !preProcessScript.isEmpty()) {
                        PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                        if (ppScript != null) {
                            executePreProcess(ppScript, bindingMap, pUser, "DELETE");
                        }
                    }
                }
            }*/

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
                identityDto.setStatus(status == UserStatusEnum.REMOVE ? LoginStatusEnum.FAIL_REMOVE
                        : LoginStatusEnum.FAIL_DELETE);
                identityService.updateIdentity(identityDto);
            }

         //   bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
           /* String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
            if (postProcessScript != null && !postProcessScript.isEmpty()) {
                PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                if (ppScript != null) {
                    executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
                }
            }*/

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
                                        mSys.getId(), "USER");
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

                               /* if (resourceId != null) {
                                    resource = resourceDataService.getResource(resourceId);
                                    if (resource != null) {
                                        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES, resource);

                                        String preProcessScript = getResProperty(resource.getResourceProps(),
                                                "PRE_PROCESS");
                                        if (preProcessScript != null && !preProcessScript.isEmpty()) {
                                            PreProcessor ppScript = createPreProcessScript(preProcessScript, bindingMap);
                                            if (ppScript != null) {
                                                if (executePreProcess(ppScript, bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }
*/
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
                              /*  if (resource != null) {
                                    String postProcessScript = getResProperty(resource.getResourceProps(),
                                            "POST_PROCESS");
                                    if (postProcessScript != null && !postProcessScript.isEmpty()) {
                                        PostProcessor ppScript = createPostProcessScript(postProcessScript, bindingMap);
                                        if (ppScript != null) {
                                            executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
                                        }
                                    }
                                }*/
                                if (status == UserStatusEnum.REMOVE) {
                                    identityService.deleteIdentity(identityDto.getIdentity());
                                }
                            }

                        }
                    } catch (Throwable tw) {
                        log.error(l, tw);
                    }
                }
            }
        }
        if (UserStatusEnum.REMOVE.getValue().equalsIgnoreCase(status.getValue())) {
            identityService.deleteIdentity(identityDto.getId());
            try {
                groupDataWebService.deleteGroup(groupId);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(ResponseStatus.FAILURE);
                response.setErrorCode(ResponseCode.FAIL_SQL_ERROR);
                return response;
            }
        } else {
            groupDto.setStatus(status.getValue());
            groupDto.setLastUpdatedBy(requestorId);
            groupDto.setLastUpdate(new Date(System.currentTimeMillis()));
            groupDataWebService.saveGroup(groupDto,requestId);
        }
        // SET POST ATTRIBUTES FOR DEFAULT SYS SCRIPT

       /* bindingMap.put(TARGET_SYSTEM_IDENTITY, identityDto.getIdentity());
        bindingMap.put(TARGET_SYSTEM_IDENTITY_STATUS, null);
        bindingMap.put(TARGET_SYS_RES_ID, null);

        if (callPostProcessor("DELETE", pUser, bindingMap) != ProvisioningConstants.SUCCESS) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.FAIL_POSTPROCESSOR);
            return response;
        }*/

        response.setStatus(ResponseStatus.SUCCESS);
        return response;

    }

    protected ObjectResponse delete(
            IdentityDto identityDto,
            String requestId,
            ManagedSysDto mSys,
            ManagedSystemObjectMatch matchObj) {

        CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();

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

        ObjectResponse resp = connectorAdapter.deleteRequest(mSys, request, MuleContextProvider.getCtx());

        return resp;
    }



    @Override
    public ProvisionGroupResponse deprovisionSelectedResources(@WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "requestorGroupId", targetNamespace = "") String requestorGroupId, @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
