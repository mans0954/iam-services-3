package org.openiam.provision.service;


import groovy.lang.MissingPropertyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationResponse;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.AccountLockEnum;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.PasswordResponse;
import org.openiam.provision.resp.ProvisionGroupResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
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
    private ManagedSystemService managedSystemService;

    @Autowired
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    protected AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

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
                        // bind the objects to the scripting engine
                        Map<String, Object> bindingMap = new HashMap<String, Object>();
                        bindingMap.put("operation", isAdd ? "ADD" : "MODIFY");
                        ManagedSysEntity managedSys = managedSystemService.getManagedSysByResource(res.getId(), "ACTIVE");
                        if (managedSys != null) {
                            String managedSysId = managedSys.getId();
                            bindingMap.put("sysId", managedSysId);
                            bindingMap.put("group", group);

                            List<AttributeMapEntity> attrMapEntities = managedSystemService
                                    .getAttributeMapsByManagedSysId(managedSysId);
                            List<AttributeMap> attrMap = attributeMapDozerConverter.convertToDTOList(attrMapEntities, true);
                            ManagedSystemObjectMatch matchObj = null;
                            List<ManagedSystemObjectMatchEntity> objList = managedSystemService.managedSysObjectParam(managedSysId,
                                    "GROUP");
                            if (CollectionUtils.isNotEmpty(objList)) {
                                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(objList.get(0), false);
                            }

                            ExtensibleObject extObj = buildFromRules(attrMap, bindingMap);

                            // get the attributes at the target system
                            // this lookup only for getting attributes from the
                            // system
                            String requestId = "R" + UUIDGen.getUUID();
                            Map<String, String> currentValueMap = new HashMap<String, String>();
//                            boolean isExistedInTargetSystem = getCurrentObjectAtTargetSystem(requestId, groupPrincipal, extObj, managedSys,
//                                    matchObj, currentValueMap);
                            boolean connectorSuccess = false;

                            // pre-processing
                            bindingMap.put("targetSystemAttributes", currentValueMap);

                            ResourceProp preProcessProp = res.getResourceProperty("PRE_PROCESS");
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


                        /*    if (!isExistedInTargetSystem) {

                                connectorSuccess = requestAddModify(groupPrincipal, requestId, managedSys, matchObj, extObj, true);

                            } else { // if user exists in target system

                                // updates the attributes with the correct operation codes
                                extObj = updateAttributeList(extUser, currentValueMap);

                                if (targetSysLogin.getOperation() == AttributeOperationEnum.REPLACE
                                        && targetSysLogin.getOrigPrincipalName() != null) {
                                    extObj.getAttributes().add(
                                            new ExtensibleAttribute("ORIG_IDENTITY", targetSysLogin.getOrigPrincipalName(),
                                                    AttributeOperationEnum.REPLACE.getValue(), "String"));
                                }
                                connectorSuccess = requestAddModify(targetSysLogin, requestId, managedSys, matchObj, extObj, false);
                            }*/


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

    private ExtensibleObject buildFromRules(List<AttributeMap> attrMap,
                                          Map<String, Object> bindingMap) {

        ExtensibleObject extUser = new ExtensibleObject();

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
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Integer) {

                                // if its memberOf object than dont add it to
                                // the list
                                // the connectors can detect a delete if an
                                // attribute is not in the list

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        ((Integer) output).toString(), 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else if (output instanceof Date) {
                                // date
                                Date d = (Date) output;
                                String DATE_FORMAT = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), sdf.format(d), 1, attr
                                        .getDataType().getValue());
                                newAttr.setObjectType(objectType);

                                extUser.getAttributes().add(newAttr);
                            } else if (output instanceof byte[]) {
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr.getAttributeName(), (byte[]) output, 1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {
                                // process a complex object which can be passed
                                // to the connector
                                newAttr = new ExtensibleAttribute(attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1, attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else {
                                // process a list - multi-valued object

                                newAttr = new ExtensibleAttribute(attr.getAttributeName(), (List) output, 1, attr
                                        .getDataType().getValue());
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
        }

        return extUser;
    }


    @Override
    public ProvisionGroupResponse deleteGroup(@WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId, @WebParam(name = "principal", targetNamespace = "") String principal, @WebParam(name = "status", targetNamespace = "") UserStatusEnum status, @WebParam(name = "requestorId", targetNamespace = "") String requestorId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProvisionGroupResponse deprovisionSelectedResources(@WebParam(name = "groupId", targetNamespace = "") String groupId, @WebParam(name = "requestorGroupId", targetNamespace = "") String requestorGroupId, @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
