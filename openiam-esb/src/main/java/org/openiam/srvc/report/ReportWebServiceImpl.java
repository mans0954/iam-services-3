package org.openiam.srvc.report;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.ReportRequest;
import org.openiam.base.response.*;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ReportSearchBean;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportQueryDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.mq.constants.api.common.ReportAPI;
import org.openiam.mq.constants.queue.common.ReportQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WS for report system
 *
 * @author vitaly.yakunin
 */

@WebService(endpointInterface = "org.openiam.srvc.report.ReportWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/report/service",
		portName = "ReportWebServicePort",
		serviceName = "ReportWebService")
@Service("reportWS")
public class ReportWebServiceImpl extends AbstractApiService implements ReportWebService {
	
	@Autowired
	public ReportWebServiceImpl(ReportQueue queue) {
		super(queue);
	}


	@Override
	public GetReportDataResponse executeQuery(final ReportQueryDto reportQuery) {
		ReportRequest request = new ReportRequest();
		request.setReportQuery(reportQuery);
		return this.getResponse(ReportAPI.ExecuteQueue, request, GetReportDataResponse.class);
	}

	@Override
	public String getReportUrl(final ReportQueryDto reportQuery, final String taskName, final String reportBaseUrl, String locale) {
		ReportRequest request = new ReportRequest();
		request.setReportQuery(reportQuery);
		request.setTaskName(taskName);
		request.setReportBaseUrl(reportBaseUrl);
		request.setLocale(locale);
		return this.getValue(ReportAPI.GetReportUrl, request, StringResponse.class);
	}

	@Override
	public GetAllReportsResponse getReports(final int from, final int size) {
		BaseSearchServiceRequest request = new BaseSearchServiceRequest();
		request.setFrom(from);
		request.setSize(size);
		return this.getResponse(ReportAPI.GetReports, request, GetAllReportsResponse.class);
	}

	@Override
	public Integer getReportCount() {
		return this.getIntValue(ReportAPI.GetReportCount, new BaseSearchServiceRequest());

	}

	@Override
	public GetAllReportsResponse getReportsByUserId(String requestorId) {
		BaseSearchServiceRequest request = new BaseSearchServiceRequest();
		request.setRequesterId(requestorId);
		return this.getResponse(ReportAPI.GetReportsByUserId, request, GetAllReportsResponse.class);
	}

	@Override
	public Response createOrUpdateReportInfo(ReportInfoDto report) {
		return this.manageCrudApiRequest(ReportAPI.Save, report);
	}

	@Override
	public Response validateUpdateReportInfo(ReportInfoDto report) {
		return this.manageCrudApiRequest(ReportAPI.Validate, report);
	}

	@Override
	public Response createOrUpdateReportInfoParam(final ReportCriteriaParamDto reportParam) {
		return this.manageCrudApiRequest(ReportAPI.SaveReportParam, reportParam);
	}

	@Override
	public GetReportParametersResponse getReportParametersByReportId(String reportId) {
		ReportSearchBean searchBean = new ReportSearchBean();
		searchBean.addKey(reportId);
		return this.getResponse(ReportAPI.GetReportParametersByReportId, new BaseSearchServiceRequest<>(searchBean), GetReportParametersResponse.class);
	}

	@Override
	public GetReportParametersResponse getReportParametersByReportName(String reportName) {
		ReportSearchBean searchBean = new ReportSearchBean();
		searchBean.setReportName(reportName);
		return this.getResponse(ReportAPI.GetReportParametersByReportName, new BaseSearchServiceRequest<>(searchBean), GetReportParametersResponse.class);
	}

	@Override
	public GetReportInfoResponse getReportByName(String reportName) {
		ReportSearchBean searchBean = new ReportSearchBean();
		searchBean.setReportName(reportName);
		return this.getResponse(ReportAPI.GetReportByName, new BaseSearchServiceRequest<>(searchBean), GetReportInfoResponse.class);
	}

	@Override
	public GetReportInfoResponse getReport(String reportId) {
		ReportSearchBean searchBean = new ReportSearchBean();
		searchBean.addKey(reportId);
		return this.getResponse(ReportAPI.GetReport, new BaseSearchServiceRequest<>(searchBean), GetReportInfoResponse.class);
	}

	@Override
	public Response deleteReportParam(String reportParamId) {
		ReportCriteriaParamDto dto = new ReportCriteriaParamDto();
		dto.setId(reportParamId);
		return this.manageCrudApiRequest(ReportAPI.DeleteReportParam, dto);
	}

	@Override
	public Response deleteReport(String reportId) {
		ReportInfoDto dto = new ReportInfoDto();
		dto.setId(reportId);
		return this.manageCrudApiRequest(ReportAPI.DeleteReport, dto);
	}

	@Override
	public GetReportParameterTypesResponse getReportParameterTypes() {
		return this.getResponse(ReportAPI.GetReportParameterTypes, new EmptyServiceRequest(),GetReportParameterTypesResponse.class);
	}

	@Override
	public GetReportParameterMetaTypesResponse getReportParameterMetaTypes() {
		return this.getResponse(ReportAPI.GetReportParameterMetaTypes, new EmptyServiceRequest(),GetReportParameterMetaTypesResponse.class);
	}
	@Override
	public List<ReportCriteriaParamDto> getAllReportCriteriaParam(){
		GetReportParametersResponse response = this.getResponse(ReportAPI.GetAllReportCriteriaParam, new EmptyServiceRequest(), GetReportParametersResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getParameters();
	}
	@Override
	public GetAllSubCriteriaParamReportsResponse getSubCriteriaParamReports() {
		ReportSearchBean searchBean = new ReportSearchBean();
		return this.getResponse(ReportAPI.GetSubCriteriaParam, new BaseSearchServiceRequest<>(searchBean), GetAllSubCriteriaParamReportsResponse.class);
	}

	@Override
	public GetAllSubCriteriaParamReportsResponse getAllSubCriteriaParamReport(String reportId) {
		ReportSearchBean searchBean = new ReportSearchBean();
		searchBean.addKey(reportId);
		return this.getResponse(ReportAPI.GetSubCriteriaParam, new BaseSearchServiceRequest<>(searchBean), GetAllSubCriteriaParamReportsResponse.class);
	}

	@Override
	public Integer getSubCriteriaParamReportCount() {
		ReportSearchBean searchBean = new ReportSearchBean();
		return this.getIntValue(ReportAPI.CountSubCriteriaParam, new BaseSearchServiceRequest<>(searchBean));
	}

		@Override
	public Response deleteSubCriteriaParamReport(String reportId) {
		ReportSubCriteriaParamDto dto = new ReportSubCriteriaParamDto();
		dto.setReportId(reportId);
		return this.manageCrudApiRequest(ReportAPI.DeleteSubCriteriaParam, dto);
	}

	@Override
	public Response createOrUpdateSubCriteriaParam(final ReportSubCriteriaParamDto subCriteriaParamReport) {
		return this.manageCrudApiRequest(ReportAPI.SaveSubCriteriaParam, subCriteriaParamReport);
	}
}
