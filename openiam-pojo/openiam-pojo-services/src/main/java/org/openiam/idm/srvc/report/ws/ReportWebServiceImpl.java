package org.openiam.idm.srvc.report.ws;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.ReportCriteriaParamDozerConverter;
import org.openiam.dozer.converter.ReportSubCriteriaParamDozerConverter;
import org.openiam.dozer.converter.ReportInfoDozerConverter;
import org.openiam.dozer.converter.ReportSubscriptionDozerConverter;
import org.openiam.dozer.converter.ReportParamTypeDozerConverter;
import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.idm.srvc.report.domain.ReportParamTypeEntity;
import org.openiam.idm.srvc.report.dto.ReportCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportDataDto;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportParamTypeDto;
import org.openiam.idm.srvc.report.service.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Autowired
	private ReportInfoDozerConverter reportInfoDozerConverter;
	@Autowired
	private ReportSubscriptionDozerConverter reportSubscriptionDozerConverter;
	@Autowired
	private ReportCriteriaParamDozerConverter criteriaParamDozerConverter;
	@Autowired
	private ReportSubCriteriaParamDozerConverter criteriaSubParamDozerConverter;
	@Autowired
	private ReportParamTypeDozerConverter paramTypeDozerConverter;
	@Autowired
	private ReportParamMetaTypeDozerConverter paramMetaTypeDozerConverter;
	@Autowired
	private ReportDataService reportDataService;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    @Qualifier(value = "subsQueue")
    private Queue queue;

	@Override
	public GetReportDataResponse executeQuery(final String reportName,
			final HashMap<String, String> queryParams) {
		GetReportDataResponse response = new GetReportDataResponse();
		if (!StringUtils.isEmpty(reportName)) {
			try {
				ReportDataDto reportDataDto = reportDataService.getReportData(
						reportName, queryParams);

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
					+ reportName);
			response.setStatus(ResponseStatus.SUCCESS);
		}

		return response;
	}

    @Override
    public String getReportUrl(final String reportName, final HashMap<String, String> queryParams,
                               final String taskName, final String reportBaseUrl) {
        try {
            ReportInfoEntity report = reportDataService.getReportByName(reportName);
            if (report == null) {
                log.debug("Report couldn't be found. Report name = " + reportName);
                return null;
            }
            String taskPath = StringUtils.isNotBlank(taskName) ? taskName : DEFAULT_REPORT_TASK;
            String reportDesignName = report.getReportUrl();
            URIBuilder uriBuilder = new URIBuilder(reportBaseUrl);
            uriBuilder.setPath(uriBuilder.getPath() + taskPath);
            uriBuilder.setParameter(REPORT_PARAMETER_NAME, reportDesignName);
            for (Map.Entry<String, String> entry : queryParams.entrySet()  ) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            return uriBuilder.toString();
        } catch (URISyntaxException ex) {
            log.error(ex);
            return null;
        }
    }

	@Override
	public GetAllReportsResponse getReports(final int from, final int size) {
		List<ReportInfoEntity> reports = reportDataService.getAllReports(from,
				size);
		GetAllReportsResponse reportsResponse = new GetAllReportsResponse();
		List<ReportInfoDto> reportDtos = new LinkedList<ReportInfoDto>();
		if (reports != null) {
			reportDtos = reportInfoDozerConverter.convertToDTOList(reports,
					false);
		}
		reportsResponse.setReports(reportDtos);
		return reportsResponse;
	}

	@Override
	public Integer getReportCount() {
		return reportDataService.getReportCount();

	}
	
	@Override
	public List<ReportInfoDto> getAllReportsInfo() {
		List<ReportInfoDto> reportInfo=reportInfoDozerConverter.convertToDTOList(reportDataService.getAllReports(),false);
		return reportInfo;

	}


	@Override
	public Response createOrUpdateReportInfo(
			@WebParam(name = "report", targetNamespace = "") ReportInfoDto report) {
		Response response = new Response();
		ReportInfoEntity entity = null;

		if (report != null) {
			try {

				if (StringUtils.isBlank(report.getReportName())) {
					response.setErrorCode(
							ResponseCode.REPORT_NAME_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.REPORT_NAME_NOT_SET.toString());
					return response;
				}
				if (StringUtils.isBlank(report.getReportDataSource())) {
					
					response.setErrorCode(
							ResponseCode.REPORT_DATASOURCE_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.REPORT_DATASOURCE_NOT_SET.toString());
					return response;
				}
				if (StringUtils.isBlank(report.getReportUrl())) {
					
					response.setErrorCode(
							ResponseCode.REPORT_URL_NOT_SET);
					response.setStatus(ResponseStatus.FAILURE);
					response.setErrorText(ResponseCode.REPORT_URL_NOT_SET.toString());
					return response;
				}
				final ReportInfoEntity found = reportDataService
						.getReportByName(report.getReportName());
				if (found != null) {
					if (StringUtils.isBlank(report.getReportId())) {
						System.out.println("Call=1");
						response.setErrorCode(
								ResponseCode.NAME_TAKEN);
						response.setStatus(ResponseStatus.FAILURE);
						response.setErrorText(ResponseCode.NAME_TAKEN.toString());
						return response;
					}

					if (StringUtils.isNotBlank(report.getReportId())
							&& !report.getReportId()
									.equals(found.getReportId())) {
						System.out.println("Call=2");
						response.setErrorCode(
								ResponseCode.NAME_TAKEN);
						response.setStatus(ResponseStatus.FAILURE);
						response.setErrorText(ResponseCode.NAME_TAKEN.toString());
						return response;
					}
				}
               
				entity = reportInfoDozerConverter.convertToEntity(report, true);
               
				entity = reportDataService.createOrUpdateReportInfo(entity);
			} catch (Throwable t) {			
				response.setErrorText(t.getMessage());
				return response;
			}
			
			response.setResponseValue(entity.getReportId());
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: report=" + report);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response createOrUpdateReportInfoParam(
			@WebParam(name = "reportParam", targetNamespace = "") final ReportCriteriaParamDto reportParam) {
		log.debug("In createOrUpdateReportInfoParam:" + reportParam);
		Response response = new Response();
		ReportCriteriaParamEntity entity = null;

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
				final ReportCriteriaParamEntity found = reportDataService
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

				entity = criteriaParamDozerConverter.convertToEntity(
						reportParam, true);
				log.debug("In createOrUpdateReportParamInfo, converted entity:"
						+ entity);

				entity = reportDataService
						.createOrUpdateReportParamInfo(entity);
			} catch (Throwable t) {
				log.error("error while saving:" + t);
				response.setErrorText(t.getMessage());
				
				return response;
			}
			response.setResponseValue(entity.getId());
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: report="
					+ reportParam);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportParametersResponse getReportParametersByReportId(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {
		GetReportParametersResponse response = new GetReportParametersResponse();
		if (!StringUtils.isEmpty(reportId)) {
			List<ReportCriteriaParamEntity> params = reportDataService
					.getReportParametersByReportId(reportId);
			List<ReportCriteriaParamDto> paramsDtos = new LinkedList<ReportCriteriaParamDto>();
			if (params != null) {
				paramsDtos = criteriaParamDozerConverter.convertToDTOList(
						params, false);
			}
			response.setParameters(paramsDtos);
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
	public GetReportParametersResponse getReportParametersByReportName(
			@WebParam(name = "reportName", targetNamespace = "") String reportName) {
		GetReportParametersResponse response = new GetReportParametersResponse();
		if (!StringUtils.isEmpty(reportName)) {
			List<ReportCriteriaParamEntity> params = reportDataService
					.getReportParametersByReportName(reportName);
			List<ReportCriteriaParamDto> paramsDtos = new LinkedList<ReportCriteriaParamDto>();
			if (params != null) {
				paramsDtos = criteriaParamDozerConverter.convertToDTOList(
						params, false);
			}
			response.setParameters(paramsDtos);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportName="
					+ reportName);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportInfoResponse getReportByName(
			@WebParam(name = "reportName", targetNamespace = "") String reportName) {
		GetReportInfoResponse response = new GetReportInfoResponse();
		if (!StringUtils.isEmpty(reportName)) {
			ReportInfoEntity reportInfoEntity = reportDataService
					.getReportByName(reportName);
			ReportInfoDto reportInfoDto = reportInfoDozerConverter
					.convertToDTO(reportInfoEntity, true);
			response.setReport(reportInfoDto);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportName="
					+ reportName);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public GetReportInfoResponse getReport(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {
		GetReportInfoResponse response = new GetReportInfoResponse();
		if (!StringUtils.isEmpty(reportId)) {
			ReportInfoEntity reportInfoEntity = reportDataService
					.getReport(reportId);
			log.debug("In getReport:" + reportInfoEntity);
			ReportInfoDto reportInfoDto = reportInfoDozerConverter
					.convertToDTO(reportInfoEntity, true);
			log.debug("Converted dto:" + reportInfoDto);
			response.setReport(reportInfoDto);
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
	public Response deleteReportParam(
			@WebParam(name = "reportParamId", targetNamespace = "") String reportParamId) {
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
			response.setErrorText("Invalid parameter list: reportParamId="
					+ reportParamId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;

	}

	@Override
	public Response deleteReport(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {
		Response response = new Response();
		if (!StringUtils.isEmpty(reportId)) {
			try {
				reportDataService.deleteReport(reportId);
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
	public GetReportParameterTypesResponse getReportParameterTypes() {
		GetReportParameterTypesResponse response = new GetReportParameterTypesResponse();
		List<ReportParamTypeEntity> paramTypeEntities = reportDataService
				.getReportParameterTypes();
		List<ReportParamTypeDto> reportParamTypeDtos = new LinkedList<ReportParamTypeDto>();
		if (paramTypeEntities != null && paramTypeEntities.size() > 0) {
			reportParamTypeDtos = paramTypeDozerConverter.convertToDTOList(
					paramTypeEntities, false);
		}
		response.setTypes(reportParamTypeDtos);
		response.setStatus(ResponseStatus.SUCCESS);
		return response;
	}

    @Override
    public GetReportParameterMetaTypesResponse getReportParameterMetaTypes() {
        GetReportParameterMetaTypesResponse response = new GetReportParameterMetaTypesResponse();
        List<ReportParamMetaTypeEntity> metaTypeEntities = reportDataService
                .getReportParamMetaTypes();
        List<ReportParamMetaTypeDto> metaTypeDtos = new LinkedList<ReportParamMetaTypeDto>();
        if (metaTypeEntities != null && metaTypeEntities.size() > 0) {
            metaTypeDtos = paramMetaTypeDozerConverter.convertToDTOList(
                    metaTypeEntities, false);
        }
        response.setTypes(metaTypeDtos);
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

	@Override
	public GetAllSubscribedReportsResponse getSubscribedReports() {
		List<ReportSubscriptionEntity> reports = reportDataService
				.getAllSubscribedReports();
		GetAllSubscribedReportsResponse reportsResponse = new GetAllSubscribedReportsResponse();
		List<ReportSubscriptionDto> reportDtos = new LinkedList<ReportSubscriptionDto>();
		if (reports != null) {
			reportDtos = reportSubscriptionDozerConverter.convertToDTOList(
					reports, false);
		}
		reportsResponse.setReports(reportDtos);
		return reportsResponse;
	}

    @Override
    public GetSubCriteriaParamReportResponse getSubscribedReportParametersByReportId(
            @WebParam(name = "reportId", targetNamespace = "") String reportId) {
        GetSubCriteriaParamReportResponse response = new GetSubCriteriaParamReportResponse();
        if (!StringUtils.isEmpty(reportId)) {
            List<ReportSubCriteriaParamEntity> params = reportDataService
                    .getSubReportParametersByReportId(reportId);
            List<ReportSubCriteriaParamDto> paramsDtos = new LinkedList<ReportSubCriteriaParamDto>();
            if (params != null) {
                paramsDtos = criteriaSubParamDozerConverter.convertToDTOList(
                        params, false);
            }
            response.setParameters(paramsDtos);
            response.setStatus(ResponseStatus.SUCCESS);
        } else {
            response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
            response.setErrorText("Invalid parameter list: reportId="
                    + reportId);
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

	public List<ReportCriteriaParamDto> getAllReportCriteriaParam(){
		List<ReportCriteriaParamDto> reportCriteriaParam = criteriaParamDozerConverter.convertToDTOList(reportDataService.getAllReportParameters(), false);
		return reportCriteriaParam;
	}

	@Override
	public Response createOrUpdateSubscribedReportInfo(
			@WebParam(name = "reportSubscriptionDto", targetNamespace = "") ReportSubscriptionDto reportSubscriptionDto,
			@WebParam(name = "parameters", targetNamespace = "") List<ReportSubCriteriaParamDto> parameters) {
		Response response = new Response();
		ReportSubscriptionEntity entity = null;
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
				
				

				entity = reportDataService
						.createOrUpdateSubscribedReportInfo(reportSubscriptionDozerConverter
								.convertToEntity(reportSubscriptionDto, true));


			} catch (Throwable t) {			
				response.setErrorText(t.getMessage());
				return response;
			}
			response.setResponseValue(entity.getReportId());
			response.setStatus(ResponseStatus.SUCCESS);

		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response deleteSubscribedReport(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {
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
	public GetSubscribedReportResponse getSubscribedReportById(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {

		GetSubscribedReportResponse response = new GetSubscribedReportResponse();
		if (!StringUtils.isEmpty(reportId)) {
			ReportSubscriptionEntity reportSubscriptionEntity = reportDataService
					.getSubscriptionReportById(reportId);
			log.debug("In getSubscriptionReport:" + reportSubscriptionEntity);

			ReportSubscriptionDto reportSubscriptionDto = reportSubscriptionDozerConverter
					.convertToDTO(reportSubscriptionEntity, true);

			log.debug("Converted Subscription dto:" + reportSubscriptionDto);

			response.setReport(reportSubscriptionDto);
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
		List<ReportSubCriteriaParamEntity> reports = reportDataService
				.getAllSubCriteriaParamReports();
		GetAllSubCriteriaParamReportsResponse reportsResponse = new GetAllSubCriteriaParamReportsResponse();
		List<ReportSubCriteriaParamDto> reportDtos = new LinkedList<ReportSubCriteriaParamDto>();
		if (reports != null) {
			reportDtos = criteriaSubParamDozerConverter.convertToDTOList(
					reports, false);
		}
		reportsResponse.setReports(reportDtos);
		return reportsResponse;
	}

	@Override
	public GetAllSubCriteriaParamReportsResponse getAllSubCriteriaParamReport(
			@WebParam(name = "reportId", targetNamespace = "") String reportId) {
		List<ReportSubCriteriaParamEntity> reports = reportDataService
				.getAllSubCriteriaParamReport(reportId);
		GetAllSubCriteriaParamReportsResponse reportsResponse = new GetAllSubCriteriaParamReportsResponse();
		List<ReportSubCriteriaParamDto> reportDtos = new LinkedList<ReportSubCriteriaParamDto>();
		if (reports != null) {
			reportDtos = criteriaSubParamDozerConverter.convertToDTOList(
					reports, false);
		}
		reportsResponse.setReports(reportDtos);
		return reportsResponse;
	}

	@Override
	public Integer getSubCriteriaParamReportCount() {
		// TODO Auto-generated method stub
		return reportDataService.getSubCriteriaParamReportCount();
	}
	

	/*@Override
	public GetSubCriteriaParamReportResponse getSubCriteriaParamReportById(
			@WebParam(name = "Id", targetNamespace = "") String reportId) {
		GetSubCriteriaParamReportResponse response = new GetSubCriteriaParamReportResponse();
		if (!StringUtils.isEmpty(reportId)) {
			ReportSubCriteriaParamEntity reportSubCriteriaParamEntity = reportDataService
					.getSubCriteriaParamReportById(reportId);
			log.debug("In getSubCriteriaParamReport:"
					+ reportSubCriteriaParamEntity);

			ReportSubCriteriaParamDto reportSubCriteriaParamDto = criteriaSubParamDozerConverter
					.convertToDTO(reportSubCriteriaParamEntity, true);

			log.debug("Converted Subscription dto:" + reportSubCriteriaParamDto);

			response.setReport(reportSubCriteriaParamDto);
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: reportId="
					+ reportId);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}*/
	
	

	@Override
	public Response deleteSubCriteriaParamReport(
			@WebParam(name = "rscpId", targetNamespace = "") String reportId) {
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
	public Response createOrUpdateSubCriteriaParam(
			@WebParam(name = "subCriteriaParamReport", targetNamespace = "") final ReportSubCriteriaParamDto subCriteriaParamReport) {
		log.debug("In createOrUpdateSubCriteriaParam:" + subCriteriaParamReport);
		Response response = new Response();
		ReportSubCriteriaParamEntity entity = new ReportSubCriteriaParamEntity() ;

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
				 

				entity = criteriaSubParamDozerConverter.convertToEntity(
						subCriteriaParamReport, true);
				log.debug("In createOrUpdateReportParamInfo, converted entity:"
						+ entity);

				entity = reportDataService
						.createOrUpdateSubCriteriaParamReport(entity);
			} 
			catch (Throwable e) {
				log.error("error while saving:" + e);							
				response.setErrorText(e.getMessage());
				return response;
			}
			response.setResponseValue(entity.getRscpId());
			response.setStatus(ResponseStatus.SUCCESS);
		} else {
			response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
			response.setErrorText("Invalid parameter list: report="
					+ subCriteriaParamReport);
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

    @Override
    public Response runSubscription(@WebParam(name = "reportId", targetNamespace = "") String reportId) {
        final ReportSubscriptionEntity reportSubscriptionEntity = reportDataService
                .getSubscriptionReportById(reportId);
        final ReportSubscriptionDto reportSubscriptionDto = reportSubscriptionDozerConverter
                .convertToDTO(reportSubscriptionEntity, true);

        Response response = new Response();
        if (reportSubscriptionDto != null) {
            try {
                jmsTemplate.send(queue, new MessageCreator() {
                    public javax.jms.Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(reportSubscriptionDto);
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
        final List<ReportSubscriptionEntity> reportSubscriptions = reportDataService
                .getAllActiveSubscribedReports();

        for(ReportSubscriptionEntity reportSubscription : reportSubscriptions) {

            final ReportSubscriptionDto reportSubscriptionDto = reportSubscriptionDozerConverter
                    .convertToDTO(reportSubscription, true);

            if (reportSubscriptionDto != null) {
                try {
                    jmsTemplate.send(queue, new MessageCreator() {
                        public javax.jms.Message createMessage(Session session) throws JMSException {
                            return session.createObjectMessage(reportSubscriptionDto);
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
