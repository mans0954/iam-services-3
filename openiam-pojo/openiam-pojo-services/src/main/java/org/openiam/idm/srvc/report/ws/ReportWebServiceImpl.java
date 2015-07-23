package org.openiam.idm.srvc.report.ws;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportParamMetaTypeDto;
import org.openiam.idm.srvc.report.dto.ReportParamTypeDto;
import org.openiam.idm.srvc.report.dto.ReportQueryDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;
import org.openiam.idm.srvc.report.service.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * WS for report system
 *
 * @author vitaly.yakunin
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.report.ws.ReportWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/report/service",
		portName = "ReportWebServicePort",
		serviceName = "ReportWebService")
@Service("reportWS")
public class ReportWebServiceImpl implements ReportWebService {

	private static final Log log = LogFactory
			.getLog(ReportWebServiceImpl.class);

	private static final String DEFAULT_REPORT_TASK = "frameset";
	private static final String REPORT_PARAMETER_NAME = "__report";
	private static final String LOCALE_PARAMETER_NAME = "__locale";

	@Autowired
	private ReportDataService reportDataService;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public GetReportDataResponse executeQuery(final ReportQueryDto reportQuery) {
		GetReportDataResponse response = new GetReportDataResponse();
		if (!StringUtils.isEmpty(reportQuery.getReportName())) {
			try {
				ReportDataDto reportDataDto = reportDataService.getReportData(reportQuery);

				response.setReportDataDto(reportDataDto);
			} catch (Throwable ex) {
				log.error(ex);
				response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
				response.setErrorText(ex.getMessage());
				response.setStatus(ResponseStatus.FAILURE);
			}
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportName="
					+ reportQuery.getReportName());
			response.setStatus(ResponseStatus.SUCCESS);
		}

		return response;
	}

	@Override
	public String getReportUrl(final ReportQueryDto reportQuery,
							   final String taskName, final String reportBaseUrl, String locale) {
		try {
			ReportInfoDto report = reportDataService.getReportByName(reportQuery.getReportName());
			if (report == null) {
				log.debug("Report couldn't be found. Report name = " + reportQuery.getReportName());
				return null;
			}
			String taskPath = StringUtils.isNotBlank(taskName) ? taskName : DEFAULT_REPORT_TASK;
			String reportDesignName = report.getReportUrl();
			URIBuilder uriBuilder = new URIBuilder(reportBaseUrl);
			uriBuilder.setPath(uriBuilder.getPath() + taskPath);
			uriBuilder.setParameter(REPORT_PARAMETER_NAME, reportDesignName);
			if (reportQuery.getQueryParams() != null) {
				for (Map.Entry<String, List<String>> entry : reportQuery.getQueryParams().entrySet()  ) {
					if (CollectionUtils.isNotEmpty(entry.getValue())) {
						for(String value : entry.getValue()) {
							uriBuilder.addParameter(entry.getKey(), value);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(locale)) {
				uriBuilder.setParameter(LOCALE_PARAMETER_NAME, locale);
			}
			return uriBuilder.toString();
		} catch (URISyntaxException ex) {
			log.error(ex);
			return null;
		}
	}

	@Override
	public GetAllReportsResponse getReports(final int from, final int size) {
		GetAllReportsResponse reportsResponse = new GetAllReportsResponse();
		List<ReportInfoDto> reports = reportDataService.getReports(from, size);
		reportsResponse.setReports(reports);
		return reportsResponse;
	}

	@Override
	public Integer getReportCount() {
		return reportDataService.getReportCount();

	}

	@Override
	public GetAllReportsResponse getReportsByUserId(String requestorId) {
		GetAllReportsResponse reportsResponse = new GetAllReportsResponse();
		List<ReportInfoDto> reports = reportDataService.getAllReports(requestorId);
		reportsResponse.setReports(reports);
		return reportsResponse;
	}

	@Override
	public Response createOrUpdateReportInfo(ReportInfoDto report) {
		Response response = new Response();
		String reportId = null;

		try {
			if ( !validateCreateOrUpdateInternal(report, response) ) {
				return response;
			}
			reportId = reportDataService.createOrUpdateReportInfo(report);

		} catch (Throwable t) {
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(ResponseCode.INTERNAL_ERROR);
			response.setErrorText(t.getMessage());
			return response;
		}
		response.setResponseValue(reportId);
		response.setStatus(ResponseStatus.SUCCESS);

		return response;
	}

	@Override
	public Response validateUpdateReportInfo(ReportInfoDto report) {
		Response response = new Response();
		validateCreateOrUpdateInternal(report, response);
		return response;
	}

	private boolean validateCreateOrUpdateInternal(ReportInfoDto report, Response response) {
		response.setStatus(ResponseStatus.FAILURE);

		if (report == null) {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Parameter 'report' is not defined");
			return false;
		}

		if (StringUtils.isBlank(report.getReportName())) {
			response.setErrorCode(ResponseCode.REPORT_NAME_NOT_SET);
			return false;
		}
		if (StringUtils.isBlank(report.getReportDataSource())) {
			response.setErrorCode(ResponseCode.REPORT_DATASOURCE_NOT_SET);
			return false;
		}
		if (StringUtils.isBlank(report.getReportUrl())) {
			response.setErrorCode(ResponseCode.REPORT_URL_NOT_SET);
			return false;
		}

		// validate unique name
		final ReportInfoDto found = reportDataService.getReportByName( report.getReportName() );
		if (found != null && !found.getReportId().equals( report.getReportId() ) ) {
			response.setErrorCode(ResponseCode.NAME_TAKEN);
			return false;
		}

		// validate built-in report name
		if ( report.getReportId() != null ) {
			final ReportInfoDto reportDto = reportDataService.getReport( report.getReportId() );
			if (reportDto.getIsBuiltIn() && !reportDto.getReportName().equals(report.getReportName())) {
				response.setErrorCode(ResponseCode.READONLY);
				return false;
			}
		}
		response.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	@Override
	public Response createOrUpdateReportInfoParam(final ReportCriteriaParamDto reportParam) {
		log.debug("In createOrUpdateReportInfoParam:" + reportParam);
		Response response = new Response();
		String paramId = null;

		if (reportParam != null) {
			try {

				if (StringUtils.isBlank(reportParam.getName())) {

					response.setErrorCode(
							ResponseCode.REPORT_PARAM_NAME_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.REPORT_PARAM_NAME_NOT_SET.toString());
					return response;
				}
				if (StringUtils.isBlank(reportParam.getTypeId())) {

					response.setErrorCode(
							ResponseCode.REPORT_PARAM_TYPE_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.REPORT_PARAM_TYPE_NOT_SET.toString());
					return response;

				}
				final ReportCriteriaParamDto found = reportDataService
						.getReportParameterByName(reportParam.getReportId(),
								reportParam.getName());
				if (found != null) {
					if (StringUtils.isBlank(reportParam.getId())) {


						response.setErrorCode(
								ResponseCode.NAME_TAKEN);
						response.setStatus(ResponseStatus.FAILURE);
						response.setErrorText(ResponseCode.NAME_TAKEN.toString());
						return response;
					}

					if (StringUtils.isNotBlank(reportParam.getId())
							&& !reportParam.getId().equals(found.getId())) {
						response.setErrorCode(
								ResponseCode.NAME_TAKEN);
						response.setStatus(ResponseStatus.FAILURE);
						response.setErrorText(ResponseCode.NAME_TAKEN.toString());
						return response;
					}
				}

				paramId = reportDataService.createOrUpdateReportParamInfo(reportParam);

			} catch (Throwable t) {
				log.error("error while saving:" + t);
				response.setErrorText(t.getMessage());

				return response;
			}
			response.setResponseValue(paramId);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: report=" + reportParam);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportParametersResponse getReportParametersByReportId(String reportId) {
		GetReportParametersResponse response = new GetReportParametersResponse();
		if (!StringUtils.isEmpty(reportId)) {
			List<ReportCriteriaParamDto> params = reportDataService.getReportParametersByReportId(reportId);
			response.setParameters(params);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId=" + reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportParametersResponse getReportParametersByReportName(String reportName) {
		GetReportParametersResponse response = new GetReportParametersResponse();
		if (!StringUtils.isEmpty(reportName)) {
			List<ReportCriteriaParamDto> params = reportDataService.getReportParametersByReportName(reportName);
			response.setParameters(params);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportName=" + reportName);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportInfoResponse getReportByName(String reportName) {
		GetReportInfoResponse response = new GetReportInfoResponse();
		if (!StringUtils.isEmpty(reportName)) {
			ReportInfoDto reportInfo = reportDataService.getReportByName(reportName);
			response.setReport(reportInfo);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportName=" + reportName);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportInfoResponse getReport(String reportId) {
		GetReportInfoResponse response = new GetReportInfoResponse();
		if (!StringUtils.isEmpty(reportId)) {
			ReportInfoDto reportInfo = reportDataService.getReport(reportId);
			response.setReport(reportInfo);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId=" + reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response deleteReportParam(String reportParamId) {
		Response response = new Response();
		if (!StringUtils.isEmpty(reportParamId)) {
			try {
				reportDataService.deleteReportParam(reportParamId);
				response.setStatus(ResponseStatus.SUCCESS);
			} catch (Throwable t) {
				log.error("error while deleting report param" + t);
				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.SQL_EXCEPTION);
				response.setErrorText(t.getMessage());
				return response;
			}

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportParamId=" + reportParamId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;

	}

	@Override
	public Response deleteReport(String reportId) {
		Response response = new Response();
		if (!StringUtils.isEmpty(reportId)) {

			try {
				if (validateDelete(reportId, response)) {
					reportDataService.deleteReport(reportId);
					response.setStatus(ResponseStatus.SUCCESS);
				}
			} catch (Throwable t) {
				log.error("Can't delete report. " + t);
				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.INTERNAL_ERROR);
				response.setErrorText(t.getMessage());
				return response;
			}

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("ReportId is null or empty");
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	private boolean validateDelete(String reportId, Response response) {
		ReportInfoDto report = reportDataService.getReport(reportId);
		if (report == null) {
			response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
			response.setErrorText("Report does not exist");
			response.setStatus(ResponseStatus.FAILURE);
			return false;
		}
		if (report.getIsBuiltIn()) {
			response.setErrorCode(ResponseCode.PERMISSION_EXCEPTION);
			response.setErrorText("Built-in report can not be deleted");
			response.setStatus(ResponseStatus.FAILURE);
			return false;
		}
		return true;
	}

	@Override
	public GetReportParameterTypesResponse getReportParameterTypes() {
		GetReportParameterTypesResponse response = new GetReportParameterTypesResponse();
		List<ReportParamTypeDto> parameterTypes = reportDataService.getReportParameterTypes();
		response.setTypes(parameterTypes);
		response.setStatus(ResponseStatus.SUCCESS);
		return response;
	}

	@Override
	public GetReportParameterMetaTypesResponse getReportParameterMetaTypes() {
		GetReportParameterMetaTypesResponse response = new GetReportParameterMetaTypesResponse();
		List<ReportParamMetaTypeDto> metaTypes = reportDataService.getReportParamMetaTypes();
		response.setTypes(metaTypes);
		response.setStatus(ResponseStatus.SUCCESS);
		return response;
	}

	@Override
	public GetAllSubscribedReportsResponse getSubscribedReports() {
		List<ReportSubscriptionDto> reports = reportDataService.getAllSubscribedReports();
		GetAllSubscribedReportsResponse reportsResponse = new GetAllSubscribedReportsResponse();
		reportsResponse.setReports(reports);
		return reportsResponse;
	}

	@Override
	public GetSubCriteriaParamReportResponse getSubscribedReportParametersByReportId(String reportId) {
		GetSubCriteriaParamReportResponse response = new GetSubCriteriaParamReportResponse();
		if (!StringUtils.isEmpty(reportId)) {
			List<ReportSubCriteriaParamDto> params = reportDataService.getSubReportParametersByReportId(reportId);
			response.setParameters(params);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId=" + reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public List<ReportCriteriaParamDto> getAllReportCriteriaParam(){
		return reportDataService.getAllReportParameters();
	}

	@Override
	public Response createOrUpdateSubscribedReportInfo(ReportSubscriptionDto reportSubscriptionDto,
													   List<ReportSubCriteriaParamDto> parameters) {
		Response response = new Response();
		String reportId = null;
		if (reportSubscriptionDto != null) {
			try {

				if(StringUtils.isBlank(reportSubscriptionDto.getReportName())){
					response.setErrorCode(
							ResponseCode.SUBSCRIBED_NAME_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_NAME_NOT_SET.toString());
					return response;
				}

				if(StringUtils.isBlank(reportSubscriptionDto.getDeliveryMethod())){

					response.setErrorCode(
							ResponseCode.SUBSCRIBED_DELIVERY_METHOD_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_DELIVERY_METHOD_NOT_SET.toString());
					return response;
				}

				if(StringUtils.isBlank(reportSubscriptionDto.getDeliveryAudience())){

					response.setErrorCode(
							ResponseCode.SUBSCRIBED_DELIVERY_AUDIENCE_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_DELIVERY_AUDIENCE_NOT_SET.toString());
					return response;
				}

				if(StringUtils.isBlank(reportSubscriptionDto.getDeliveryFormat())){

					response.setErrorCode(
							ResponseCode.SUBSCRIBED_DELIVERY_FORMAT_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_DELIVERY_FORMAT_NOT_SET.toString());
					return response;
				}

				reportId = reportDataService.createOrUpdateSubscribedReportInfo(reportSubscriptionDto);

			} catch (Throwable t) {
				response.setErrorText(t.getMessage());
				return response;
			}
			response.setResponseValue(reportId);
			response.setStatus(ResponseStatus.SUCCESS);

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response deleteSubscribedReport(String reportId) {
		Response response = new Response();
		if (!StringUtils.isEmpty(reportId)) {
			try {
				reportDataService.deleteSubscribedReport(reportId);
				response.setStatus(ResponseStatus.SUCCESS);
			} catch (Throwable t) {
				log.error("error while deleting report" + t);
				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.SQL_EXCEPTION);
				response.setErrorText(t.getMessage());
				return response;
			}

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId="
					+ reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Integer getSubscribedReportCount() {
		// TODO Auto-generated method stub
		return reportDataService.getSubscribedReportCount();
	}

	@Override
	public GetSubscribedReportResponse getSubscribedReportById(String reportId) {

		GetSubscribedReportResponse response = new GetSubscribedReportResponse();
		if (!StringUtils.isEmpty(reportId)) {
			ReportSubscriptionDto reportSubscription = reportDataService.getSubscriptionReportById(reportId);
			response.setReport(reportSubscription);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId="
					+ reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;

	}

	@Override
	public GetAllSubCriteriaParamReportsResponse getSubCriteriaParamReports() {
		GetAllSubCriteriaParamReportsResponse reportsResponse = new GetAllSubCriteriaParamReportsResponse();
		List<ReportSubCriteriaParamDto> reports = reportDataService.getAllSubCriteriaParamReports();
		reportsResponse.setReports(reports);
		return reportsResponse;
	}

	@Override
	public GetAllSubCriteriaParamReportsResponse getAllSubCriteriaParamReport(String reportId) {
		GetAllSubCriteriaParamReportsResponse reportsResponse = new GetAllSubCriteriaParamReportsResponse();
		List<ReportSubCriteriaParamDto> reports = reportDataService.getAllSubCriteriaParamReport(reportId);
		reportsResponse.setReports(reports);
		return reportsResponse;
	}

	@Override
	public Integer getSubCriteriaParamReportCount() {
		// TODO Auto-generated method stub
		return reportDataService.getSubCriteriaParamReportCount();
	}

	@Override
	public Response deleteSubCriteriaParamReport(String reportId) {
		Response response = new Response();
		if (!StringUtils.isEmpty(reportId)) {
			try {
				reportDataService.deleteSubCriteriaParamReport(reportId);
				response.setStatus(ResponseStatus.SUCCESS);
			} catch (Throwable t) {
				log.error("error while deleting report" + t);
				response.setStatus(ResponseStatus.FAILURE);
				response.setErrorCode(ResponseCode.SQL_EXCEPTION);
				response.setErrorText(t.getMessage());
				return response;
			}

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId="
					+ reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response createOrUpdateSubCriteriaParam(final ReportSubCriteriaParamDto subCriteriaParamReport) {
		log.debug("In createOrUpdateSubCriteriaParam:" + subCriteriaParamReport);
		Response response = new Response();
		String paramId = null;

		if (subCriteriaParamReport != null) {
			try {


				if(StringUtils.isBlank(subCriteriaParamReport.getId())){

					response.setErrorCode(
							ResponseCode.SUBSCRIBED_ID_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_ID_NOT_SET.toString());
					return response;
				}

				if(StringUtils.isBlank(subCriteriaParamReport.getValue())){

					response.setErrorCode(
							ResponseCode.SUBSCRIBED_VALUE_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.SUBSCRIBED_VALUE_NOT_SET.toString());
					return response;
				}

				paramId = reportDataService.createOrUpdateSubCriteriaParamReport(subCriteriaParamReport);
			}
			catch (Throwable e) {
				log.error("error while saving:" + e);
				response.setErrorText(e.getMessage());
				return response;
			}
			response.setResponseValue(paramId);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: report=" + subCriteriaParamReport);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response runSubscription(String reportId) {
		final ReportSubscriptionDto reportSubscription = reportDataService.getSubscriptionReportById(reportId);

		Response response = new Response();
		if (reportSubscription != null) {
			try {
				jmsTemplate.send("subsQueue", new MessageCreator() {
					public javax.jms.Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(reportSubscription);
					}
				});
				response.setStatus(ResponseStatus.SUCCESS);
				return response;
			} catch (JmsException e) {
			}
		}
		response.setStatus(ResponseStatus.FAILURE);
		return response;
	}

	@Override
	public Response runAllActiveSubscriptions() {
		final List<ReportSubscriptionDto> reportSubscriptions = reportDataService.getAllActiveSubscribedReports();

		for(final ReportSubscriptionDto reportSubscription : reportSubscriptions) {

			if (reportSubscription != null) {
				try {
					jmsTemplate.send("subsQueue", new MessageCreator() {
						public javax.jms.Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(reportSubscription);
						}
					});
				} catch (JmsException e) {
					log.error("Failed to schedule report generation ", e);
				}
			}
		}
		return new Response(ResponseStatus.SUCCESS);
	}
}
