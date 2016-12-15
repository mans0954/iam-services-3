package org.openiam.idm.srvc.report.service;

import java.io.IOException;
import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.ReportSearchBean;
import org.openiam.idm.srvc.report.dto.*;

/**
 * Report data service provides transactional
 *
 * @author vitaly.yakunin
 */
public interface ReportDataService {
	ReportDataDto getReportData(ReportQueryDto reportQuery) throws BasicDataServiceException;
	String getReportUrl(ReportQueryDto reportQuery, String taskName, String reportBaseUrl, String locale) throws BasicDataServiceException;
	String createOrUpdateReportInfo(ReportInfoDto report) throws BasicDataServiceException;
	void validate(ReportInfoDto report) throws BasicDataServiceException;
	String createOrUpdateReportParamInfo(ReportCriteriaParamDto reportParam) throws BasicDataServiceException;
	List<ReportCriteriaParamDto> getReportParametersByReportId(String reportId) throws BasicDataServiceException;
	List<ReportCriteriaParamDto> getReportParametersByReportName(String reportName) throws BasicDataServiceException;
	ReportInfoDto getReportByName(String name)throws BasicDataServiceException;
	ReportInfoDto getReport(String reportId)throws BasicDataServiceException;
	void deleteReportParam(String reportParamId)throws BasicDataServiceException;
	void deleteReport(String reportId)throws BasicDataServiceException;
	List<ReportSubCriteriaParamDto> getAllSubCriteriaParamReports(ReportSearchBean searchBean);
	Integer getSubCriteriaParamReportCount();
	void deleteSubCriteriaParamReport(String reportId) throws BasicDataServiceException;
	String createOrUpdateSubCriteriaParamReport(ReportSubCriteriaParamDto param)throws BasicDataServiceException;
	List<ReportInfoDto> getReports(final int from, final int size);
	Integer getReportCount();
	List<ReportInfoDto> getAllReports(String requestorId);
	List<ReportParamTypeDto> getReportParameterTypes();
	List<ReportParamMetaTypeDto> getReportParamMetaTypes();
	ReportCriteriaParamDto getReportParameterByName(String reportId, String paramName);
	List<ReportCriteriaParamDto> getAllReportParameters();
}