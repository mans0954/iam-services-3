package org.openiam.srvc.reports.ds.ws;

import java.util.HashMap;
import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
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
    public GetReportByNameResponse executeQuery(final String reportName, final HashMap<String, String> queryParams) {
        GetReportByNameResponse response = new GetReportByNameResponse();
        if(!StringUtils.isEmpty(reportName)){
            try {
                GetReportByNameResponse.GetInfoByReportNameResult getInfoByReportNameResult = new GetReportByNameResponse.GetInfoByReportNameResult();
                getInfoByReportNameResult.setContent(reportDataService.getReportData(reportName, queryParams));
                response.setGetInfoByReportNameResult(getInfoByReportNameResult);
                response.setStatus(ResponseStatus.SUCCESS);
            } catch(Throwable ex) {
                response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                response.setErrorText(ex.getMessage());
                response.setStatus(ResponseStatus.FAILURE);
            }
        } else {
            response.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
            response.setErrorText("Invalid parameter list: reportName="+reportName);
            response.setStatus(ResponseStatus.SUCCESS);
        }
        return response;
    }
}
