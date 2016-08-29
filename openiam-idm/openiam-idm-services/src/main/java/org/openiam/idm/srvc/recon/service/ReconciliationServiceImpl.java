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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.parser.csv.UserSearchBeanCSVParser;
import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconcliationFieldComparatorByField;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.PrePostExecutor;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suneet
 * 
 */
@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    public static final String RECONCILIATION_CONFIG = "RECONCILIATION_CONFIG";
    @Autowired
    protected ReconciliationSituationDAO reconSituationDAO;

    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ResourceService resourceDataService;
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
	ReconciliationConfigService reconConfigService;

	@Autowired
	@Qualifier("reconciliationUserProcessor")
    private ReconciliationProcessor userProcessor;
    @Autowired
	@Qualifier("reconciliationGroupProcessor")
    private ReconciliationProcessor groupProcessor;

    private static final Log log = LogFactory.getLog(ReconciliationServiceImpl.class);

    /*
    * The flags for the running tasks are handled by this Thread-Safe Set.
    * It stores the taskIds of the currently executing tasks.
    * This is faster and as reliable as storing the flags in the database,
    * if the tasks are only launched from ONE host in a clustered environment.
    * It is unique for each class-loader, which means unique per war-deployment.
    */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());

    public ReconciliationResponse startReconciliation(ReconciliationConfig config) {

		ReconciliationConfig reconConfig = reconConfigService.getConfigById(config.getId());

		IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(config.getRequesterId());
        idmAuditLog.setAction(AuditAction.RECONCILIATION.value());
        ManagedSysEntity managedSysEntity = managedSysService.getManagedSysById(config.getManagedSysId());
        idmAuditLog.setTargetManagedSys(config.getManagedSysId(), managedSysEntity.getName());
        idmAuditLog.setSource(config.getId());

        if ("INACTIVE".equalsIgnoreCase(config.getStatus())) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Reconciliation config is in 'INACTIVE' status");
            auditLogService.enqueue(idmAuditLog);
            ReconciliationResponse resp = new ReconciliationResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PROCESS_INACTIVE);
            return resp;
        }

        ReconciliationResponse processCheckResponse = addTask(config.getId());
        if ( processCheckResponse.getStatus() == ResponseStatus.FAILURE &&
                processCheckResponse.getErrorCode() == ResponseCode.FAIL_PROCESS_ALREADY_RUNNING) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Previous reconciliation run is not finished yet");
            auditLogService.enqueue(idmAuditLog);
            return processCheckResponse;
        }
		boolean reconFailed = false;
		ReconciliationResponse reconciliationResponse = null;
        try {
        	if(log.isDebugEnabled()) {
            log.debug("Reconciliation started for configId=" + config.getId() + " - resource="
                    + config.getResourceId());
        	}
			reconConfigService.updateExecStatus(reconConfig.getId(), ReconExecStatusOptions.STARTED);

			Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put("RECONCILIATION_CONFIG", config);
            String preProcessScript = config.getPreProcessor();
            if (StringUtils.isNotEmpty(preProcessScript)) {
                PrePostExecutor ppScript = null;
                try {
                    ppScript = (PrePostExecutor) scriptRunner.instantiateClass(bindingMap, preProcessScript);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ppScript != null) {
                    ppScript.execute(bindingMap);
                }
            }

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
			reconciliationResponse = new ReconciliationResponse(ResponseStatus.FAILURE);
			reconciliationResponse.setErrorText(e.getMessage());
            reconFailed = true;

        } finally {
            endTask(config.getId());
            auditLogService.enqueue(idmAuditLog);

			ReconExecStatusOptions actualStatus = reconConfigService.getExecStatus(config.getId());
			final ReconExecStatusOptions execStatus =
					reconFailed ? ReconExecStatusOptions.FAILED :
					(actualStatus == ReconExecStatusOptions.STOPPING) ? ReconExecStatusOptions.STOPPED :
					(actualStatus == ReconExecStatusOptions.STARTED) ? ReconExecStatusOptions.FINISHED :
					null;
			if (execStatus != null) {
				reconConfigService.updateExecStatus(config.getId(), execStatus);
			}

            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put(RECONCILIATION_CONFIG, config);
            String postProcessScript = config.getPostProcessor();
            if (StringUtils.isNotEmpty(postProcessScript)) {
                PrePostExecutor ppScript = null;
                try {
                    ppScript = (PrePostExecutor) scriptRunner.instantiateClass(bindingMap, postProcessScript);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ppScript != null) {
                    ppScript.execute(bindingMap);
                }
            }
        }

        return reconciliationResponse;
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
