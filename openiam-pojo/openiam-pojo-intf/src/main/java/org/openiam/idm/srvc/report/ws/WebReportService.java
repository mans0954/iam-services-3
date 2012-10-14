package org.openiam.idm.srvc.report.ws;

import java.util.HashMap;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openiam.base.ws.PropertyMapAdapter;
import org.openiam.base.ws.Response;

@WebService(targetNamespace = "urn:idm.openiam.org/idm/srvc/report/ws/service", name = "ReportService")
public interface WebReportService {

    @WebMethod
    GetReportDataResponse executeQuery(@WebParam(name = "reportName", targetNamespace = "") String reportName, @WebParam(name = "queryParams", targetNamespace = "") @XmlJavaTypeAdapter(PropertyMapAdapter.class) HashMap<String, String> queryParams);

    @WebMethod
    GetAllReportsResponse getReports();

    @WebMethod
    Response createOrUpdateReportInfo(@WebParam(name = "reportName", targetNamespace = "") String reportName, @WebParam(name = "reportDataSource", targetNamespace = "") String reportDataSource, @WebParam(name = "reportUrl", targetNamespace = "") String reportUrl);

}
