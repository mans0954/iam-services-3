package org.openiam.core.ws.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.core.domain.reports.ReportQuery;
import org.openiam.core.dto.reports.ReportDataDto;
import org.openiam.core.dto.reports.ReportDto;
import org.openiam.core.dto.reports.ReportParameterDto;
import org.openiam.core.dto.reports.ReportRow;
import org.openiam.core.dto.reports.ReportTable;
import org.openiam.core.service.reports.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportWS")
@WebService(endpointInterface = "org.openiam.core.ws.reports.WebReportService",
        targetNamespace = "urn:idm.openiam.org/ws/reports/service",
        portName = "ReportServicePort",
        serviceName = "ReportService")
public class WebReportServiceImpl implements WebReportService {
    protected final Log LOG = LogFactory.getLog(WebReportServiceImpl.class);

    @Autowired
    private ReportDataService reportDataService;

    @Override
    public GetReportDataResponse executeQuery(final String reportName, final HashMap<String, String> queryParams) {
        GetReportDataResponse response = new GetReportDataResponse();

        ReportDataDto reportDataDto = new ReportDataDto();
        for (int n = 0; n < 2; n++) {
            ReportTable reportTable = new ReportTable();
            reportTable.setName("Table_"+n);
            List<ReportRow> reportRowList = new LinkedList<ReportRow>();
            for (int i = 0; i < 10; i++) {
                ReportRow reportRow1 = new ReportRow();
                List<ReportRow.ReportColumn> columns = new LinkedList<ReportRow.ReportColumn>();
                for (int j = 0; j < 4; j++) {
                    ReportRow.ReportColumn column1 = new ReportRow.ReportColumn();
                    column1.setName("column" + j);
                    column1.setValue("TestValue" + j);
                    columns.add(column1);
                }

                reportRow1.setColumn(columns);
                reportRowList.add(reportRow1);
            }
            reportTable.setRow(reportRowList);
            reportDataDto.getTables().add(reportTable);
        }
        reportDataDto.getParameters().put("createdDate", new Date().toString());
        response.setReportDataDto(reportDataDto);
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
