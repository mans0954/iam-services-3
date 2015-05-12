package org.openiam.idm.srvc.recon.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.command.BaseReconciliationCommand;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.openiam.provision.resp.LookupObjectResponse;

import java.io.IOException;
import java.util.*;

@Component("reconciliationGroupProcessor")
public class ReconciliationGroupProcessor implements ReconciliationProcessor {


    private static final Log log = LogFactory.getLog(ReconciliationGroupProcessor.class);

    @Autowired
    private ResourceDataService resourceDataService;

    @Autowired
    private ManagedSystemWebService managedSysService;

    @Autowired
    private ProvisionConnectorWebService connectorService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("groupWS")
    private GroupDataWebService groupDataWebService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("matchRuleFactory")
    private MatchRuleFactory matchRuleFactory;

    @Autowired
    @Qualifier("reconciliationFactory")
    private ReconciliationCommandFactory commandFactory;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService provisionService;

	@Autowired
	ReconciliationConfigService reconConfigService;

    private static final int BATCH_STOPPING_STEP = 10;
    private static final int CLEAR_SESSION_STEP = 20;

    @Override
    public ReconciliationResponse startReconciliation(ReconciliationConfig config, IdmAuditLog idmAuditLog) throws IOException, ScriptEngineException {
        log.debug("Reconciliation started for configId=" + config.getReconConfigId() + " - resource="
                + config.getResourceId());

        Resource res = resourceDataService.getResource(config.getResourceId(), null);

        ManagedSysDto mSys = managedSysService.getManagedSysByResource(res.getId());
		if (mSys == null) {
			log.error("Requested managed sys does not exist");
			return new ReconciliationResponse(ResponseStatus.FAILURE);
		}

        // have resource
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation for target system: " + mSys.getName() + " is started..." + new Date());

        log.debug("ManagedSysId = " + mSys.getId());
        log.debug("Getting identities for managedSys");

        // have situations
        Map<String, ReconciliationSituation> situations = new HashMap<String, ReconciliationSituation>();
        for (ReconciliationSituation situation : config.getSituationSet()) {
            situations.put(situation.getSituation().trim(), situation);
        }

        // have resource connector
        ProvisionConnectorDto connector = connectorService.getProvisionConnector(mSys.getConnectorId());

        List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(res.getId());

        // initialization match parameters of connector
        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(mSys.getId(), "GROUP");
        // execute all Reconciliation Commands need to be check
        if (matchObjAry.length == 0) {
            log.error("No match object found for this managed sys");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }
        String keyField = matchObjAry[0].getKeyField();
        String baseDnField = matchObjAry[0].getBaseDn();

        GroupSearchBean searchBean;
        if (StringUtils.isNotBlank(config.getMatchScript())) {
            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
            bindingMap.put("searchFilter", config.getSearchFilter());
            bindingMap.put("updatedSince", config.getUpdatedSince());
            IDMSearchScript searchScript = (IDMSearchScript) scriptRunner.instantiateClass(bindingMap,
                    config.getMatchScript());

            searchBean = searchScript.createGroupSearchBean(bindingMap);
        } else {
            searchBean = new GroupSearchBean();
        }

		// checking for STOP status
		if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
			return new ReconciliationResponse(ResponseStatus.SUCCESS);
		}

        // First get All Groups by search bean from IDM for processing
        List<String> processedGroupIds = new ArrayList<String>();

        if (searchBean != null) {
            List<Group> idmGroups = groupDataWebService.findBeans(searchBean, null, 0, Integer.MAX_VALUE);
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Starting processing '" + idmGroups.size()
                    + "' users from Repository to " + mSys.getName());
            int counter = 0;
            for (Group group : idmGroups) {
                counter++;
                if (counter % CLEAR_SESSION_STEP == 0) {
                    reconConfigService.clearSession();
                }
                // checking for STOPING status for every 10 users
                if (counter % BATCH_STOPPING_STEP == 0) {
					if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
						return new ReconciliationResponse(ResponseStatus.SUCCESS);
					}
                }

                processedGroupIds.add(group.getId());
                // IDs to avoid
                // double
                // processing
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "starting reconciliation for group: "
                        + group.getName());

                reconciliationIDMGroupToTargetSys(attrMap, group, mSys, situations, idmAuditLog);

                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "finished reconciliation for group: "
                        + group.getName());
            }
        }

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation from Repository to target system: " + mSys.getName() + " is complete.");

        // 2. Do reconciliation users from Target Managed System to IDM
        // search for all Roles and Groups related with resource
        // GET Users from ConnectorAdapter by BaseDN and query rules

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Starting processing from target system: " + mSys.getName() + " to Repository");

		if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
			return new ReconciliationResponse(ResponseStatus.SUCCESS);
		}

        processingTargetToIDM(config, mSys, situations, connector, keyField, baseDnField,
                processedGroupIds, idmAuditLog);


        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation from target system: " + mSys.getName() + " to Repository is complete.");

		ReconciliationConfig reconConfig = reconConfigService.getConfigById(config.getReconConfigId());
		reconConfig.setLastExecTime(new Date());
		reconConfig.setExecStatus(ReconExecStatusOptions.FINISHED);
		reconConfigService.updateConfig(reconConfig);

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation for target system: " + mSys.getName() + " is complete.");

        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }


    private ReconciliationResponse processingTargetToIDM(ReconciliationConfig config,
                                                         ManagedSysDto mSys,
                                                         Map<String, ReconciliationSituation> situations,
                                                         ProvisionConnectorDto connector,
                                                         String keyField,
                                                         String baseDnField,
                                                         List<String> processedGroupIds,
                                                         final IdmAuditLog idmAuditLog)
            throws ScriptEngineException, IOException {

        if (config == null) {
            log.error("Reconciliation config is null");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }
        if (StringUtils.isBlank(config.getTargetSystemMatchScript())) {
            log.error("SearchQuery is not defined for reconciliation config.");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
        bindingMap.put("baseDnField", baseDnField);
        bindingMap.put("searchFilter", config.getTargetSystemSearchFilter());
        bindingMap.put("lastExecTime", config.getLastExecTime());
        bindingMap.put("updatedSince", config.getUpdatedSince());
        String searchQuery = (String) scriptRunner.execute(bindingMap, config.getTargetSystemMatchScript());
        if (StringUtils.isEmpty(searchQuery)) {
            log.error("SearchQuery not defined for this reconciliation config.");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }
        log.debug("processingTargetToIDM: mSys=" + mSys);
        SearchRequest<ExtensibleGroup> searchRequest = new SearchRequest<>();
        String requestId = "R" + UUIDGen.getUUID();
        searchRequest.setRequestID(requestId);
        searchRequest.setBaseDN(baseDnField);
        searchRequest.setScriptHandler(mSys.getSearchHandler());
        searchRequest.setSearchValue(keyField);
        searchRequest.setSearchQuery(searchQuery);
        searchRequest.setTargetID(mSys.getId());
        searchRequest.setHostUrl(mSys.getHostUrl());
        searchRequest.setHostPort((mSys.getPort() != null) ? mSys.getPort().toString() : null);
        searchRequest.setHostLoginId(mSys.getUserId());
        searchRequest.setHostLoginPassword(mSys.getDecryptPassword());
        searchRequest.setExtensibleObject(new ExtensibleGroup());
        SearchResponse searchResponse;

        log.debug("Calling reconcileResource with Local connector");
        searchResponse = connectorAdapter.search(searchRequest, connector, MuleContextProvider.getCtx());

        if (searchResponse != null && searchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ObjectValue> groupsFromRemoteSys = searchResponse.getObjectList();
            if (groupsFromRemoteSys != null) {

                // AUDITLOG COUNT of proccessing users from target sys
                int counter = 0;
                for (ObjectValue groupValue : groupsFromRemoteSys) {
                    counter++;
                    if (counter % CLEAR_SESSION_STEP == 0) {
                        reconConfigService.clearSession();
                    }
                    // AUDITLOG start processing user Y from target systems to
                    // IDM

                    // checking for STOPPING status every 10 users
                    if (counter % BATCH_STOPPING_STEP == 0) {
						if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
							return new ReconciliationResponse(ResponseStatus.SUCCESS);
						}
                    }

                    List<ExtensibleAttribute> extensibleAttributes = groupValue.getAttributeList() != null ? groupValue
                            .getAttributeList() : new LinkedList<ExtensibleAttribute>();

                    String targetUserPrincipal = reconcilationTargetGroupObjectToIDM(mSys, situations,
                            extensibleAttributes, config, processedGroupIds, idmAuditLog);

                    if (StringUtils.isNotEmpty(targetUserPrincipal)) {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "reconciled group: "
                                + targetUserPrincipal);
                    } else {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "reconciled group: " + groupValue.getObjectIdentity());
                    }
                }
            }
        } else {
            log.debug(searchResponse.getErrorMessage());
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Error: " + searchResponse);
        }
        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }

    private boolean reconciliationIDMGroupToTargetSys(List<AttributeMap> attrMap,
                                                      final Group group,
                                                      final ManagedSysDto mSys,
                                                      final Map<String, ReconciliationSituation> situations,
                                                      IdmAuditLog idmAuditLog) throws IOException {

        IdentityDto primaryIdentity = identityService.getIdentityByManagedSys(group.getId(),
                BaseReconciliationCommand.OPENIAM_MANAGED_SYS_ID);
        IdentityDto identitySys = identityService.getIdentityByManagedSys(group.getId(), mSys.getId());

        log.debug("Reconciliation for group: " + group);

        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();

        for (AttributeMap ame : attrMap) {
            if ((PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(ame.getMapForObjectType()) || PolicyMapObjectTypeOptions.GROUP.name().equalsIgnoreCase(ame.getMapForObjectType()))
                    && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getAttributeName(), null));
            }
        }

        List<ExtensibleAttribute> extensibleAttributes = new LinkedList<ExtensibleAttribute>();
        boolean userFoundInTargetSystem = false;
        if (identitySys != null) {
            String principal = identitySys.getIdentity();
            log.debug("looking up identity in resource: " + principal);
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "looking up identity in resource: " + principal);

            LookupObjectResponse lookupResp = provisionService.getTargetSystemObject(principal, mSys.getId(),
                    requestedExtensibleAttributes);

            log.debug("Lookup status for " + principal + " =" + lookupResp.getStatus());
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "Lookup status for " + principal + " =" + lookupResp.getStatus());
            userFoundInTargetSystem = lookupResp.getStatus() == ResponseStatus.SUCCESS;
            if (lookupResp.getAttrList() != null) {
                extensibleAttributes = lookupResp.getAttrList();
            }

        }

        if (userFoundInTargetSystem) {
            // Record exists in resource
            if (UserStatusEnum.DELETED.getValue().equalsIgnoreCase(group.getStatus())) {
                // IDM_DELETED__SYS_EXISTS
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_DELETED__SYS_EXISTS);
                ReconciliationObjectCommand<Group> command = commandFactory.createGroupCommand(situation.getSituationResp(), situation, mSys.getId());
                if (command != null) {
                    log.debug("Call command for: Record in resource but deleted in IDM");
                    ProvisionGroup provisionGroup = new ProvisionGroup(group);
                    provisionGroup.setParentAuditLogId(idmAuditLog.getId());
                    provisionGroup.setSrcSystemId(mSys.getId());
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                            "SYS_EXISTS__IDM_NOT_EXISTS for group= " + identitySys.getIdentity());

                    command.execute(situation, identitySys.getIdentity(), mSys.getId(), provisionGroup, extensibleAttributes);
                }
            } else {
                // IDM_EXISTS__SYS_EXISTS
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                ReconciliationObjectCommand<Group> command = commandFactory.createGroupCommand(situation.getSituationResp(), situation, mSys.getId());
                if (command != null) {
                    log.debug("Call command for: Record in resource and in IDM");
                    ProvisionGroup provisionGroup = new ProvisionGroup(group);
                    provisionGroup.setParentAuditLogId(idmAuditLog.getId());
                    provisionGroup.setSrcSystemId(mSys.getId());

                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for group= "
                            + identitySys.getIdentity());

                    command.execute(situation, identitySys.getIdentity(), mSys.getId(), provisionGroup, extensibleAttributes);
                }
            }

        } else {
            // Record not found in resource
            if (!UserStatusEnum.DELETED.getValue().equalsIgnoreCase(group.getStatus())) {
                // IDM_EXISTS__SYS_NOT_EXISTS
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_NOT_EXISTS);
                ReconciliationObjectCommand<Group> command = commandFactory.createGroupCommand(situation.getSituationResp(), situation, mSys.getId());
                if (command != null) {
                    log.debug("Call command for: Record in resource and in IDM");
                    ProvisionGroup provisionGroup = new ProvisionGroup(group);
                    provisionGroup.setParentAuditLogId(idmAuditLog.getId());
                    provisionGroup.setSrcSystemId(mSys.getId());

                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                            "IDM_EXISTS__SYS_NOT_EXISTS for group= " + primaryIdentity.getIdentity());

                    command.execute(situation, primaryIdentity.getIdentity(), mSys.getId(), provisionGroup, extensibleAttributes);
                }
            }
        }

        return true;
    }

    // Reconciliation processingTargetToIDM
    private String reconcilationTargetGroupObjectToIDM(ManagedSysDto mSys,
                                                       Map<String, ReconciliationSituation> situations,
                                                       List<ExtensibleAttribute> extensibleAttributes,
                                                       ReconciliationConfig config,
                                                       List<String> processedGroupIds,
                                                       final IdmAuditLog idmAuditLog) throws IOException {
        String targetGroupPrincipal = null;

        Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
        for (ExtensibleAttribute attr : extensibleAttributes) {
            // search principal attribute by KeyField
            attributeMap.put(attr.getName(), attr);
            if (attr.getName().equals(config.getCustomMatchAttr())) {
                targetGroupPrincipal = attr.getValue();
                break;
            }
        }

        if (StringUtils.isBlank(targetGroupPrincipal)) {
            throw new IllegalArgumentException("Target system Principal can not be defined with Match Attribute Name: "
                    + config.getCustomMatchAttr());
        }

        try {
            MatchObjectRule matchObjectRule = matchRuleFactory.create(config.getCustomIdentityMatchScript());
            Group grp = matchObjectRule.lookupGroup(config, attributeMap);

            if (grp != null) {
                if (processedGroupIds.contains(grp.getId())) { // already
                    // processed
                    return targetGroupPrincipal;
                }
                Group gr = groupDataWebService.getGroup(grp.getId(), null);

                IdentityDto identityDto = identityService.getIdentityByManagedSys(gr.getId(), mSys.getId());
                if (identityDto == null) {
                    // SET Default OpenIAM identity to avoid NULL pointer
                    identityDto = identityService.getIdentityByManagedSys(gr.getId(), sysConfiguration.getDefaultManagedSysId());
                }
                // situation TARGET EXIST, IDM EXIST do modify
                // if user exists but don;t have principal for current target
                // sys
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                ReconciliationObjectCommand<Group> command = commandFactory.createGroupCommand(situation.getSituationResp(), situation, mSys.getId());
                if (command != null) {
                    ProvisionGroup newGroup = new ProvisionGroup(gr);
                    newGroup.setSrcSystemId(mSys.getId());

                    log.debug("Call command for IDM Match Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for group= "
                            + identityDto.getIdentity());

                    // AUDIT LOG Y user processing IDM_EXISTS__SYS_EXISTS
                    // situation
                    command.execute(situation, identityDto.getIdentity(), mSys.getId(), newGroup, extensibleAttributes);

                }
            } else {
                // create new user in IDM
                ReconciliationSituation situation = situations.get(ReconciliationCommand.SYS_EXISTS__IDM_NOT_EXISTS);
                ReconciliationObjectCommand<Group> command = commandFactory.createGroupCommand(situation.getSituationResp(), situation, mSys.getId());
                if (command != null) {
                    ProvisionGroup newGroup = new ProvisionGroup();

                    newGroup.setSrcSystemId(mSys.getId());

                    IdentityDto identityDto = new IdentityDto(IdentityTypeEnum.GROUP, mSys.getId(), targetGroupPrincipal, newGroup.getId());
                    log.debug("Call command for Match Not Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "SYS_EXISTS__IDM_NOT_EXISTS for group= "
                            + targetGroupPrincipal);

                    // AUDIT LOG Y user processing SYS_EXISTS__IDM_NOT_EXISTS
                    // situation
                    command.execute(situation, identityDto.getIdentity(), mSys.getId(), newGroup, extensibleAttributes);
                }
            }

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
        }
        return targetGroupPrincipal;
    }

	private boolean processReconciliationStop(String reconConfigId, IdmAuditLog idmAuditLog) {
		ReconExecStatusOptions status = reconConfigService.getExecStatus(reconConfigId);
		if (status == ReconExecStatusOptions.STOPPING || status == ReconExecStatusOptions.STOPPED) {
			reconConfigService.updateExecStatus(reconConfigId, ReconExecStatusOptions.STOPPED);
			idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
					"Reconciliation was manually stopped at " + new Date());
			return true;
		}
		final boolean isRunning = (status == ReconExecStatusOptions.STARTED);
		return !isRunning;
	}
}
