package org.openiam.srvc.reports.ds.ws;

import java.util.HashMap;
import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.srvc.reports.ds.service.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportWS")
@WebService(endpointInterface = "org.openiam.srvc.reports.ds.ws.WebReportDataService",
		targetNamespace = "urn:idm.openiam.org/srvc/reports/service",
		portName = "ReportDataServicePort",
		serviceName = "ReportService")
public class WebReportDataServiceImpl implements WebReportDataService {
    protected final Log log = LogFactory.getLog(WebReportDataServiceImpl.class);

    @Autowired
    private ReportDataService reportDataService;

    @Override
    public ReportQueryListResponse executeQuery(final String reportName, final HashMap<String, String> queryParams) {
        ReportQueryListResponse response;
        if(!StringUtils.isEmpty(reportName)){
            response = new ReportQueryListResponse(ResponseStatus.SUCCESS);
            try {
                response.setRowList(reportDataService.getReportData(reportName, queryParams));
            } catch(Throwable ex) {
                response = new ReportQueryListResponse(ResponseStatus.FAILURE);
                response.setErrorText(ex.getMessage());
            }
        } else {
            response = new ReportQueryListResponse(ResponseStatus.FAILURE);
            response.setErrorText("Invalid parameter list: reportName="+reportName);
        }
        return response;
    }
}
