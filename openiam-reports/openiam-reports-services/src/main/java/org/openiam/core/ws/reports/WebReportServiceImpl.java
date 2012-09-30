package org.openiam.core.ws.reports;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.core.dto.reports.ReportDto;
import org.openiam.core.dto.reports.ReportParameterDto;
import org.openiam.core.service.reports.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportWS")
@WebService(endpointInterface = "org.openiam.core.ws.reports.WebReportService",
        targetNamespace = "urn:idm.openiam.org/ws/reports/service",
        portName = "ReportServicePort",
        serviceName = "ReportService")
public class WebReportServiceImpl implements WebReportService {
    protected final Log log = LogFactory.getLog(WebReportServiceImpl.class);

    @Autowired
    private ReportDataService reportDataService;

    @Override
    public GetReportDataResponse executeQuery(final String reportName, final HashMap<String, String> queryParams) {
        GetReportDataResponse response = new GetReportDataResponse();
        if (!StringUtils.isEmpty(reportName)) {
            try {
                GetReportDataResponse.GetInfoByReportNameResult getInfoByReportNameResult = new GetReportDataResponse.GetInfoByReportNameResult();
                getInfoByReportNameResult.setContent(reportDataService.getReportData(reportName, queryParams));
                response.setGetInfoByReportNameResult(getInfoByReportNameResult);
                response.setStatus(ResponseStatus.SUCCESS);
            } catch (Throwable ex) {
                response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                response.setErrorText(ex.getMessage());
                response.setStatus(ResponseStatus.FAILURE);
            }
        } else {
            response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
            response.setErrorText("Invalid parameter list: reportName=" + reportName);
            response.setStatus(ResponseStatus.SUCCESS);
        }
        return response;
    }

    @Override
    public GetAllReportsResponse getReports() {
        List<ReportQuery> reports = reportDataService.getAllReports();
        GetAllReportsResponse reportsResponse = new GetAllReportsResponse();
        List<ReportDto> reportDtos = new LinkedList<ReportDto>();
        for (ReportQuery reportQuery : reports) {
            ReportDto reportDto = new ReportDto();
            reportDto.setReportName(reportQuery.getReportName());
            reportDto.setReportUrl(reportQuery.getReportFilePath());
            reportDto.setParams(reportQuery.getParamsList());
            reportDto.setRequiredParams(reportQuery.getRequiredParamsList());
            reportDtos.add(reportDto);
        }
        reportsResponse.setReports(reportDtos);
        return reportsResponse;
    }

    @Override
    public GetReportParametersResponse getParametersByReport(@WebParam(name = "reportName", targetNamespace = "") String reportName) {
        ReportQuery reportQuery = reportDataService.getReportByName(reportName);
        GetReportParametersResponse getReportParametersResponse = new GetReportParametersResponse();
        List<String> params = reportQuery.getParamsList();
        List<String> requiredParams = reportQuery.getRequiredParamsList();
        List<ReportParameterDto> parameterDtoList = new LinkedList<ReportParameterDto>();
        for (String param : params) {
            ReportParameterDto parameterDto = new ReportParameterDto();
            parameterDto.setName(param);
            parameterDto.setLabel(param);
            parameterDto.setRequired(requiredParams.contains(param));
            parameterDtoList.add(parameterDto);
        }
        getReportParametersResponse.setParameters(parameterDtoList);
        return getReportParametersResponse;
    }
}
