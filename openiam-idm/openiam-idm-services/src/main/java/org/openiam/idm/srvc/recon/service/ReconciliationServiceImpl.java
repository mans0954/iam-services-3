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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.*;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.parser.csv.UserSearchBeanCSVParser;
import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.domain.ReconciliationSituationEntity;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconcliationFieldComparatorByField;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    protected ResourceDataService resourceDataService;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter objectMatchDozerConverter;
    @Autowired
    protected ManagedSystemService managedSysService;
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
    private ReconciliationConfigDozerConverter reconConfigDozerMapper;
    @Autowired
    private ReconciliationSituationDozerConverter reconSituationDozerMapper;
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
    protected MatchRuleFactory matchRuleFactory;
    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    private ReconciliationUserProcessor userProcessor;
    @Autowired
    private ReconciliationGroupProcessor groupProcessor;

    private static final Log log = LogFactory.getLog(ReconciliationServiceImpl.class);

    /*
    * The flags for the running tasks are handled by this Thread-Safe Set.
    * It stores the taskIds of the currently executing tasks.
    * This is faster and as reliable as storing the flags in the database,
    * if the tasks are only launched from ONE host in a clustered environment.
    * It is unique for each class-loader, which means unique per war-deployment.
    */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());

    @Transactional
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

        ReconciliationConfigEntity configEntity = reconConfigDozerMapper.convertToEntity(config, false);

        for (ReconciliationSituation s : config.getSituationSet()) {
            if (StringUtils.isEmpty(s.getReconConfigId())) {
                s.setReconConfigId(configEntity.getReconConfigId());
            }
            ReconciliationSituationEntity situationEntity;
            if (StringUtils.isEmpty(s.getReconSituationId())) {
                situationEntity = reconSituationDozerMapper.convertToEntity(s, false);
                reconSituationDAO.save(situationEntity);
            } else {
                situationEntity = reconSituationDAO.findById(s.getReconSituationId());
                situationEntity.setScript(s.getScript());
                situationEntity.setSituation(s.getSituation());
                situationEntity.setSituationResp(s.getSituationResp());
                situationEntity.setCustomCommandScript(s.getCustomCommandScript());
                reconSituationDAO.save(situationEntity);
            }
            configEntity.getSituationSet().add(situationEntity);
        }

        reconConfigDao.update(configEntity);

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
    public ReconciliationConfig getConfigByResourceByType(String resourceId, String type) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfigDao.findByResourceIdByType(resourceId, type);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ReconciliationConfig> findReconConfig(ReconConfigSearchBean searchBean, int from, int size) {
        List<ReconciliationConfigEntity> reconciliationConfigEntities = reconConfigDao.getByExample(searchBean, from, size);
        if(reconciliationConfigEntities == null) {
            return Collections.EMPTY_LIST;
        }
        return reconConfigDozerMapper.convertToDTOList(reconciliationConfigEntities, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int countReconConfig(final ReconConfigSearchBean searchBean) {
        return reconConfigDao.count(searchBean);
    }

    @Transactional(readOnly = true)
    public List<ReconciliationConfig> getConfigsByResource(final String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        List<ReconciliationConfigEntity> result = reconConfigDao.findByResourceId(resourceId);
        if (result == null)
            return new LinkedList<ReconciliationConfig>();
        else
            return reconConfigDozerMapper.convertToDTOList(result, false);
    }

    @Transactional(readOnly = true)
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

    @Transactional(propagation = Propagation.REQUIRED)
    public ReconciliationResponse startReconciliation(ReconciliationConfig config) {

        ReconciliationConfigEntity configEntity = reconConfigDao.findById(config.getReconConfigId());

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
        ReconciliationResponse reconciliationResponse = null;
        try {
            log.debug("Reconciliation started for configId=" + config.getReconConfigId() + " - resource="
                    + config.getResourceId());

            configEntity.setExecStatus(ReconExecStatusOptions.STARTED);

            // Check custom Processor script and execute if exists
            if(StringUtils.isNotEmpty(config.getCustomProcessorScript())) {
                // fill map with attributes if needed
                final Map<String, Object> objectMap = new HashMap<String, Object>();

                ReconciliationProcessor processor = (ReconciliationProcessor)scriptRunner.instantiateClass(objectMap, config.getCustomProcessorScript());
                if(processor == null) {
                    throw new FileNotFoundException("The ReconciliationProcessor script '"+config.getCustomProcessorScript()+"' wasn't found. Please check the configuration.");
                }
                reconciliationResponse = processor.startReconciliation(config, idmAuditLog);
            } else {
                if("USER".equalsIgnoreCase(config.getReconType())) {
                    reconciliationResponse = userProcessor.startReconciliation(config, idmAuditLog);
                } else if("GROUP".equalsIgnoreCase(config.getReconType())) {
                    reconciliationResponse = groupProcessor.startReconciliation(config, idmAuditLog);
                }
            }

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
            if(reconciliationResponse == null || configEntity.getExecStatus() == ReconExecStatusOptions.STARTED) {
                configEntity.setExecStatus(ReconExecStatusOptions.FINISHED);
            }
            reconConfigDao.save(configEntity);
        }

        return new ReconciliationResponse(ResponseStatus.SUCCESS);
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
