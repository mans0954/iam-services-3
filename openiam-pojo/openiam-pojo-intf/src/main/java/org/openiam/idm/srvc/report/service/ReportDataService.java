package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.List;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.report.dto.*;

/**
 * Report data service provides transactional
 *
 * @author vitaly.yakunin
 */
public interface ReportDataService {
	ReportDataDto getReportData(ReportQueryDto reportQuery) throws ClassNotFoundException, ScriptEngineException, IOException;
	List<ReportInfoDto> getReports(final int from, final int size);
	Integer getReportCount();
	List<ReportInfoDto> getAllReports(String requestorId);
	Integer getSubscribedReportCount();
	Integer getSubCriteriaParamReportCount();
	List<ReportSubscriptionDto> getAllSubscribedReports();
	List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReports();
	List<ReportSubscriptionDto> getAllActiveSubscribedReports();
	ReportInfoDto getReportByName(String name);
	void deleteReport(String reportId);
	ReportInfoDto getReport(String reportId);
	String createOrUpdateReportInfo(ReportInfoDto report);
	ReportCriteriaParamDto getReportParameterByName(String reportId, String paramName);
	String createOrUpdateReportParamInfo(ReportCriteriaParamDto reportParam);
	void deleteReportParam(String reportParamId);
	String createOrUpdateSubscribedReportInfo(ReportSubscriptionDto reportSubscription);
	List<ReportCriteriaParamDto> getReportParametersByReportId(String reportId);
	List<ReportCriteriaParamDto> getReportParametersByReportName(String reportName);
	List<ReportSubCriteriaParamDto> getSubReportParametersByReportId(String reportId);
	List<ReportParamTypeDto> getReportParameterTypes();
	List<ReportParamMetaTypeDto> getReportParamMetaTypes();
	void deleteSubscribedReport(String reportId);
	void deleteSubCriteriaParamReport(String reportId);
	ReportSubscriptionDto getSubscriptionReportById(String reportId);
	ReportCriteriaParamDto getReportCriteriaParamById(String rcpId);
	List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReport(String reportId);
	String createOrUpdateSubCriteriaParamReport(ReportSubCriteriaParamDto param);
	List<ReportCriteriaParamDto> getAllReportParameters();

}