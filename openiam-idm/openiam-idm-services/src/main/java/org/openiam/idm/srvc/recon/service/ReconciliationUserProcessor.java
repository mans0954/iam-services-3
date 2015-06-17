package org.openiam.idm.srvc.recon.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.parser.csv.UserSearchBeanCSVParser;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.recon.command.BaseReconciliationCommand;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.*;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.UserUtils;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.service.ProvisionServiceUtil;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component("reconciliationUserProcessor")
public class ReconciliationUserProcessor implements ReconciliationProcessor {

    private static final Log log = LogFactory.getLog(ReconciliationUserProcessor.class);

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    private ReconciliationCommandFactory commandFactory;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Value("${iam.files.location}")
    private String absolutePath;
    @Autowired
    private ManagedSystemWebService managedSystemWebService;
    @Autowired
    private ProvisionConnectorWebService connectorService;
    @Autowired
    private LoginDataWebService loginDataWebService;
	@Autowired
	private LoginDataService loginManager;
    @Autowired
    private ConnectorAdapter connectorAdapter;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    @Autowired
    protected UserCSVParser userCSVParser;
    @Autowired
    public UserSearchBeanCSVParser userSearchCSVParser;
    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;
    @Autowired
    @Qualifier("matchRuleFactory")
    private MatchRuleFactory matchRuleFactory;
    @Autowired
    protected GroupDataWebService groupDataWebService;
    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataWebService;
	@Autowired
	ReconciliationConfigService reconConfigService;

    private static final int BATCH_STOPPING_STEP = 10;
    private static final int CLEAR_SESSION_STEP = 20;

    @Override
    public ReconciliationResponse startReconciliation(final ReconciliationConfig config, final IdmAuditLog idmAuditLog) throws IOException, ScriptEngineException {
        Date startDate = new Date();

        Resource res = resourceDataService.getResource(config.getResourceId(), null);

        ManagedSysDto mSys = managedSystemWebService.getManagedSysByResource(res.getId());
        if (mSys == null) {
			log.error("Requested managed sys does not exist");
			return new ReconciliationResponse(ResponseStatus.FAILURE);
		}

        // have resource
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
				"Reconciliation for target system: " + mSys.getName() + " is started..." + startDate);

        log.debug("ManagedSys: Id = " + mSys.getId() + ", Name " + mSys.getName());
        log.debug("Getting identities for managedSys");

        // have situations
        Map<String, ReconciliationSituation> situations = new HashMap<String, ReconciliationSituation>();
        for (ReconciliationSituation situation : config.getSituationSet()) {
            situations.put(situation.getSituation().trim(),situation);
            log.debug("Created Command for: " + situation.getSituation());
        }
        // have resource connector
        ProvisionConnectorDto connector = connectorService.getProvisionConnector(mSys.getConnectorId());

        if (connector.getServiceUrl().contains("CSV")) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "CSV Processing started for configId="
                    + config.getReconConfigId() + " - resource=" + config.getResourceId());

            // reconciliation into TargetSystem directional
            log.debug("Start recon");
            connectorAdapter.reconcileResource(mSys, config);
            log.debug("end recon");
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "CSV Processing finished for configId="
                    + config.getReconConfigId() + " - resource=" + config.getResourceId());
            return new ReconciliationResponse(ResponseStatus.SUCCESS);
        }
        ReconciliationResultBean resultBean = new ReconciliationResultBean();
        List<AttributeMap> attrMap = managedSystemWebService.getResourceAttributeMaps(mSys.getResourceId());
        resultBean.setObjectType("USER");
        resultBean.setRows(new ArrayList<ReconciliationResultRow>());
        resultBean.setHeader(ReconciliationResultUtil.setHeaderInReconciliationResult(attrMap));

        // initialization match parameters of connector
        ManagedSystemObjectMatch matchObjAry[] = managedSystemWebService.managedSysObjectParam(mSys.getId(), "USER");
        // execute all Reconciliation Commands need to be check
        if (matchObjAry == null || matchObjAry.length < 1) {
            log.error("No match object found for this managed sys");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }
        String keyField = matchObjAry[0].getKeyField();
        String baseDnField = matchObjAry[0].getBaseDn();

        List<LoginEntity> idmIdentities = new ArrayList<LoginEntity>();

        UserSearchBean searchBean;
        if (StringUtils.isNotBlank(config.getMatchScript())) {
            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSys.getId());
            bindingMap.put("searchFilter", config.getSearchFilter());
            bindingMap.put("updatedSince", config.getUpdatedSince());
            IDMSearchScript searchScript = (IDMSearchScript) scriptRunner.instantiateClass(bindingMap,
                    config.getMatchScript());
            searchBean = searchScript.createUserSearchBean(bindingMap);
        } else {
            searchBean = new UserSearchBean();
        }
        // checking for STOP status
		if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
            return new ReconciliationResponse(ResponseStatus.SUCCESS);
        }

        if (searchBean != null) {
            if (searchBean.getPrincipal() == null) {
                searchBean.setPrincipal(new LoginSearchBean());
            }
            searchBean.getPrincipal().setManagedSysId(mSys.getId());
            List<UserEntity> idmUsers = userManager.getByExample(searchBean, 0, Integer.MAX_VALUE);

            if (CollectionUtils.isNotEmpty(idmUsers)) {
                for (UserEntity u : idmUsers) {
                    for (LoginEntity l : u.getPrincipalList()) {
                        if (l.getManagedSysId().equals(mSys.getId())) {
                            idmIdentities.add(l);
                            break;
                        }
                    }
                }
            }
        }

        List<String> processedUserIds = new ArrayList<String>();
        int usersCount = idmIdentities.size();
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Starting processing '" + usersCount
                + "' users from Repository to " + mSys.getName());

        int counter = 0;
        for (LoginEntity identity : idmIdentities) {
            counter++;
            if (counter % CLEAR_SESSION_STEP == 0) {
                reconConfigService.clearSession();
            }
            if (identity.getUserId() != null) {
                // checking for STOPING status for every 10 users
                if (counter % BATCH_STOPPING_STEP == 0) {
					if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
                        return new ReconciliationResponse(ResponseStatus.SUCCESS);
                    }
                }
                processedUserIds.add(identity.getUserId()); // Collect user
                // IDs to avoid
                // double
                // processing
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "starting reconciliation for user: "
                        + identity);

                reconciliationIDMUserToTargetSys(resultBean, attrMap, identity, mSys, situations,
                        config.getManualReconciliationFlag(), idmAuditLog);

                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "finished reconciliation for user: "
                        + identity);
            }
        }
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation from Repository to target system: " + mSys.getName() + " is complete.");
        // auditLogService.enqueue(auditBuilder);
        // 2. Do reconciliation users from Target Managed System to IDM
        // search for all Roles and Groups related with resource
        // GET Users from ConnectorAdapter by BaseDN and query rules

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Starting processing from target system: " + mSys.getName() + " to Repository");
        // auditLogService.enqueue(auditBuilder);
        // checking for STOPPING status
		if (processReconciliationStop(config.getReconConfigId(), idmAuditLog)) {
            return new ReconciliationResponse(ResponseStatus.SUCCESS);
        }
        processingTargetToIDM(config, mSys, situations, connector, keyField, baseDnField,
				processedUserIds, idmAuditLog);

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
				"Reconciliation from target system: " + mSys.getName() + " to Repository is complete.");
        // auditLogService.enqueue(auditBuilder);

        this.saveReconciliationResults(config.getResourceId(), resultBean);

		ReconciliationConfig reconConfig = reconConfigService.getConfigById(config.getReconConfigId());
		reconConfig.setLastExecTime(new Date());
        reconConfig.setExecStatus(ReconExecStatusOptions.FINISHED);
		reconConfigService.updateConfig(reconConfig);

        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Reconciliation for target system: " + mSys.getName() + " is complete.");

        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }

    private void saveReconciliationResults(String fileName, ReconciliationResultBean resultBean) {
        int i = 0;
        resultBean.getHeader().setRowId(i++);
        for (ReconciliationResultRow row : resultBean.getRows()) {
            row.setRowId(i++);
        }
        Serializer.serialize(resultBean, absolutePath + fileName + ".rcndat");
    }

    private ReconciliationResponse processingTargetToIDM(ReconciliationConfig config, ManagedSysDto mSys,
														 Map<String, ReconciliationSituation> situations,
														 ProvisionConnectorDto connector,
                                                         String keyField, String baseDnField,
														 List<String> processedUserIds, final IdmAuditLog idmAuditLog)
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
        SearchRequest<ExtensibleUser> searchRequest = new SearchRequest<>();
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
        searchRequest.setExtensibleObject(new ExtensibleUser());
        SearchResponse searchResponse;

        log.debug("Calling reconcileResource with Local connector");
        searchResponse = connectorAdapter.search(searchRequest, connector);

        if (searchResponse != null && searchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ObjectValue> usersFromRemoteSys = searchResponse.getObjectList();
            if (usersFromRemoteSys != null) {

                // AUDITLOG COUNT of proccessing users from target sys
                int counter = 0;
                for (ObjectValue userValue : usersFromRemoteSys) {
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
                    List<ExtensibleAttribute> extensibleAttributes = userValue.getAttributeList() != null ? userValue
                            .getAttributeList() : new LinkedList<ExtensibleAttribute>();
                    String targetUserPrincipal = reconcilationTargetUserObjectToIDM(mSys, situations,
                            extensibleAttributes, config, processedUserIds, idmAuditLog);

                    if (StringUtils.isNotEmpty(targetUserPrincipal)) {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "reconciled user: "
                                + targetUserPrincipal);
                    } else {
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "reconciled user: " + userValue.getObjectIdentity());
                    }
                }
            }
        } else {
            log.debug(searchResponse.getErrorMessage());
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Error: " + searchResponse);
        }
        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }

	// Reconciliation processingTargetToIDM
    private String reconcilationTargetUserObjectToIDM(ManagedSysDto mSys,
                                                      Map<String, ReconciliationSituation> situations, List<ExtensibleAttribute> extensibleAttributes,
                                                      ReconciliationConfig config, List<String> processedUserIds, final IdmAuditLog idmAuditLog) throws IOException {
        String targetUserPrincipal = null;

        Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
        for (ExtensibleAttribute attr : extensibleAttributes) {
            // search principal attribute by KeyField
            attributeMap.put(attr.getName(), attr);
            if (attr.getName().equals(config.getCustomMatchAttr())) {
                targetUserPrincipal = attr.getValue();
                break;
            }
        }

        if (StringUtils.isBlank(targetUserPrincipal)) {
            throw new IllegalArgumentException("Target system Principal can not be defined with Match Attribute Name: "
                    + config.getCustomMatchAttr());
        }

        try {
            MatchObjectRule matchObjectRule = matchRuleFactory.create(config.getCustomIdentityMatchScript());
            User usr = matchObjectRule.lookup(config, attributeMap);

            if (usr != null) {
                if (processedUserIds.contains(usr.getId())) { // already
                    // processed
                    return targetUserPrincipal;
                }
                User u = userManager.getUserDto(usr.getId());
                // situation TARGET EXIST, IDM EXIST do modify
                // check principal list on this ManagedSys exists
                List<Login> principals = u.getPrincipalList();
                Login principal = null;
                for (Login l : principals) {
                    if (l.getManagedSysId().equals(mSys.getId())) {
                        principal = l;
                        break;
                    }
                }
                // if user exists but don;t have principal for current target
                // sys
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                ReconciliationObjectCommand<User> command = commandFactory.createUserCommand(situation.getSituationResp(), situation, mSys.getId());

                if (command != null) {
                    ProvisionUser newUser = new ProvisionUser(u);
                    if (principal == null) {
                        principal = new Login();
                        principal.setLogin(targetUserPrincipal);
                        principal.setManagedSysId(mSys.getId());
                        principal.setOperation(AttributeOperationEnum.ADD);
                        principal.setStatus(LoginStatusEnum.ACTIVE);
                        principal.setProvStatus(ProvLoginStatusEnum.CREATED);
                        // ADD Target user principal
                        newUser.getPrincipalList().add(principal);
                    }
                    newUser.setSrcSystemId(mSys.getId());

                    log.debug("Call command for IDM Match Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for user= "
                            + targetUserPrincipal);
                    // AUDIT LOG Y user processing IDM_EXISTS__SYS_EXISTS
                    // situation
                    command.execute(situation, principal.getLogin(), mSys.getId(), newUser, extensibleAttributes);

                }
            } else {
                // create new user in IDM
                ReconciliationSituation situation = situations.get(ReconciliationCommand.SYS_EXISTS__IDM_NOT_EXISTS);
                ReconciliationObjectCommand<User>  command = commandFactory.createUserCommand(situation.getSituationResp(), situation, mSys.getId());

                if (command != null) {
                    Login l = new Login();
                    l.setLogin(targetUserPrincipal);
                    l.setManagedSysId(mSys.getId());
                    l.setOperation(AttributeOperationEnum.ADD);
                    ProvisionUser newUser = new ProvisionUser();

                    newUser.setSrcSystemId(mSys.getId());
                    // ADD Target user principal
                    newUser.getPrincipalList().add(l);
                    LoginResponse loginResponse = loginDataWebService.getLoginByManagedSys(targetUserPrincipal,
							BaseReconciliationCommand.OPENIAM_MANAGED_SYS_ID);
                    if (loginResponse.isSuccess()) {
                        newUser.getPrincipalList().add(loginResponse.getPrincipal());
                    }

                    log.debug("Call command for Match Not Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "SYS_EXISTS__IDM_NOT_EXISTS for user= "
                            + targetUserPrincipal);

                    // AUDIT LOG Y user processing SYS_EXISTS__IDM_NOT_EXISTS
                    // situation
                    command.execute(situation, l.getLogin(), mSys.getId(), newUser, extensibleAttributes);
                }
            }

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
        }
        return targetUserPrincipal;
    }

    private boolean reconciliationIDMUserToTargetSys(ReconciliationResultBean resultBean,
                                                     List<AttributeMap> attrMap, final LoginEntity identity, final ManagedSysDto mSys,
                                                     final Map<String, ReconciliationSituation> situations, boolean isManualRecon, IdmAuditLog idmAuditLog) throws IOException {

        User user = userManager.getUserDto(identity.getUserId());
        log.debug("1 Reconciliation for user " + user);

        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();

        for (AttributeMap ame : attrMap) {
            if (PolicyMapObjectTypeOptions.USER.name().equalsIgnoreCase(ame.getMapForObjectType()) && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getName(), null));
            }
        }

        String principal = identity.getLogin();
        log.debug("looking up identity in resource: " + principal);
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "looking up identity in resource: " + principal);

        LookupUserResponse lookupResp = provisionService.getTargetSystemUser(principal, mSys.getId(),
                requestedExtensibleAttributes);

        log.debug("Lookup status for " + principal + " =" + lookupResp.getStatus());
        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                "Lookup status for " + principal + " =" + lookupResp.getStatus());

        boolean userFoundInTargetSystem = lookupResp.getStatus() == ResponseStatus.SUCCESS;
        ExtensibleUser fromIDM = new ExtensibleUser();
        ExtensibleUser fromTS = new ExtensibleUser();

        List<ExtensibleAttribute> extensibleAttributes = lookupResp.getAttrList() != null ? lookupResp.getAttrList()
                : new LinkedList<ExtensibleAttribute>();
        fromTS.setAttributes(extensibleAttributes);
        fromTS.setPrincipalFieldName(lookupResp.getPrincipalName());
        fromIDM.setAttributes(new ArrayList<ExtensibleAttribute>());
        getValuesForExtensibleUser(fromIDM, user, attrMap, identity);
        if (userFoundInTargetSystem) {
            // Record exists in resource
            if (UserStatusEnum.DELETED.equals(user.getStatus())) {
                // IDM_DELETED__SYS_EXISTS

                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, null,
                                ReconciliationResultCase.IDM_DELETED));

                if (!isManualRecon) {
                    ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_DELETED__SYS_EXISTS);
                    ReconciliationObjectCommand<User> command = commandFactory.createUserCommand(situation.getSituationResp(), situation, mSys.getId());

                    if (command != null) {
                        log.debug("Call command for: Record in resource but deleted in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "SYS_EXISTS__IDM_NOT_EXISTS for user= " + principal);

                        command.execute(situation, identity.getLogin(), mSys.getId(), provisionUser, extensibleAttributes);
                    }
                }
            } else {
                // IDM_EXISTS__SYS_EXISTS
                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, fromTS,
                                ReconciliationResultCase.MATCH_FOUND));
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                ReconciliationObjectCommand<User> command = commandFactory.createUserCommand(situation.getSituationResp(), situation, mSys.getId());

                    if (command != null) {
                        log.debug("Call command for: Record in resource and in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());

                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for user= "
                                + principal);

                        command.execute(situation, identity.getLogin(), mSys.getId(), provisionUser, extensibleAttributes);
                }
            }

        } else {
            // Record not found in resource
            if (!UserStatusEnum.DELETED.equals(user.getStatus())) {
                // IDM_EXISTS__SYS_NOT_EXISTS
                resultBean.getRows().add(
                        setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, fromTS,
								ReconciliationResultCase.NOT_EXIST_IN_RESOURCE));
                ReconciliationSituation situation = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_NOT_EXISTS);
                ReconciliationObjectCommand<User>  command = commandFactory.createUserCommand(situation.getSituationResp(), situation, mSys.getId());

                if (command != null) {
                    log.debug("Call command for: Record in resource and in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());

                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "IDM_EXISTS__SYS_NOT_EXISTS for user= " + principal);

                        command.execute(situation, identity.getLogin(), mSys.getId(), provisionUser, extensibleAttributes);
                }
            }
        }

        return true;
    }

    private void getValuesForExtensibleUser(ExtensibleUser fromIDM, User user, List<AttributeMap> attrMap,
                                            LoginEntity identity) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            bindingMap.put("user", new ProvisionUser(user));
            bindingMap.put("managedSysId", identity.getManagedSysId());
            final ManagedSystemObjectMatch[] matches = managedSystemWebService.managedSysObjectParam(
                    identity.getManagedSysId(), "USER");
            if (matches != null && matches.length > 0) {
                bindingMap.put("matchParam", matches[0]);
            }

            // get all groups for user
            final GroupSearchBean sb = new GroupSearchBean();
            sb.addUserId(user.getId());
            sb.setDeepCopy(false);
            
            
            final List<org.openiam.idm.srvc.grp.dto.Group> curGroupList =groupDataWebService.findBeansLocalize(sb, null, -1, -1, null);

            String decPassword;
			if (StringUtils.isEmpty(identity.getUserId())) {
				throw new IllegalArgumentException("Identity userId can not be empty");
			}
			String password = identity.getPassword();
			if (password != null) {
				decPassword = loginManager.decryptPassword(identity.getUserId(), password);
				bindingMap.put("password", decPassword);
			}
			bindingMap.put("lg", identity);
			bindingMap.put("targetSystemIdentity", identity.getLogin());

            // make the role and group list before these updates available to
            // the
            // attribute policies
            bindingMap.put("currentGroupList", curGroupList);
            for (AttributeMap attr : attrMap) {
                fromIDM.getAttributes().add(
                        new ExtensibleAttribute(attr.getName(), (String) ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, scriptRunner)));
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                        && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    fromIDM.setPrincipalFieldName(attr.getName());
                }
            }

        } catch (Exception e) {
            log.error(e);
            // e.printStackTrace();
        }
    }


    private <T> T convertObject(List<ReconciliationResultField> header, List<ReconciliationResultField> fields,
                                Class<T> clazz, boolean onlyKeyField) throws InstantiationException, IllegalAccessException {
        if (clazz.getSimpleName().equals(userCSVParser.getObjectSimlpeClass())) {
            return (T) userCSVParser.getObjectByReconResltFields(header, fields, onlyKeyField);
        }
        if (clazz.getSimpleName().equals(userSearchCSVParser.getObjectSimlpeClass())) {
            return (T) userSearchCSVParser.getObjectByReconResltFields(header, fields, onlyKeyField);
        }
        return null;
    }

    private User getUserFromIDM(List<ReconciliationResultField> header, ReconciliationResultRow row)
            throws InstantiationException, IllegalAccessException {
        UserSearchBean searchBean = convertObject(header, row.getFields(), UserSearchBean.class, true);
        searchBean.setShowInSearch(0);
        searchBean.setMaxResultSize(1);
		searchBean.setDeepCopy(true);
        List<User> idmUsers = userDataWebService.findBeans(searchBean, 0, 1);
        if (CollectionUtils.isEmpty(idmUsers)) {
            return null;
        } else {
            return idmUsers.get(0);
        }
    }

    private ReconciliationResultRow setRowInReconciliationResult(ReconciliationResultRow headerRow,
                                                                 List<AttributeMap> attrMapList, ExtensibleUser currentObject, ExtensibleUser findedObject,
                                                                 ReconciliationResultCase caseReconciliation) {
        ReconciliationResultRow row = new ReconciliationResultRow();

        Map<String, ReconciliationResultField> user1Map = null;
        Map<String, ReconciliationResultField> user2Map = null;
        if (currentObject != null) {
            user1Map = UserUtils.extensibleAttributeListToReconciliationResultFieldMap(currentObject);
        }
        if (findedObject != null) {
            user2Map = UserUtils.extensibleAttributeListToReconciliationResultFieldMap(findedObject);
        }
        row.setCaseReconciliation(caseReconciliation);
        if (!MapUtils.isEmpty(user1Map) && MapUtils.isEmpty(user2Map)) {
            row.setFields(this.setFromReconcilationUser(user1Map, headerRow));
        }
        if (MapUtils.isEmpty(user1Map) && !MapUtils.isEmpty(user2Map)) {
            row.setFields(this.setFromReconcilationUser(user2Map, headerRow));
        }
        // Merge both
        if (!MapUtils.isEmpty(user1Map) && !MapUtils.isEmpty(user2Map)) {
            for (String key : user1Map.keySet()) {
                ReconciliationResultField value1 = user1Map.get(key);
                ReconciliationResultField value2 = user2Map.get(key);

                if (value1 == null && value2 != null) {
                    user1Map.put(key, value2);
                }
                if (value1 != null && value2 != null && !value1.equals(value2)) {
                    row.setCaseReconciliation(ReconciliationResultCase.MATCH_FOUND_DIFFERENT);
                    List<String> merged = new ArrayList<String>();
                    for (String val : value1.getValues()) {
                        if (StringUtils.isNotBlank(val)) {
                            merged.add(val);
                        }
                    }
                    for (String val : value2.getValues()) {
                        if (StringUtils.isNotBlank(val)) {
                            merged.add(val);
                        }
                    }
                    value1.setValues(merged);
                    user1Map.put(key, value1);
                }
            }
            row.setFields(this.setFromReconcilationUser(user1Map, headerRow));
        }

        return row;
    }

    private List<ReconciliationResultField> setFromReconcilationUser(Map<String, ReconciliationResultField> user,
                                                                     ReconciliationResultRow headerRow) {
        List<ReconciliationResultField> fieldList = new ArrayList<ReconciliationResultField>();
        for (ReconciliationResultField field : headerRow.getFields()) {
            ReconciliationResultField value = user.get(field.getValues().get(0));

            ReconciliationResultField newField = new ReconciliationResultField();
            if (value == null)
                newField.setValues(Arrays.asList(""));
            else
                newField.setValues(value.getValues());
            fieldList.add(newField);
        }
        return fieldList;
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
