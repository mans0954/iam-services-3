package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.idm.srvc.report.dto.ReportTable;
/**
 * Report data service provides transactional
 *
 * @author vitaly.yakunin
 */
public interface ReportDataService {
    ReportDataDto getReportData(final String reportName, final Map<String, String> reportParams) throws ClassNotFoundException, ScriptEngineException, IOException;
    List<ReportInfoEntity> getAllReports(final int from, final int size);
    Integer getReportCount();
    Integer getSubscribedReportCount();
    Integer getSubCriteriaParamReportCount();
    List<ReportSubscriptionEntity> getAllSubscribedReports();
    List<ReportSubCriteriaParamEntity> getAllSubCriteriaParamReports();
    List<ReportSubscriptionEntity> getAllActiveSubscribedReports();
    ReportInfoEntity getReportByName(String name);
    void deleteReport(String reportId);
    ReportInfoEntity getReport(String reportId);
    ReportInfoEntity createOrUpdateReportInfo(ReportInfoEntity report);
    ReportCriteriaParamEntity getReportParameterByName(String reportId, String paramName);
    ReportCriteriaParamEntity createOrUpdateReportParamInfo(ReportCriteriaParamEntity reportParam);
    void deleteReportParam(String reportParamId);
    ReportSubscriptionEntity createOrUpdateSubscribedReportInfo(ReportSubscriptionEntity reportSubscriptionEntity);
    List<ReportCriteriaParamEntity> getReportParametersByReportId(String reportId);
    List<ReportCriteriaParamEntity> getReportParametersByReportName(String reportName);    
    List<ReportSubCriteriaParamEntity> getSubReportParametersByReportName(String reportName);    
    void updateReportParametersByReportName(String reportName, List<ReportCriteriaParamEntity> prameters);
    void updateSubReportParametersByReportName(String reportName, List<ReportSubCriteriaParamEntity> prameters);
    List<ReportParamTypeEntity> getReportParameterTypes();
    void deleteSubscribedReport(String reportId);
    void deleteSubCriteriaParamReport(String reportId);
    ReportSubscriptionEntity getSubscriptionReportById(String reportId);
    ReportSubCriteriaParamEntity getSubCriteriaParamReportById(String reportId);
    List<ReportSubCriteriaParamEntity> getAllSubCriteriaParamReport(String reportId);
    
    ReportSubCriteriaParamEntity createOrUpdateSubCriteriaParamReport(ReportSubCriteriaParamEntity entity);
    
}