/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.recon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.*;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.parser.csv.UserSearchBeanCSVParser;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultAction;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultCase;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultUtil;
import org.openiam.idm.srvc.recon.result.dto.ReconcliationFieldComparatorByField;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.UserUtils;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.service.ProvisionServiceUtil;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 * 
 */
@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    @Autowired
    protected ReconciliationSituationDAO reconSituationDAO;

    @Autowired
    protected ReconciliationConfigDAO reconConfigDao;

    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    private MailService mailService;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter objectMatchDozerConverter;
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private ProvisionConnectorWebService connectorService;
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected GroupDataService groupManager;
    @Autowired
    protected GroupDozerConverter groupDozerConverter;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private ReconciliationConfigDozerConverter reconConfigDozerMapper;
    @Autowired
    private ReconciliationSituationDozerConverter reconSituationDozerMapper;
    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;
    @Autowired
    protected UserCSVParser userCSVParser;
    @Autowired
    public UserSearchBeanCSVParser userSearchCSVParser;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    @Value("${iam.files.location}")
    private String absolutePath;
    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    private ReconciliationCommandFactory commandFactory;
    @Autowired
    protected MatchRuleFactory matchRuleFactory;
    @Autowired
    protected AuditLogService auditLogService;

    private static final Log log = LogFactory.getLog(ReconciliationServiceImpl.class);

    /*
    * The flags for the running tasks are handled by this Thread-Safe Set.
    * It stores the taskIds of the currently executing tasks.
    * This is faster and as reliable as storing the flags in the database,
    * if the tasks are only launched from ONE host in a clustered environment.
    * It is unique for each class-loader, which means unique per war-deployment.
    */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());

    public ReconciliationConfig addConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        Set<ReconciliationSituation> sitSet = null;
        if (!CollectionUtils.isEmpty(config.getSituationSet())) {
            sitSet = new HashSet<ReconciliationSituation>(config.getSituationSet());
        }
        config.setReconConfigId(null);
        ReconciliationConfig result = reconConfigDozerMapper.convertToDTO(
                reconConfigDao.add(reconConfigDozerMapper.convertToEntity(config, false)), false);
        saveSituationSet(sitSet, result.getReconConfigId());
        result.setSituationSet(sitSet);
        return result;
    }

    @Transactional
    private void saveSituationSet(Set<ReconciliationSituation> sitSet, String configId) {
        if (sitSet != null) {
            for (ReconciliationSituation s : sitSet) {
                if (StringUtils.isEmpty(s.getReconConfigId())) {
                    s.setReconConfigId(configId);
                }
                if (StringUtils.isEmpty(s.getReconSituationId())) {
                    s.setReconSituationId(null);
                    s.setReconSituationId(reconSituationDAO.add(reconSituationDozerMapper.convertToEntity(s, false))
                            .getReconSituationId());
                } else {
                    reconSituationDAO.update(reconSituationDozerMapper.convertToEntity(s, false));
                }
            }
        }
    }

    @Transactional
    public void updateConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        Set<ReconciliationSituation> sitSet = null;
        if (!CollectionUtils.isEmpty(config.getSituationSet())) {
            sitSet = new HashSet<ReconciliationSituation>(config.getSituationSet());
        }
        config.setSituationSet(null);

        reconConfigDao.update(reconConfigDozerMapper.convertToEntity(config, false));

        this.saveSituationSet(sitSet, config.getReconConfigId());
    }

    @Transactional
    public void removeConfigByResourceId(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        reconConfigDao.removeByResourceId(resourceId);

    }

    @Transactional
    public void removeConfig(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        ReconciliationConfigEntity config = reconConfigDao.findById(configId);
        reconConfigDao.delete(config);

    }

    @Transactional(readOnly = true)
    public ReconciliationConfig getConfigByResource(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfigDao.findByResourceId(resourceId);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);

    }

    public ReconciliationConfig getConfigById(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfigDao.findById(configId);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);
    }

    public ReconciliationResponse startReconciliation(ReconciliationConfig config) {

        ReconciliationConfigEntity configEntity = reconConfigDao.findById(config.getReconConfigId());

        Date startDate = new Date();
        IdmAuditLog idmAuditLog = new IdmAuditLog();

        idmAuditLog.setRequestorUserId(config.getRequesterId());
        idmAuditLog.setAction(AuditAction.RECONCILIATION.value());
        ManagedSysEntity managedSysEntity = managedSysService.getManagedSysById(config.getManagedSysId());
        idmAuditLog.setTargetManagedSys(config.getManagedSysId(), managedSysEntity.getName());
        idmAuditLog.setSource(config.getReconConfigId());

        if ("INACTIVE".equalsIgnoreCase(config.getStatus())) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Reconciliation config is in 'INACTIVE' status");
            auditLogService.enqueue(idmAuditLog);
            ReconciliationResponse resp = new ReconciliationResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PROCESS_INACTIVE);
            return resp;
        }

        ReconciliationResponse processCheckResponse = addTask(config.getReconConfigId());
        if ( processCheckResponse.getStatus() == ResponseStatus.FAILURE &&
                processCheckResponse.getErrorCode() == ResponseCode.FAIL_PROCESS_ALREADY_RUNNING) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Previous reconciliation run is not finished yet");
            auditLogService.enqueue(idmAuditLog);
            return processCheckResponse;
        }

        try {

            log.debug("Reconciliation started for configId=" + config.getReconConfigId() + " - resource="
                    + config.getResourceId());

            configEntity.setExecStatus(ReconExecStatusOptions.STARTED);

            reconConfigDao.save(configEntity);

            Resource res = resourceDataService.getResource(config.getResourceId(), null);

            ManagedSysEntity mSys = managedSysService.getManagedSysByResource(res.getId(), "ACTIVE");
            String managedSysId = (mSys != null) ? mSys.getId() : null;
            // have resource
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "Reconciliation for target system: " + mSys.getName() + " is started..." + startDate);

            log.debug("ManagedSysId = " + managedSysId);
            log.debug("Getting identities for managedSys");

            ManagedSysDto sysDto = null;
            if (mSys != null) {
                sysDto = managedSysDozerConverter.convertToDTO(mSys, true);
                if (sysDto != null && sysDto.getPswd() != null) {
                    try {
                        final byte[] bytes = keyManagementService.getUserKey(systemUserId, KeyName.password.name());
                        sysDto.setDecryptPassword(cryptor.decrypt(bytes, mSys.getPswd()));
                    } catch (Exception e) {
                        log.error("Can't decrypt", e);
                    }
                }
            }

            // have situations
            Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
            for (ReconciliationSituation situation : config.getSituationSet()) {
                situations.put(situation.getSituation().trim(),
                        commandFactory.createCommand(situation.getSituationResp(), situation, managedSysId));
                log.debug("Created Command for: " + situation.getSituation());
            }
            // have resource connector
            ProvisionConnectorDto connector = connectorService.getProvisionConnector(sysDto.getConnectorId());

            if (connector.getServiceUrl().contains("CSV")) {
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "CSV Processing started for configId="
                        + config.getReconConfigId() + " - resource=" + config.getResourceId());

                // reconciliation into TargetSystem directional
                log.debug("Start recon");
                connectorAdapter.reconcileResource(sysDto, config, MuleContextProvider.getCtx());
                log.debug("end recon");
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "CSV Processing finished for configId="
                        + config.getReconConfigId() + " - resource=" + config.getResourceId());
                return new ReconciliationResponse(ResponseStatus.SUCCESS);
            }
            ReconciliationResultBean resultBean = new ReconciliationResultBean();
            List<AttributeMapEntity> attrMap = managedSysService.getResourceAttributeMaps(sysDto.getResourceId());
            resultBean.setObjectType("USER");
            resultBean.setRows(new ArrayList<ReconciliationResultRow>());
            resultBean.setHeader(ReconciliationResultUtil.setHeaderInReconciliationResult(attrMap));

            // initialization match parameters of connector
            List<ManagedSystemObjectMatchEntity> matchObjAry = managedSysService.managedSysObjectParam(managedSysId,
                    "USER");
            // execute all Reconciliation Commands need to be check
            if (CollectionUtils.isEmpty(matchObjAry)) {
                log.error("No match object found for this managed sys");
                return new ReconciliationResponse(ResponseStatus.FAILURE);
            }
            String keyField = matchObjAry.get(0).getKeyField();
            String baseDnField = matchObjAry.get(0).getBaseDn();

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
            configEntity = reconConfigDao.get(config.getReconConfigId());
            reconConfigDao.refresh(configEntity);
            if (configEntity.getExecStatus() == ReconExecStatusOptions.STOPPING) {
                configEntity.setExecStatus(ReconExecStatusOptions.STOPPED);
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Reconciliation was manually stopped at "
                        + new Date());
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
                if (identity.getUserId() != null) {
                    // checking for STOPING status for every 10 users
                    if (counter == 10) {
                        configEntity = reconConfigDao.get(config.getReconConfigId());
                        reconConfigDao.refresh(configEntity);
                        if (configEntity.getExecStatus() == ReconExecStatusOptions.STOPPING) {
                            configEntity.setExecStatus(ReconExecStatusOptions.STOPPED);
                            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                    "Reconciliation was manually stopped at " + new Date());
                            return new ReconciliationResponse(ResponseStatus.SUCCESS);
                        }
                        counter = 0;
                    }
                    processedUserIds.add(identity.getUserId()); // Collect user
                                                                // IDs to avoid
                                                                // double
                                                                // processing
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "starting reconciliation for user: "
                            + identity);

                    reconciliationIDMUserToTargetSys(resultBean, attrMap, identity, sysDto, situations,
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
            configEntity = reconConfigDao.get(config.getReconConfigId());
            reconConfigDao.refresh(configEntity);
            if (configEntity.getExecStatus() == ReconExecStatusOptions.STOPPING) {
                configEntity.setExecStatus(ReconExecStatusOptions.STOPPED);
                idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Reconciliation was manually stopped at "
                        + new Date());
                return new ReconciliationResponse(ResponseStatus.SUCCESS);
            }
            processingTargetToIDM(config, managedSysId, sysDto, situations, connector, keyField, baseDnField,
                    processedUserIds, idmAuditLog);

            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "Reconciliation from target system: " + mSys.getName() + " to Repository is complete.");
            // auditLogService.enqueue(auditBuilder);

            this.saveReconciliationResults(config.getResourceId(), resultBean);

            configEntity.setLastExecTime(new Date());
            configEntity.setExecStatus(ReconExecStatusOptions.FINISHED);

            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                    "Reconciliation for target system: " + mSys.getName() + " is complete.");

            this.sendMail(config, res);

        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Error: " + e.getMessage());
            ReconciliationResponse resp = new ReconciliationResponse(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
            configEntity.setExecStatus(ReconExecStatusOptions.FAILED);
            return resp;

        } finally {
            endTask(config.getReconConfigId());
            auditLogService.enqueue(idmAuditLog);
            reconConfigDao.save(configEntity);
        }

        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }

    private ReconciliationResponse processingTargetToIDM(ReconciliationConfig config, String managedSysId,
            ManagedSysDto mSys, Map<String, ReconciliationCommand> situations, ProvisionConnectorDto connector,
            String keyField, String baseDnField, List<String> processedUserIds, final IdmAuditLog idmAuditLog)
            throws ScriptEngineException {

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
        SearchRequest searchRequest = new SearchRequest();
        String requestId = "R" + UUIDGen.getUUID();
        searchRequest.setRequestID(requestId);
        searchRequest.setBaseDN(baseDnField);
        searchRequest.setScriptHandler(mSys.getSearchHandler());
        searchRequest.setSearchValue(keyField);
        searchRequest.setSearchQuery(searchQuery);
        searchRequest.setTargetID(managedSysId);
        searchRequest.setHostUrl(mSys.getHostUrl());
        searchRequest.setHostPort((mSys.getPort() != null) ? mSys.getPort().toString() : null);
        searchRequest.setHostLoginId(mSys.getUserId());
        searchRequest.setHostLoginPassword(mSys.getDecryptPassword());
        searchRequest.setExtensibleObject(new ExtensibleUser());
        SearchResponse searchResponse;

        log.debug("Calling reconcileResource with Local connector");
        searchResponse = connectorAdapter.search(searchRequest, connector, MuleContextProvider.getCtx());

        if (searchResponse != null && searchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<ObjectValue> usersFromRemoteSys = searchResponse.getObjectList();
            if (usersFromRemoteSys != null) {

                // AUDITLOG COUNT of proccessing users from target sys
                int counter = 0;
                for (ObjectValue userValue : usersFromRemoteSys) {
                    counter++;
                    // AUDITLOG start processing user Y from target systems to
                    // IDM

                    // checking for STOPPING status every 10 users
                    if (counter == 10) {
                        ReconciliationConfigEntity configEntity = reconConfigDao.findById(config.getReconConfigId());
                        reconConfigDao.refresh(configEntity);
                        if (configEntity.getExecStatus() == ReconExecStatusOptions.STOPPING) {
                            configEntity.setExecStatus(ReconExecStatusOptions.STOPPED);
                            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                    "Reconciliation was manually stopped at " + new Date());
                            return new ReconciliationResponse(ResponseStatus.SUCCESS);
                        }
                        counter++;
                    }
                    List<ExtensibleAttribute> extensibleAttributes = userValue.getAttributeList() != null ? userValue
                            .getAttributeList() : new LinkedList<ExtensibleAttribute>();
                    String targetUserPrincipal = reconcilationTargetUserObjectToIDM(managedSysId, mSys, situations,
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
    private String reconcilationTargetUserObjectToIDM(String managedSysId, ManagedSysDto mSys,
            Map<String, ReconciliationCommand> situations, List<ExtensibleAttribute> extensibleAttributes,
            ReconciliationConfig config, List<String> processedUserIds, final IdmAuditLog idmAuditLog) {
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
                    if (l.getManagedSysId().equals(managedSysId)) {
                        principal = l;
                        break;
                    }
                }
                // if user exists but don;t have principal for current target
                // sys
                ReconciliationCommand command = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                if (command != null) {
                    ProvisionUser newUser = new ProvisionUser(u);
                    if (principal == null) {
                        principal = new Login();
                        principal.setLogin(targetUserPrincipal);
                        principal.setManagedSysId(managedSysId);
                        principal.setOperation(AttributeOperationEnum.ADD);
                        principal.setStatus(LoginStatusEnum.ACTIVE);
                        principal.setProvStatus(ProvLoginStatusEnum.CREATED);
                        // ADD Target user principal
                        newUser.getPrincipalList().add(principal);
                    }
                    newUser.setSrcSystemId(managedSysId);

                    log.debug("Call command for IDM Match Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for user= "
                            + targetUserPrincipal);
                    // AUDIT LOG Y user processing IDM_EXISTS__SYS_EXISTS
                    // situation
                    command.execute(principal, newUser, extensibleAttributes);

                }
            } else {
                // create new user in IDM
                ReconciliationCommand command = situations.get(ReconciliationCommand.SYS_EXISTS__IDM_NOT_EXISTS);
                if (command != null) {
                    Login l = new Login();
                    l.setLogin(targetUserPrincipal);
                    l.setManagedSysId(managedSysId);
                    l.setOperation(AttributeOperationEnum.ADD);
                    ProvisionUser newUser = new ProvisionUser();

                    newUser.setSrcSystemId(managedSysId);
                    // ADD Target user principal
                    newUser.getPrincipalList().add(l);
                    LoginEntity idmLogin = loginManager.getLoginByManagedSys(targetUserPrincipal, "0");
                    if (idmLogin != null) {
                        newUser.getPrincipalList().add(loginDozerConverter.convertToDTO(idmLogin, true));
                    }

                    log.debug("Call command for Match Not Found");
                    idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "SYS_EXISTS__IDM_NOT_EXISTS for user= "
                            + targetUserPrincipal);

                    // AUDIT LOG Y user processing SYS_EXISTS__IDM_NOT_EXISTS
                    // situation
                    command.execute(l, newUser, extensibleAttributes);
                }
            }

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
        }
        return targetUserPrincipal;
    }

    @Override
    public String manualReconciliation(ReconciliationResultBean reconciledBean, String resourceId) throws Exception {
        ReconciliationConfig config = this.getConfigByResource(resourceId);
        ManagedSysEntity mSys = managedSysService.getManagedSysByResource(resourceId, "ACTIVE");
        ReconciliationResultBean oldResult = this.getReconciliationResult(config, null);
        List<ReconciliationResultField> header = oldResult.getHeader().getFields();
        if (reconciledBean != null && reconciledBean.getRows() != null) {
            List<ReconciliationResultRow> reconciledRows = reconciledBean.getRows();
            for (ReconciliationResultRow row : reconciledRows) {
                switch (row.getCaseReconciliation()) {
                case NOT_EXIST_IN_IDM_DB:
                    if (row.getAction() == null) {
                        continue;
                    }
                    User u = this.convertObject(header, row.getFields(), User.class, false);
                    if (ReconciliationResultAction.ADD_TO_IDM.equals(row.getAction())) {
                        ProvisionUser puer = new ProvisionUser(u);
                        provisionService.addUser(puer);
                    }
                    if (ReconciliationResultAction.REMOVE_FROM_TARGET.equals(row.getAction())) {
                        // REMOVETE From Target system
                        // provisionService.de(managedSysDozerConverter
                        // .convertToDTO(mSys, false), u);
                    }
                    break;
                case NOT_EXIST_IN_RESOURCE:
                    if (ReconciliationResultAction.ADD_TO_TARGET.equals(row.getAction())) {
                        User idmUser = this.getUserFromIDM(header, row);
                        if (idmUser != null) {
                            provisionService.addUser(new ProvisionUser(idmUser));
                        }
                    }
                    if (ReconciliationResultAction.REMOVE_FROM_IDM.equals(row.getAction())) {
                        User idmUser = getUserFromIDM(header, row);
                        if (idmUser != null) {
                            provisionService.deleteByUserId(idmUser.getId(), UserStatusEnum.REMOVE, systemUserId);
                        }
                    }
                    break;
                case MATCH_FOUND_DIFFERENT:
                    User fromIDM = this.getUserFromIDM(header, row);
                    if (fromIDM != null) {
                        // merge idm and reconciled Users
                        fromIDM = userCSVParser.addObjectByReconResltFields(header, row.getFields(), fromIDM);
                        // userManager.updateUserWithDependent(userDozerConverter
                        // .convertToEntity(fromIDM, true),true);
                        provisionService.modifyUser(new ProvisionUser(fromIDM));
                    }
                    break;
                default:
                    break;
                }
            }
        } else {
            return "";
        }
        return "";
    }

    private boolean reconciliationIDMUserToTargetSys(ReconciliationResultBean resultBean,
            List<AttributeMapEntity> attrMap, final LoginEntity identity, final ManagedSysDto mSys,
            final Map<String, ReconciliationCommand> situations, boolean isManualRecon, IdmAuditLog idmAuditLog) {

        User user = userManager.getUserDto(identity.getUserId());
        Login idDto = loginDozerConverter.convertToDTO(identity, true);
        log.debug("1 Reconciliation for user " + user);

        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();

        for (AttributeMapEntity ame : attrMap) {
            if ("USER".equalsIgnoreCase(ame.getMapForObjectType()) && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getAttributeName(), null));
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
        this.getValuesForExtensibleUser(fromIDM, user, attrMap, identity);
        if (userFoundInTargetSystem) {
            // Record exists in resource
            if (UserStatusEnum.DELETED.equals(user.getStatus())) {
                // IDM_DELETED__SYS_EXISTS

                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, null,
                                ReconciliationResultCase.IDM_DELETED));

                if (!isManualRecon) {
                    ReconciliationCommand command = situations.get(ReconciliationCommand.IDM_DELETED__SYS_EXISTS);
                    if (command != null) {
                        log.debug("Call command for: Record in resource but deleted in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());
                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "SYS_EXISTS__IDM_NOT_EXISTS for user= " + principal);

                        command.execute(idDto, provisionUser, extensibleAttributes);
                    }
                }
            } else {
                // IDM_EXISTS__SYS_EXISTS

                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, fromTS,
                                ReconciliationResultCase.MATCH_FOUND));
                if (!isManualRecon) {
                    ReconciliationCommand command = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                    if (command != null) {
                        log.debug("Call command for: Record in resource and in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());

                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "IDM_EXISTS__SYS_EXISTS for user= "
                                + principal);

                        command.execute(idDto, provisionUser, extensibleAttributes);
                    }
                }
            }

        } else {
            // Record not found in resource
            if (!UserStatusEnum.DELETED.equals(user.getStatus())) {
                // IDM_EXISTS__SYS_NOT_EXISTS
                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getHeader(), attrMap, fromIDM, fromTS,
                                ReconciliationResultCase.NOT_EXIST_IN_RESOURCE));
                if (!isManualRecon) {
                    ReconciliationCommand command = situations.get(ReconciliationCommand.IDM_EXISTS__SYS_NOT_EXISTS);
                    if (command != null) {
                        log.debug("Call command for: Record in resource and in IDM");
                        ProvisionUser provisionUser = new ProvisionUser(user);
                        provisionUser.setParentAuditLogId(idmAuditLog.getId());
                        provisionUser.setSrcSystemId(mSys.getId());

                        idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION,
                                "IDM_EXISTS__SYS_NOT_EXISTS for user= " + principal);

                        command.execute(idDto, provisionUser, extensibleAttributes);
                    }
                }
            }
        }

        return true;
    }

    public ProvisionService getProvisionService() {
        return provisionService;
    }

    public void setProvisionService(ProvisionService provisionService) {
        this.provisionService = provisionService;
    }

    public ProvisionConnectorWebService getConnectorService() {
        return connectorService;
    }

    public void setConnectorService(ProvisionConnectorWebService connectorService) {
        this.connectorService = connectorService;
    }

    public void setConnectorAdapter(ConnectorAdapter connectorAdapter) {
        this.connectorAdapter = connectorAdapter;
    }

    private User getUserFromIDM(List<ReconciliationResultField> header, ReconciliationResultRow row)
            throws InstantiationException, IllegalAccessException {
        UserSearchBean searchBean = this.convertObject(header, row.getFields(), UserSearchBean.class, true);
        searchBean.setShowInSearch(0);
        searchBean.setMaxResultSize(1);
        List<org.openiam.idm.srvc.user.domain.UserEntity> idmUsers = userManager.getByExample(searchBean, 0,
                Integer.MAX_VALUE);
        if (CollectionUtils.isEmpty(idmUsers)) {
            return null;
        } else {
            return userDozerConverter.convertToDTO(idmUsers.get(0), true);
        }
    }

    private ReconciliationResultRow setRowInReconciliationResult(ReconciliationResultRow headerRow,
            List<AttributeMapEntity> attrMapList, ExtensibleUser currentObject, ExtensibleUser findedObject,
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

    @Override
    public String getReconciliationReport(ReconciliationConfig config, String reportType) {
        String fileName = StringUtils.isEmpty(config.getResourceId()) ? "" : config.getResourceId() + ".rcndat";
        if (StringUtils.isEmpty(fileName))
            return null;
        ReconciliationResultBean r = null;
        try {
            r = (ReconciliationResultBean) Serializer.deserializer(absolutePath + fileName);
        } catch (Exception e) {
            return "";
        }
        if (r == null)
            return "";
        if ("HTML".equalsIgnoreCase(reportType) || StringUtils.isEmpty(reportType)) {
            return r.toHTML();
        } else {
            return r.toCSV();
        }
    }

    private void saveReconciliationResults(String fileName, ReconciliationResultBean resultBean) {
        int i = 0;
        resultBean.getHeader().setRowId(i++);
        for (ReconciliationResultRow row : resultBean.getRows()) {
            row.setRowId(i++);
        }
        Serializer.serialize(resultBean, absolutePath + fileName + ".rcndat");
    }

    private void sendMail(ReconciliationConfig config, Resource res) {
        StringBuilder message = new StringBuilder();
        if (!StringUtils.isEmpty(config.getNotificationEmailAddress())) {
            message.append("Resource: " + res.getName() + ".\n");
            message.append("Uploaded CSV file: " + res.getId() + ".csv was successfully reconciled.\n");
            mailService.sendEmails(null, new String[] { config.getNotificationEmailAddress() }, null, null,
                    "CSVConnector", message.toString(), false, new String[] {});
        }
    }

    @Override
    public ReconciliationResultBean getReconciliationResult(ReconciliationConfig config,
            ManualReconciliationSearchBean searchBean) {
        if (config == null || config.getResourceId() == null)
            return null;
        ReconciliationResultBean resultBean = (ReconciliationResultBean) Serializer.deserializer(absolutePath
                + config.getResourceId() + ".rcndat");
        if (resultBean == null)
            return null;
        if (searchBean == null)
            return resultBean;
        else {
            List<ReconciliationResultRow> rows = resultBean.getRows();
            if (searchBean.getSearchCase() != null) {
                List<ReconciliationResultRow> filteredRows = new ArrayList<ReconciliationResultRow>();
                for (ReconciliationResultRow row : rows) {
                    if (row.getCaseReconciliation().equals(searchBean.getSearchCase())) {
                        filteredRows.add(row);
                    }
                }
                rows = filteredRows;
            }
            if (org.springframework.util.StringUtils.hasText(searchBean.getSearchFieldName())
                    && org.springframework.util.StringUtils.hasText(searchBean.getSearchFieldValue())) {
                List<ReconciliationResultRow> filteredRows = new ArrayList<ReconciliationResultRow>();
                Integer searchIndex = null;
                for (int i = 0; i < resultBean.getHeader().getFields().size(); i++) {
                    ReconciliationResultField field = resultBean.getHeader().getFields().get(i);
                    if (field.getValues().get(0).equals(searchBean.getSearchFieldName())) {
                        searchIndex = i;
                    }
                }
                if (searchIndex != null) {
                    for (ReconciliationResultRow row : rows) {
                        ReconciliationResultField field = row.getFields().get(searchIndex);
                        for (String value : field.getValues()) {
                            if (value.equals(searchBean.getSearchFieldValue())) {
                                filteredRows.add(row);
                                break;
                            }
                        }
                    }
                }
                rows = filteredRows;
            }
            if (org.springframework.util.StringUtils.hasText(searchBean.getOrderBy())
                    && org.springframework.util.StringUtils.hasText(searchBean.getOrderByFieldName())) {
                Integer searchIndex = null;
                for (int i = 0; i < resultBean.getHeader().getFields().size(); i++) {
                    ReconciliationResultField field = resultBean.getHeader().getFields().get(i);
                    if (field.getValues().get(0).equals(searchBean.getOrderByFieldName())) {
                        searchIndex = i;
                    }
                }
                if (searchIndex != null) {
                    Collections.sort(rows,
                            new ReconcliationFieldComparatorByField(searchIndex, searchBean.getOrderBy()));
                }
            }

            int size = searchBean.getSize() < 10 ? 10 : searchBean.getSize();
            int pages = (rows.size() + (size - 1)) / size;
            int page = searchBean.getPageNumber() < 1 ? 1 : searchBean.getPageNumber();
            if (page > pages)
                page = pages;
            resultBean.setPagesNumber(pages);
            int startPos = (page - 1) * size;
            int endPos = page * size;
            endPos = endPos > rows.size() ? rows.size() : endPos;
            if (CollectionUtils.isEmpty(rows)) {
                endPos = 0;
                return resultBean;
            }
            {
                List<ReconciliationResultRow> filteredRow = new ArrayList<ReconciliationResultRow>();
                for (int i = startPos; i < endPos; i++) {
                    filteredRow.add(rows.get(i));
                }
                rows = filteredRow;
            }
            resultBean.setRows(rows);
        }
        return resultBean;
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

    private void getValuesForExtensibleUser(ExtensibleUser fromIDM, User user, List<AttributeMapEntity> attrMap,
            LoginEntity identity) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        try {
            bindingMap.put("user", new ProvisionUser(user));
            bindingMap.put("managedSysId", identity.getManagedSysId());
            final List<ManagedSystemObjectMatchEntity> matchList = managedSysService.managedSysObjectParam(
                    identity.getManagedSysId(), "USER");
            if (CollectionUtils.isNotEmpty(matchList)) {
                bindingMap.put("matchParam", objectMatchDozerConverter.convertToDTO(matchList.get(0), false));
            }

            // get all groups for user
            List<org.openiam.idm.srvc.grp.dto.Group> curGroupList = groupDozerConverter.convertToDTOList(
                    groupManager.getGroupsForUser(user.getId(), null, -1, -1), false);

            String decPassword = "";
            if (identity != null) {
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
            }

            // make the role and group list before these updates available to
            // the
            // attribute policies
            bindingMap.put("currentGroupList", curGroupList);
            for (AttributeMapEntity attr : attrMap) {
                fromIDM.getAttributes().add(
                        new ExtensibleAttribute(attr.getAttributeName(), (String) ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, scriptRunner)));
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                        && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    fromIDM.setPrincipalFieldName(attr.getAttributeName());
                }
            }

        } catch (Exception e) {
            log.error(e);
            // e.printStackTrace();
        }
    }

    /**
     * Updates the RunningTask list to show that a process is running
     * @param configId
     * @return
     */
    private ReconciliationResponse addTask(String configId) {

        ReconciliationResponse resp = new ReconciliationResponse(ResponseStatus.SUCCESS);
        synchronized (runningTask) {
            if(runningTask.contains(configId)) {

                resp = new ReconciliationResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_PROCESS_ALREADY_RUNNING);
                return resp;
            }
            runningTask.add(configId);
            return resp;
        }

    }

    private void endTask(String configID) {
        runningTask.remove(configID);
    }
}
