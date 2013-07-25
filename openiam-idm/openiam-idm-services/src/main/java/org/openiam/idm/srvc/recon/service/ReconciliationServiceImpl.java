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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.SearchRequest;
import org.openiam.connector.type.SearchResponse;
import org.openiam.connector.type.UserValue;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ReconciliationConfigDozerConverter;
import org.openiam.dozer.converter.ReconciliationSituationDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationReportCase;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultUtil;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.service.RemoteConnectorAdapter;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.openiam.spml2.msg.StatusCodeType;
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
public class ReconciliationServiceImpl implements ReconciliationService,
        MuleContextAware {
    @Autowired
    protected ReconciliationSituationDAO reconSituationDAO;

    @Autowired
    protected ReconciliationConfigDAO reconConfigDao;

    protected MuleContext muleContext;

    @Autowired
    protected LoginDataService loginManager;

    protected ProvisionService provisionService;

    @Autowired
    protected ResourceDataService resourceDataService;

    @Autowired
    protected UserDataService userMgr;
    @Autowired
    private MailService mailService;
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private ProvisionConnectorWebService connectorService;
    @Autowired
    private LoginDozerConverter loginDozerConverter;

    protected ConnectorAdapter connectorAdapter;
    protected RemoteConnectorAdapter remoteConnectorAdapter;

    @Autowired
    protected RoleDataService roleDataService;

    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private ReconciliationConfigDozerConverter reconConfigDozerMapper;
    @Autowired
    private ReconciliationSituationDozerConverter reconSituationDozerMapper;
    @Autowired
    protected CSVParser<User> userCSVParser;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    @Value("${iam.files.location}")
    private String absolutePath;
    private static final Log log = LogFactory
            .getLog(ReconciliationServiceImpl.class);

    public MuleContext getMuleContext() {
        return muleContext;
    }

    public ReconciliationConfig addConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        List<ReconciliationSituation> sitSet = null;
        if (!CollectionUtils.isEmpty(config.getSituationSet())) {
            sitSet = new ArrayList<ReconciliationSituation>(
                    config.getSituationSet());
        }
        config.setReconConfigId(null);
        ReconciliationConfig result = reconConfigDozerMapper.convertToDTO(
                reconConfigDao.add(reconConfigDozerMapper.convertToEntity(
                        config, false)), false);
        saveSituationSet(sitSet, result.getReconConfigId());
        result.setSituationSet(sitSet);
        return result;
    }

    @Transactional
    private void saveSituationSet(List<ReconciliationSituation> sitSet,
            String configId) {
        if (sitSet != null) {
            for (ReconciliationSituation s : sitSet) {
                if (StringUtils.isEmpty(s.getReconConfigId())) {
                    s.setReconConfigId(configId);
                }
                if (StringUtils.isEmpty(s.getReconSituationId())) {
                    s.setReconSituationId(null);
                    s.setReconSituationId(reconSituationDAO
                            .add(reconSituationDozerMapper.convertToEntity(s,
                                    false)).getReconSituationId());
                } else {
                    reconSituationDAO.update(reconSituationDozerMapper
                            .convertToEntity(s, false));
                }
            }
        }
    }

    @Transactional
    public void updateConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        List<ReconciliationSituation> sitSet = null;
        if (!CollectionUtils.isEmpty(config.getSituationSet())) {
            sitSet = new ArrayList<ReconciliationSituation>(
                    config.getSituationSet());
        }
        config.setSituationSet(null);

        reconConfigDao.update(reconConfigDozerMapper.convertToEntity(config,
                false));

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
        ReconciliationConfigEntity result = reconConfigDao
                .findByResourceId(resourceId);
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

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
    }

    public ReconciliationResponse startReconciliation(
            ReconciliationConfig config) {
        try {
            log.debug("Reconciliation started for configId="
                    + config.getReconConfigId() + " - resource="
                    + config.getResourceId());

            // have resource
            Resource res = resourceDataService.getResource(config
                    .getResourceId());
            String managedSysId = res.getManagedSysId();

            ManagedSysEntity mSys = managedSysService
                    .getManagedSysById(managedSysId);
            log.debug("ManagedSysId = " + managedSysId);
            log.debug("Getting identities for managedSys");
            // have situations
            Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
            for (ReconciliationSituation situation : config.getSituationSet()) {
                situations.put(situation.getSituation().trim(),
                        ReconciliationCommandFactory.createCommand(
                                situation.getSituationResp(), situation,
                                managedSysId));
                log.debug("Created Command for: " + situation.getSituation());
            }
            // have resource connector
            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(mSys.getConnectorId());

            // TODO check IF managed system is CSV, because we don't need to do
            // reconciliation into TargetSystem directional
            ManagedSysDto managedSysDto = managedSysDozerConverter
                    .convertToDTO(mSys, true);
            if (connector.getServiceUrl().contains("CSV")) {
                // Get user without fetches
                log.debug("Start recon");
                connectorAdapter.reconcileResource(managedSysDto, config,
                        muleContext);
                log.debug("end recon");
                return new ReconciliationResponse(ResponseStatus.SUCCESS);
            }
            ReconciliationResultBean resultBean = new ReconciliationResultBean();
            List<AttributeMapEntity> attrMap = managedSysService
                    .getResourceAttributeMaps(mSys.getResourceId());
            resultBean.setObjectType("USER");
            resultBean.setRows(new ArrayList<ReconciliationResultRow>());
            resultBean.getRows().add(
                    ReconciliationResultUtil
                            .setHeaderInReconciliationResult(attrMap));

            // initialization match parameters of connector
            List<ManagedSystemObjectMatchEntity> matchObjAry = managedSysService
                    .managedSysObjectParam(managedSysId, "USER");
            // execute all Reconciliation Commands need to be check
            if (CollectionUtils.isEmpty(matchObjAry)) {
                log.error("No match object found for this managed sys");
                return new ReconciliationResponse(ResponseStatus.FAILURE);
            }
            String keyField = matchObjAry.get(0).getKeyField();
            String baseDnField = matchObjAry.get(0).getBaseDn();

            // 1. Do reconciliation users from IDM to Target Managed System
            // search for all Roles and Groups related with resource
            // Get List with all users who have Identity if this resource
            List<LoginEntity> idmIdentities = loginManager
                    .getAllLoginByManagedSys(managedSysId);
            for (LoginEntity identity : idmIdentities) {
                reconciliationIDMUserToTargetSys(resultBean, attrMap, identity,
                        mSys, situations);
            }

            // 2. Do reconciliation users from Target Managed System to IDM
            // search for all Roles and Groups related with resource
            // GET Users from ConnectorAdapter by BaseDN and query rules
            processingTargetToIDM(resultBean, attrMap, config, managedSysId,
                    mSys, situations, connector, keyField, baseDnField);
            this.saveReconciliationResults(config.getResourceId(), resultBean);
            this.sendMail(config, res);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            ReconciliationResponse resp = new ReconciliationResponse(
                    ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
            return resp;
        }

        return new ReconciliationResponse(ResponseStatus.SUCCESS);
    }

    private ReconciliationResponse processingTargetToIDM(
            ReconciliationResultBean resultBean,
            List<AttributeMapEntity> attrMap, ReconciliationConfig config,
            String managedSysId, ManagedSysEntity mSys,
            Map<String, ReconciliationCommand> situations,
            ProvisionConnectorDto connector, String keyField, String baseDnField)
            throws ScriptEngineException {
        // FIXME targetSystemMatchScript is mandatory?
        if (config == null
                || StringUtils.isEmpty(config.getTargetSystemMatchScript())) {
            log.error("SearchQuery not defined for this reconciliation config.");
            return new ReconciliationResponse(ResponseStatus.FAILURE);
        }
        String searchQuery = (String) scriptRunner.execute(
                new HashMap<String, Object>(),
                config.getTargetSystemMatchScript());
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
        searchRequest.setHostPort(mSys.getPort().toString());
        searchRequest.setHostLoginId(mSys.getUserId());
        searchRequest.setHostLoginPassword(mSys.getPswd());

        SearchResponse searchResponse;

        if (connector.getConnectorInterface() != null
                && connector.getConnectorInterface().equalsIgnoreCase("REMOTE")) {
            log.debug("Calling reconcileResource with Remote connector");
            searchResponse = remoteConnectorAdapter.search(searchRequest,
                    connector, muleContext);

        } else {
            log.debug("Calling reconcileResource with Local connector");
            searchResponse = connectorAdapter.search(searchRequest, connector,
                    muleContext);
        }
        if (searchResponse != null
                && searchResponse.getStatus() == StatusCodeType.SUCCESS) {
            List<UserValue> usersFromRemoteSys = searchResponse.getUserList();
            if (usersFromRemoteSys != null) {
                for (UserValue userValue : usersFromRemoteSys) {
                    List<ExtensibleAttribute> extensibleAttributes = userValue
                            .getAttributeList() != null ? userValue
                            .getAttributeList()
                            : new LinkedList<ExtensibleAttribute>();
                    reconcilationTargetUserObjectToIDM(resultBean, attrMap,
                            managedSysId, mSys, situations, keyField,
                            extensibleAttributes);
                }
            }
        } else {
            log.debug(searchResponse.getErrorMessage());
        }
        return new ReconciliationResponse(ResponseStatus.FAILURE);
    }

    // Reconciliation processingTargetToIDM
    private void reconcilationTargetUserObjectToIDM(
            ReconciliationResultBean resultBean,
            List<AttributeMapEntity> attrMap, String managedSysId,
            ManagedSysEntity mSys,
            Map<String, ReconciliationCommand> situations, String keyField,
            List<ExtensibleAttribute> extensibleAttributes) {
        String targetUserPrincipal = null;
        for (ExtensibleAttribute attr : extensibleAttributes) {
            // search principal attribute by KeyField
            // (matchObjAry[0].getKeyField();)
            if (attr.getName().equals(keyField)) {
                targetUserPrincipal = attr.getValue();
                break;
            }
        }
        // check if principal attribute found
        if (StringUtils.isNotEmpty(targetUserPrincipal)) {
            log.debug("reconcile principle found=> [" + keyField + "="
                    + targetUserPrincipal + "]");
            // if principal attribute exists in user attributes from target
            // system
            // we need to define Command
            // try to find user in IDM by login=principal
            LoginEntity login = loginManager.getLoginByManagedSys(
                    mSys.getDomainId(), targetUserPrincipal, managedSysId);
            LoginEntity idmLogin = loginManager.getLoginByManagedSys(
                    mSys.getDomainId(), targetUserPrincipal, "0");
            boolean identityExistsInIDM = login != null;

            if (!identityExistsInIDM) {
                // user doesn't exists in IDM

                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getRows()
                                .get(0), attrMap, null, extensibleAttributes,
                                ReconciliationReportCase.NOT_EXIST_IN_IDM_DB));
                // SYS_EXISTS__IDM_NOT_EXISTS
                ReconciliationCommand command = situations
                        .get(ReconciliationCommand.SYS_EXISTS__IDM_NOT_EXISTS);
                if (command != null) {
                    Login l = new Login();
                    l.setDomainId(mSys.getDomainId());
                    l.setLogin(targetUserPrincipal);
                    l.setManagedSysId(managedSysId);

                    ProvisionUser newUser = new ProvisionUser();
                    // ADD Target user principal
                    newUser.getPrincipalList().add(l);
                    if (idmLogin != null) {
                        newUser.getPrincipalList().add(
                                loginDozerConverter
                                        .convertToDTO(idmLogin, true));
                    }
                    log.debug("Call command for Match Found");
                    command.execute(l, newUser, extensibleAttributes);
                }
            }
        }
    }

    private boolean reconciliationIDMUserToTargetSys(
            ReconciliationResultBean resultBean,
            List<AttributeMapEntity> attrMap, final LoginEntity identity,
            final ManagedSysEntity mSys,
            final Map<String, ReconciliationCommand> situations) {

        User user = userMgr.getUserDto(identity.getUserId());
        Login idDto = loginDozerConverter.convertToDTO(identity, true);
        log.debug("1 Reconciliation for user " + user);

        String principal = identity.getLogin();
        log.debug("looking up identity in resource: " + principal);

        LookupUserResponse lookupResp = provisionService.getTargetSystemUser(
                principal, mSys.getManagedSysId());

        log.debug("Lookup status for " + principal + " ="
                + lookupResp.getStatus());

        boolean userFoundInTargetSystem = lookupResp.getStatus() == ResponseStatus.SUCCESS;

        List<ExtensibleAttribute> extensibleAttributes = lookupResp
                .getAttrList() != null ? lookupResp.getAttrList()
                : new LinkedList<ExtensibleAttribute>();

        if (userFoundInTargetSystem) {
            // Record exists in resource
            if (user.getStatus().equals(UserStatusEnum.DELETED)) {
                // IDM_DELETED__SYS_EXISTS
                ReconciliationCommand command = situations
                        .get(ReconciliationCommand.IDM_DELETED__SYS_EXISTS);
                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getRows()
                                .get(0), attrMap, userCSVParser
                                .toReconciliationObject(user, attrMap), null,
                                ReconciliationReportCase.IDM_DELETED));
                if (command != null) {
                    log.debug("Call command for: Record in resource but deleted in IDM");
                    command.execute(idDto, new ProvisionUser(user),
                            extensibleAttributes);
                }
            } else {
                // IDM_EXISTS__SYS_EXISTS
                ReconciliationCommand command = situations
                        .get(ReconciliationCommand.IDM_EXISTS__SYS_EXISTS);
                resultBean.getRows().add(
                        this.setRowInReconciliationResult(resultBean.getRows()
                                .get(0), attrMap, userCSVParser
                                .toReconciliationObject(user, attrMap),
                                extensibleAttributes,
                                ReconciliationReportCase.MATCH_FOUND));
                if (command != null) {
                    log.debug("Call command for: Record in resource and in IDM");
                    command.execute(idDto, new ProvisionUser(user),
                            extensibleAttributes);
                }
            }

        } else {
            // Record not found in resource
            if (!user.getStatus().equals(UserStatusEnum.DELETED)) {
                // IDM_EXISTS__SYS_NOT_EXISTS
                ReconciliationCommand command = situations
                        .get(ReconciliationCommand.IDM_EXISTS__SYS_NOT_EXISTS);
                resultBean
                        .getRows()
                        .add(this.setRowInReconciliationResult(resultBean
                                .getRows().get(0), attrMap, userCSVParser
                                .toReconciliationObject(user, attrMap),
                                extensibleAttributes,
                                ReconciliationReportCase.NOT_EXIST_IN_RESOURCE));
                if (command != null) {
                    log.debug("Call command for: Record in resource and in IDM");
                    command.execute(idDto, new ProvisionUser(user),
                            extensibleAttributes);
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

    public void setConnectorService(
            ProvisionConnectorWebService connectorService) {
        this.connectorService = connectorService;
    }

    public void setConnectorAdapter(ConnectorAdapter connectorAdapter) {
        this.connectorAdapter = connectorAdapter;
    }

    public void setRemoteConnectorAdapter(
            RemoteConnectorAdapter remoteConnectorAdapter) {
        this.remoteConnectorAdapter = remoteConnectorAdapter;
    }

    // private ReconciliationResultRow setRowInReconciliationResult(
    // ReconciliationResultRow headerRow,
    // List<AttributeMapEntity> attrMapList,
    // ReconciliationObject<User> currentObject,
    // ReconciliationObject<User> findedObject,
    // ReconciliationReportCase caseReconciliation) {
    // ReconciliationResultRow row = new ReconciliationResultRow();
    //
    // Map<String, ReconciliationResultField> resultMap = null;
    // if (currentObject != null && findedObject != null) {
    // resultMap = userCSVParser.matchFields(attrMapList, currentObject,
    // findedObject);
    // if (!MapUtils.isEmpty(resultMap)) {
    // for (ReconciliationResultField field : resultMap.values())
    // if (field.getValues().size() > 1) {
    // caseReconciliation = ReconciliationReportCase.MATCH_FOUND_DIFFERENT;
    // break;
    // }
    // }
    // } else if (currentObject != null) {
    // resultMap = userCSVParser.convertToMap(attrMapList, currentObject);
    // }
    // row.setCaseReconciliation(caseReconciliation);
    // List<ReconciliationResultField> fieldList = new
    // ArrayList<ReconciliationResultField>();
    // if (!MapUtils.isEmpty(resultMap)) {
    // for (ReconciliationResultField field : headerRow.getFields()) {
    // ReconciliationResultField value = resultMap.get(field
    // .getValues().get(0));
    // if (value == null)
    // continue;
    // ReconciliationResultField newField = new ReconciliationResultField();
    // newField.setDisplayOrder(field.getDisplayOrder());
    // newField.setValues(value.getValues());
    // fieldList.add(newField);
    // }
    // }
    // row.setFields(fieldList);
    // return row;
    // }

    private ReconciliationResultRow setRowInReconciliationResult(
            ReconciliationResultRow headerRow,
            List<AttributeMapEntity> attrMapList,
            ReconciliationObject<User> currentObject,
            List<ExtensibleAttribute> findedObject,
            ReconciliationReportCase caseReconciliation) {
        ReconciliationResultRow row = new ReconciliationResultRow();

        Map<String, ReconciliationResultField> user1Map = null;
        Map<String, ReconciliationResultField> user2Map = null;
        if (currentObject != null) {
            user1Map = userCSVParser.convertToMap(attrMapList, currentObject);
        }
        if (findedObject != null) {
            user2Map = new HashMap<String, ReconciliationResultField>();
            for (ExtensibleAttribute a : findedObject) {
                ReconciliationResultField field = new ReconciliationResultField();
                if (a.isMultivalued()) {
                    field.setValues(a.getValueList());
                } else {
                    field.setValues(Arrays.asList(a.getValue()));
                }
                user2Map.put(a.getName(), field);
            }
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
                    row.setCaseReconciliation(ReconciliationReportCase.MATCH_FOUND_DIFFERENT);
                    value1.getValues().addAll(value2.getValues());
                    user1Map.put(key, value1);
                }
            }
            row.setFields(this.setFromReconcilationUser(user1Map, headerRow));
        }

        return row;
    }

    private List<ReconciliationResultField> setFromReconcilationUser(
            Map<String, ReconciliationResultField> user,
            ReconciliationResultRow headerRow) {
        List<ReconciliationResultField> fieldList = new ArrayList<ReconciliationResultField>();
        for (ReconciliationResultField field : headerRow.getFields()) {
            ReconciliationResultField value = user
                    .get(field.getValues().get(0));
            if (value == null)
                continue;
            ReconciliationResultField newField = new ReconciliationResultField();
            newField.setDisplayOrder(field.getDisplayOrder());
            newField.setValues(value.getValues());
            fieldList.add(newField);
        }
        return fieldList;
    }

    @Override
    public String getReconciliationReport(ReconciliationConfig config,
            String reportType) {
        String fileName = StringUtils.isEmpty(config.getResourceId()) ? ""
                : config.getResourceId() + ".rcndat";
        if (StringUtils.isEmpty(fileName))
            return null;
        ReconciliationResultBean r = null;
        try {
            r = (ReconciliationResultBean) Serializer.deserializer(absolutePath
                    + fileName);
        } catch (Exception e) {
            return "";
        }
        if (r == null)
            return "";
        if ("HTML".equalsIgnoreCase(reportType)
                || StringUtils.isEmpty(reportType)) {
            return r.toHTML();
        } else {
            return r.toCSV();
        }
    }

    private void saveReconciliationResults(String fileName,
            ReconciliationResultBean resultBean) {
        Serializer.serialize(resultBean, absolutePath + fileName + ".rcndat");
    }

    private void sendMail(ReconciliationConfig config, Resource res) {
        StringBuilder message = new StringBuilder();
        if (!StringUtils.isEmpty(config.getNotificationEmailAddress())) {
            message.append("Resource: " + res.getName() + ".\n");
            message.append("Uploaded CSV file: " + res.getResourceId()
                    + ".csv was successfully reconciled.\n");
            mailService.sendEmails(null,
                    new String[] { config.getNotificationEmailAddress() },
                    null, null, "CSVConnector", message.toString(), false,
                    new String[] {});
        }
    }

    @Override
    public ReconciliationResultBean getReconciliationResult(
            ReconciliationConfig config) {
        if (config == null || config.getResourceId() == null)
            return null;
        return (ReconciliationResultBean) Serializer.deserializer(absolutePath
                + config.getResourceId() + ".rcndat");
    }
}
