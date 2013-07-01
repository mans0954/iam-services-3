package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.script.ScriptFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for providing report data
 *
 * @author vitaly.yakunin
 */
@Service
public class ReportDataServiceImpl implements ReportDataService {
 
    private static final String scriptEngine = "org.openiam.script.GroovyScriptEngineIntegration";

    @Autowired
    private ReportInfoDao reportDao;
    @Autowired
    private ReportSubscriptionDao reportSubscriptionDao;
    @Autowired
    private ReportCriteriaParamDao criteriaParamDao;
    @Autowired
    private ReportSubCriteriaParamDao subCriteriaParamDao;
    @Autowired
    private ReportParamTypeDao reportParamTypeDao;
    @Override
    @Transactional(readOnly = true)
    public ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException, IOException {
        ReportInfoEntity reportInfo = reportDao.findByName(reportName);
        if (reportInfo == null) {
            throw new IllegalArgumentException("Invalid parameter list: report with name=" + reportName + " was not found in Database");
        }

        ScriptIntegration se = ScriptFactory.createModule(scriptEngine);
        ReportDataSetBuilder dataSourceBuilder = (ReportDataSetBuilder) se.instantiateClass(Collections.EMPTY_MAP, "/reports/" + reportInfo.getDatasourceFilePath());

        return dataSourceBuilder.getReportData(reportParams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportInfoEntity> getAllReports() {
        return reportDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportInfoEntity getReportByName(String name) {
        return reportDao.findByName(name);
    }

    @Override
    @Transactional
    public void createOrUpdateReportInfo(final String reportName, final String reportDataSource, final String reportUrl) {
        reportDao.createOrUpdateReportInfo(reportName, reportDataSource, reportUrl);
        List<ReportCriteriaParamEntity> paramEntitiesSrc = criteriaParamDao.findByReportInfoName(reportName);
        for(ReportCriteriaParamEntity paramEntity : paramEntitiesSrc) {
            criteriaParamDao.delete(paramEntity);
        }
    }

    @Override
    @Transactional
    public void updateReportParametersByReportName(final String reportName, final List<ReportCriteriaParamEntity> parameters) {
        criteriaParamDao.save(parameters);
    }
    
    @Override
    @Transactional
    public void updateSubReportParametersByReportName(final String reportName, final List<ReportSubCriteriaParamEntity> parameters) {
    	subCriteriaParamDao.save(parameters);
    }


    @Override
    @Transactional
    public List<ReportCriteriaParamEntity> getReportParametersByReportId(String reportId) {
        return criteriaParamDao.findByReportInfoId(reportId);
    }
    
    @Override
    @Transactional
    public List<ReportCriteriaParamEntity> getReportParametersByReportName(String reportName) {
        return criteriaParamDao.findByReportInfoName(reportName);
    }

    @Override
    @Transactional
    public List<ReportSubCriteriaParamEntity> getSubReportParametersByReportName(String reportName) {
        return subCriteriaParamDao.findByReportInfoName(reportName);
    }

    @Override
    @Transactional
    public List<ReportParamTypeEntity> getReportParameterTypes() {
        return reportParamTypeDao.findAll();
    }
    
    @Override
    @Transactional
    public void createOrUpdateSubscribedReportInfo(ReportSubscriptionEntity reportSubscriptionEntity){
    	reportSubscriptionDao.createOrUpdateSubscribedReportInfo(reportSubscriptionEntity);
        List<ReportSubCriteriaParamEntity> paramEntitiesSrc = subCriteriaParamDao.findByReportInfoName(reportSubscriptionEntity.getReportName());
        for(ReportSubCriteriaParamEntity paramEntity : paramEntitiesSrc) {
        	subCriteriaParamDao.delete(paramEntity);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportSubscriptionEntity> getAllActiveSubscribedReports() {
        return reportSubscriptionDao.getAllActiveSubscribedReports();
    }    
    @Override
    @Transactional(readOnly = true)
    public List<ReportSubscriptionEntity> getAllSubscribedReports() {
        return reportSubscriptionDao.findAll();
    }    
}
